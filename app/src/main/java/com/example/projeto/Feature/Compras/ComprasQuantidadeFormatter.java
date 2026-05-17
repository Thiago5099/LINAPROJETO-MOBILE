package com.example.projeto.Feature.Compras;

/**
 * Formata quantidade + unidade no mesmo padrão do backend ({@code RefeicaoMapper#formatarIngrediente}).
 */
public final class ComprasQuantidadeFormatter {

    private ComprasQuantidadeFormatter() {}

    /** Ex.: "2 unidades", "150 g", "1 pitada". */
    public static String resumo(double quantidade, String unidade) {
        String qtdTxt = formatarQuantidade(quantidade);
        if (unidade == null || unidade.trim().isEmpty()) {
            return qtdTxt;
        }
        String u = unidade.trim();
        if (u.equalsIgnoreCase("pitada") || u.equalsIgnoreCase("a gosto")) {
            return u;
        }
        return qtdTxt + " " + u;
    }

    private static String formatarQuantidade(double qtd) {
        if (qtd == 0.25) return "1/4";
        if (qtd == 0.5) return "1/2";
        if (qtd == 0.75) return "3/4";
        if (Math.rint(qtd) == qtd) {
            return String.valueOf((long) qtd);
        }
        String s = String.valueOf(qtd).replace('.', ',');
        return s.endsWith(",0") ? s.substring(0, s.length() - 2) : s;
    }
}
