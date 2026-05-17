package com.example.projeto.Feature.Compras;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Categorias e unidades dos ingredientes do catálogo Lina (espelha o seed do backend).
 * Usado no app quando a API não envia esses campos em {@code /lista-compras}.
 */
public final class IngredienteCatalogoLocal {

    private static final Map<String, Entrada> POR_NOME = new HashMap<>();

    static {
        registrar("Ovo", "PROTEINAS", "unidades");
        registrar("Tomate", "FRUTAS_E_VEGETAIS", "unidade");
        registrar("Cebola", "FRUTAS_E_VEGETAIS", "xícara");
        registrar("Pimentão", "FRUTAS_E_VEGETAIS", "xícara");
        registrar("Azeite", "OUTROS", "colher");
        registrar("Iogurte", "LATICINIOS", "pote");
        registrar("Banana", "FRUTAS_E_VEGETAIS", "unidade");
        registrar("Chia", "GRAOS_E_CEREAIS", "colher");
        registrar("Goma de Tapioca", "GRAOS_E_CEREAIS", "colheres");
        registrar("Queijo", "LATICINIOS", "fatia");
        registrar("Leite", "LATICINIOS", "ml");
        registrar("Aveia", "GRAOS_E_CEREAIS", "colheres");
        registrar("Frango", "PROTEINAS", "g");
        registrar("Arroz", "GRAOS_E_CEREAIS", "xícara");
        registrar("Salada (mix)", "FRUTAS_E_VEGETAIS", "xícara");
        registrar("Carne Bovina", "PROTEINAS", "g");
        registrar("Batata Doce", "FRUTAS_E_VEGETAIS", "unidade");
        registrar("Filé de Peixe", "PROTEINAS", "filé");
        registrar("Legumes (mix)", "FRUTAS_E_VEGETAIS", "xícara");
        registrar("Castanhas (mix)", "OUTROS", "g");
        registrar("Maçã", "FRUTAS_E_VEGETAIS", "unidade");
        registrar("Pasta de Amendoim", "OUTROS", "colher");
        registrar("Frutas (mix)", "FRUTAS_E_VEGETAIS", "xícara");
        registrar("Granola", "GRAOS_E_CEREAIS", "colheres");
        registrar("Folhas Verdes", "FRUTAS_E_VEGETAIS", "xícara");
        registrar("Sal", "OUTROS", "pitada");
    }

    private IngredienteCatalogoLocal() {}

    private static void registrar(String nome, String categoriaApi, String unidade) {
        POR_NOME.put(chave(nome), new Entrada(categoriaApi, unidade));
    }

    public static String categoriaLabel(String nomeIngrediente) {
        Entrada e = buscar(nomeIngrediente);
        return e != null
                ? CategoriaComprasMapeador.paraLabel(e.categoriaApi)
                : "Outros";
    }

    /** Unidade padrão do catálogo; {@code null} se desconhecido. */
    public static String unidadePadrao(String nomeIngrediente) {
        Entrada e = buscar(nomeIngrediente);
        return e != null ? e.unidade : null;
    }

    private static Entrada buscar(String nome) {
        if (nome == null || nome.isEmpty()) return null;
        return POR_NOME.get(chave(nome));
    }

    private static String chave(String nome) {
        return nome.trim().toLowerCase(Locale.ROOT);
    }

    private static final class Entrada {
        final String categoriaApi;
        final String unidade;

        Entrada(String categoriaApi, String unidade) {
            this.categoriaApi = categoriaApi;
            this.unidade = unidade;
        }
    }
}
