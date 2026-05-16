package com.example.projeto.Feature.Cardapio;

public class ReceitaIntentKeys {
    public static final String MOMENTO = "momento";
    public static final String TITULO = "titulo";
    public static final String TEMPO = "tempo";
    public static final String KCAL = "kcal";
    public static final String INGREDIENTES = "ingredientes";
    public static final String PREPARO = "preparo";

    /** Id da refeição no backend (detalhe via GET /refeicoes/{id}). */
    public static final String REFEICAO_ID = "refeicao_id";
    /** {@code ?periodo=} enviado ao backend (ex.: CAFE_DA_MANHA). */
    public static final String PERIODO_QUERY = "periodo_query";
    /** Quando true e há sessão, detalhes são atualizados pela API. */
    public static final String FETCH_FROM_BACKEND = "fetch_from_backend";
}
