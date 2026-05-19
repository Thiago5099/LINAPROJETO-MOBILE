package com.example.projeto.Feature.Compras;

/**
 * Formata a quantidade de vezes que um ingrediente aparece nas refeições da semana.
 * Exibe sempre no formato (xN), ex.: (x3), (x5), (x7).
 */
public final class ComprasQuantidadeFormatter {

    private ComprasQuantidadeFormatter() {}

    /** Ex.: "(x3)", "(x5)", "(x7)" */
    public static String resumo(double quantidade, String unidade) {
        long n = Math.round(quantidade);
        if (n <= 0) n = 1;
        return "(x" + n + ")";
    }
}
