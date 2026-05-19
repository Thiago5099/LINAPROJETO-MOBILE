package com.example.projeto.Feature.Cardapio;

import android.content.Context;
import com.example.projeto.Data.BancoHelper;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Grava no banco local o cardápio montado em {@code CriarCardioMainActivity}
 * (7 dias × até 4 refeições).
 */
public final class CardapioLocalStore {

    private static final String PREFS = "cardapio_local";
    private static final String KEY_JSON = "semana_json";
    private static final Gson GSON = new Gson();

    private CardapioLocalStore() {}

    public static void salvarSemana(Context ctx, List<List<CardapioItemPersistido>> dias) {
        new BancoHelper(ctx).salvarSemana(dias);
        String json = GSON.toJson(dias);
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit()
                .putString(KEY_JSON, json)
                .apply();
    }

    public static void salvarDoMapa(Context ctx, Map<String, List<Refeicao>> map) {
        String[] dias = {"Segunda", "Terça", "Quarta", "Quinta", "Sexta", "Sábado", "Domingo"};
        List<List<CardapioItemPersistido>> semana = new ArrayList<>();
        for (String d : dias) {
            List<Refeicao> refs = map != null ? map.get(d) : null;
            List<CardapioItemPersistido> dia = new ArrayList<>();
            if (refs != null) {
                for (Refeicao r : refs) {
                    dia.add(refeicaoParaItem(r));
                }
            }
            semana.add(dia);
        }
        salvarSemana(ctx, semana);
    }

    static CardapioItemPersistido refeicaoParaItem(Refeicao r) {
        if (r == null || r.prato == null) {
            return new CardapioItemPersistido("", "", "", "", "", "");
        }
        Prato p = r.prato;
        Long id = p.refeicaoId > 0L ? p.refeicaoId : null;
        return new CardapioItemPersistido(
                id,
                r.tipo != null ? r.tipo : "",
                p.nome != null ? p.nome : "",
                p.tempo + " min",
                p.calorias + " kcal",
                p.ingredientes != null ? p.ingredientes : "_",
                p.preparo != null ? p.preparo : "_");
    }

    /**
     * @return lista com 7 entradas (uma por dia); vazia enquanto o usuário não salvar.
     */
    public static List<List<CardapioItemPersistido>> carregarSemana(Context ctx) {
        return new BancoHelper(ctx).carregarSemana();
    }

    public static boolean temCardapioSalvo(Context ctx) {
        return new BancoHelper(ctx).temCardapioSalvo();
    }

    /**
     * Mapa dia da semana → refeições para a UI do cardápio (somente leitura).
     */
    public static Map<String, List<Refeicao>> carregarMapaRefeicoes(Context ctx) {
        String[] dias = {"Segunda", "Terça", "Quarta", "Quinta", "Sexta", "Sábado", "Domingo"};
        Map<String, List<Refeicao>> map = new LinkedHashMap<>();
        List<List<CardapioItemPersistido>> semana = carregarSemana(ctx);
        if (semana == null) {
            for (String d : dias) {
                map.put(d, new ArrayList<>());
            }
            return map;
        }
        for (int i = 0; i < dias.length; i++) {
            List<Refeicao> lista = new ArrayList<>();
            if (i < semana.size() && semana.get(i) != null) {
                for (CardapioItemPersistido it : semana.get(i)) {
                    lista.add(itemParaRefeicao(it));
                }
            }
            map.put(dias[i], lista);
        }
        return map;
    }

    static String tipoParaExibicao(String tipo) {
        if (tipo != null && tipo.toLowerCase().contains("lanche")) {
            return "Lanche";
        }
        return tipo != null ? tipo : "";
    }

    static Refeicao itemParaRefeicao(CardapioItemPersistido it) {
        int tempoMin = parseIntFlex(it.tempo);
        int kcal = parseIntFlex(it.kcal);
        String ing = it.ingredientes != null && !it.ingredientes.isEmpty()
                ? it.ingredientes : "_";
        String prep = it.preparo != null && !it.preparo.isEmpty()
                ? it.preparo : "_";
        long refId = it.refeicaoId != null ? it.refeicaoId : 0L;
        Prato p = new Prato(it.nome != null ? it.nome : "", ing, prep, kcal, tempoMin, refId);
        return new Refeicao(tipoParaExibicao(it.tipo), p);
    }

    private static int parseIntFlex(String s) {
        if (s == null) return 0;
        String d = s.replaceAll("[^0-9]", "");
        if (d.isEmpty()) return 0;
        try {
            return Integer.parseInt(d);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /** Ordem de exibição: café → almoço → lanche → jantar */
    public static int ordemPeriodo(String tipo) {
        if (tipo == null) return 99;
        String t = tipo.toLowerCase();
        if (t.contains("café") || t.contains("cafe")) return 0;
        if (t.contains("almoço") || t.contains("almoco")) return 1;
        if (t.contains("lanche")) return 2;
        if (t.contains("jantar")) return 3;
        return 99;
    }

    public static void ordenarPorPeriodo(List<CardapioItemPersistido> itens) {
        if (itens == null) return;
        Collections.sort(itens, (a, b) ->
                Integer.compare(ordemPeriodo(a.tipo), ordemPeriodo(b.tipo)));
    }
}
