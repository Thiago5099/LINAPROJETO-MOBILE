package com.example.projeto.Feature.Compras;

import android.content.Context;
import android.util.Log;

import com.example.projeto.Feature.Cardapio.CardapioItemPersistido;
import com.example.projeto.Feature.Cardapio.CardapioLocalStore;
import com.example.projeto.Feature.Compras.models.ComprasIngrediente;
import com.example.projeto.Feature.Nutricionistas.RetrofitClient;
import com.example.projeto.Feature.Refeicoes.ApiUiFormatter;
import com.example.projeto.Feature.Refeicoes.IngredienteItemResponse;
import com.example.projeto.Feature.Refeicoes.PeriodoMapeador;
import com.example.projeto.Feature.Refeicoes.RefeicaoApiService;
import com.example.projeto.Feature.Refeicoes.RefeicaoResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Response;

/**
 * Monta a lista de compras consumindo apenas APIs existentes + cardápio local.
 */
public final class ComprasListaCarregador {

    private static final String TAG = "ComprasLista";

    private ComprasListaCarregador() {}

    public static List<ComprasIngrediente> carregar(Context ctx, String authorization, long usuarioId)
            throws IOException {

        ListaComprasApiService api = RetrofitClient.getInstance().create(ListaComprasApiService.class);
        Response<List<ListaComprasResponse>> resp =
                api.gerar(authorization, usuarioId).execute();

        if (resp.isSuccessful() && resp.body() != null) {
            List<ComprasIngrediente> daApi = mapearApi(resp.body());
            if (!daApi.isEmpty()) {
                Log.d(TAG, "Lista via API: " + daApi.size() + " itens");
                return daApi;
            }
            Log.d(TAG, "API retornou lista vazia, tentando cardápio local");
        } else if (!resp.isSuccessful() && resp.code() != 404) {
            throw new IOException("lista-compras HTTP " + resp.code());
        }

        if (CardapioLocalStore.temCardapioSalvo(ctx)) {
            List<ComprasIngrediente> local = montarDoCardapioLocal(ctx, authorization, usuarioId);
            Log.d(TAG, "Lista via cardápio local: " + local.size() + " itens");
            return local;
        }

        return new ArrayList<>();
    }

    private static List<ComprasIngrediente> mapearApi(List<ListaComprasResponse> grupos) {
        List<ComprasIngrediente> out = new ArrayList<>();
        for (ListaComprasResponse grupo : grupos) {
            if (grupo == null || grupo.itens == null) continue;
            String categoria = CategoriaComprasMapeador.paraLabel(grupo.categoria);
            for (ItemListaResponse item : grupo.itens) {
                if (item == null || item.nomeIngrediente == null) continue;
                double qtd = item.quantidade != null ? item.quantidade : 1d;
                String unidade = resolverUnidade(item.nomeIngrediente, item.unidade);
                out.add(new ComprasIngrediente(item.nomeIngrediente, qtd, unidade, categoria));
            }
        }
        return out;
    }

    private static List<ComprasIngrediente> montarDoCardapioLocal(
            Context ctx, String authorization, long usuarioId) throws IOException {

        List<List<CardapioItemPersistido>> semana = CardapioLocalStore.carregarSemana(ctx);
        if (semana == null) {
            return new ArrayList<>();
        }

        Map<String, ComprasIngrediente> mapa = new HashMap<>();
        Map<String, List<IngredienteLinha>> cacheRefeicao = new HashMap<>();

        RefeicaoApiService refeicaoApi = RetrofitClient.getInstance().create(RefeicaoApiService.class);

        for (List<CardapioItemPersistido> dia : semana) {
            if (dia == null) continue;
            for (CardapioItemPersistido item : dia) {
                if (item == null) continue;

                boolean adicionou = false;

                if (item.refeicaoId != null && item.refeicaoId > 0) {
                    String periodo = PeriodoMapeador.uiParaQuery(item.tipo);
                    String chave = item.refeicaoId + "|" + periodo;

                    if (!cacheRefeicao.containsKey(chave)) {
                        cacheRefeicao.put(chave,
                                buscarIngredientesDaApi(refeicaoApi, authorization, usuarioId, item, periodo));
                    }

                    List<IngredienteLinha> linhas = cacheRefeicao.get(chave);
                    if (linhas != null && !linhas.isEmpty()) {
                        for (IngredienteLinha linha : linhas) {
                            mesclar(mapa, linha.nome, linha.quantidade, linha.unidade, linha.categoria);
                        }
                        adicionou = true;
                    }
                }

                if (!adicionou) {
                    adicionarDoTextoLocal(item.ingredientes, mapa);
                }
            }
        }

        return new ArrayList<>(mapa.values());
    }

