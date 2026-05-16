package com.example.projeto.Feature.Cardapio;

public class ReceitaIntentKeys {
    public static final String MOMENTO   = "momento";
    public static final String TITULO    = "titulo";
    public static final String TEMPO     = "tempo";
    public static final String KCAL      = "kcal";
    public static final String SEM_GLUTEN   = "sem_gluten";
    public static final String SEM_LACTOSE  = "sem_lactose";
    public static final String INGREDIENTES = "ingredientes";
    public static final String PREPARO      = "preparo";
    public static final String NUTRICIONAL  = "nutricional";

    /** {@code ?periodo=} enviado ao backend (ex.: CAFE_DA_MANHA). */
    public static final String PERIODO_QUERY = "periodo_query";
    /** Quando true e há sessão, detalhes são atualizados via GET /refeicoes. */
    public static final String FETCH_FROM_BACKEND = "fetch_from_backend";
}