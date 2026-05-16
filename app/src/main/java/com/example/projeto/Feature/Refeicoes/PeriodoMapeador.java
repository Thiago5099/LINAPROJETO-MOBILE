package com.example.projeto.Feature.Refeicoes;

/**
 * Converte rótulos da UI para {@code PeriodoDia} da query (?periodo=...) e vice-versa.
 */
public final class PeriodoMapeador {

    private PeriodoMapeador() {}

    /** Query enviada ao backend: CAFE_DA_MANHA, ALMOCO, LANCHE_DA_TARDE, JANTAR */
    public static String uiParaQuery(String tipoExibicao) {
        if (tipoExibicao == null) return null;
        switch (tipoExibicao) {
            case "Café da manhã":
                return "CAFE_DA_MANHA";
            case "Almoço":
                return "ALMOCO";
            case "Lanche":
            case "Lanche da tarde":
                return "LANCHE_DA_TARDE";
            case "Jantar":
                return "JANTAR";
            default:
                return null;
        }
    }

    /** Rótulo usado no criar cardápio (agrupamento por período). */
    public static String queryParaLabelCriarCardapio(String periodoQuery) {
        if (periodoQuery == null) return "";
        switch (periodoQuery) {
            case "CAFE_DA_MANHA":
                return "Café da manhã";
            case "ALMOCO":
                return "Almoço";
            case "LANCHE_DA_TARDE":
                return "Lanche da tarde";
            case "JANTAR":
                return "Jantar";
            default:
                return periodoQuery;
        }
    }
}