    private static List<IngredienteLinha> buscarIngredientesDaApi(
            RefeicaoApiService refeicaoApi,
            String authorization,
            long usuarioId,
            CardapioItemPersistido item,
            String periodo) throws IOException {

        Response<RefeicaoResponse> detalhe = refeicaoApi.buscarPorId(
                authorization, item.refeicaoId, usuarioId, periodo).execute();

        if (!detalhe.isSuccessful() || detalhe.body() == null) {
            Log.w(TAG, "Detalhe refeição " + item.refeicaoId + " HTTP " + detalhe.code());
            return new ArrayList<>();
        }

        RefeicaoResponse refeicao = detalhe.body();
        List<IngredienteLinha> linhas = new ArrayList<>();

        if (refeicao.ingredientesDetalhados != null && !refeicao.ingredientesDetalhados.isEmpty()) {
            for (IngredienteItemResponse ing : refeicao.ingredientesDetalhados) {
                if (ing == null) continue;
                String nome = ing.nome != null && !ing.nome.isEmpty()
                        ? ing.nome
                        : (ing.texto != null ? ing.texto : null);
                if (nome == null || nome.isEmpty()) continue;
                double qtd = ing.quantidade != null ? ing.quantidade : 1d;
                String unidade = resolverUnidade(nome, ing.unidade);
                String categoria = IngredienteCatalogoLocal.categoriaLabel(nome);
                linhas.add(new IngredienteLinha(nome, qtd, unidade, categoria));
            }
        } else if (refeicao.ingredientes != null && !refeicao.ingredientes.isEmpty()) {
            for (String linha : refeicao.ingredientes) {
                if (linha == null || linha.trim().isEmpty()) continue;
                String nome = linha.trim();
                linhas.add(new IngredienteLinha(
                        nome, 1d,
                        resolverUnidade(nome, null),
                        IngredienteCatalogoLocal.categoriaLabel(nome)));
            }
        }

        return linhas;
    }

    /** Prioriza unidade da API de refeição; senão usa catálogo local. */
    private static String resolverUnidade(String nomeIngrediente, String unidadeApi) {
        if (unidadeApi != null && !unidadeApi.trim().isEmpty()) {
            return unidadeApi.trim();
        }
        String padrao = IngredienteCatalogoLocal.unidadePadrao(nomeIngrediente);
        return padrao != null ? padrao : "";
    }

    private static final class IngredienteLinha {
        final String nome;
        final double quantidade;
        final String unidade;
        final String categoria;

        IngredienteLinha(String nome, double quantidade, String unidade, String categoria) {
            this.nome = nome;
            this.quantidade = quantidade;
            this.unidade = unidade != null ? unidade : "";
            this.categoria = categoria != null ? categoria : "Outros";
        }
    }

    private static void adicionarDoTextoLocal(String ingredientesTexto,
                                              Map<String, ComprasIngrediente> mapa) {
        if (ingredientesTexto == null || ingredientesTexto.isEmpty()) return;
        if (ApiUiFormatter.VAZIO.equals(ingredientesTexto.trim())) return;

        for (String linha : ComprasIngredienteTextoParser.linhasDeTexto(ingredientesTexto)) {
            mesclar(mapa, linha, 1d,
                    resolverUnidade(linha, null),
                    IngredienteCatalogoLocal.categoriaLabel(linha));
        }
    }

    private static void mesclar(Map<String, ComprasIngrediente> mapa,
                                String nome,
                                double qtd,
                                String unidade,
                                String categoria) {
        String un = unidade != null ? unidade : "";
        String chave = nome.trim().toLowerCase() + "|" + un.toLowerCase();
        ComprasIngrediente existente = mapa.get(chave);
        if (existente != null) {
            existente.setQuantidade(existente.getQuantidade() + qtd);
        } else {
            mapa.put(chave, new ComprasIngrediente(nome.trim(), qtd, un, categoria));
        }
    }
}
