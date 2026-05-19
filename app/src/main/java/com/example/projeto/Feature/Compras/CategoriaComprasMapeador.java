package com.example.projeto.Feature.Compras;

public final class CategoriaComprasMapeador {

    private CategoriaComprasMapeador() {}

    public static String paraLabel(String categoria) {
        if (categoria == null || categoria.isEmpty()) {
            return "Outros";
        }
        switch (categoria) {
            case "FRUTAS_E_VEGETAIS":
                return "Frutas e Vegetais";
            case "LATICINIOS":
                return "Laticínios";
            case "GRAOS_E_CEREAIS":
                return "Grãos e Cereais";
            case "PROTEINAS":
                return "Proteínas";
            case "OUTROS":
            default:
                return "Outros";
        }
    }

    /** Ordem de exibição na lista. */
    public static int ordem(String label) {
        if (label == null) return 99;
        switch (label) {
            case "Frutas e Vegetais":
                return 0;
            case "Laticínios":
                return 1;
            case "Grãos e Cereais":
                return 2;
            case "Proteínas":
                return 3;
            default:
                return 4;
        }
    }
}
