package com.example.projeto.Feature.Refeicoes;

import java.util.List;

/**
 * Espelha {@code RefeicaoResponseDTO} do backend (JSON camelCase).
 */
public class RefeicaoResponse {
    public Long id;
    public String nome;
    public String imagemUrl;
    public Double calorias;
    public Integer tempoPreparo;
    public String modoPreparo;
    public String periodo;
    public String periodoLabel;

    public List<String> ingredientes;
    public List<IngredienteItemResponse> ingredientesDetalhados;
    public List<String> restricoes;
    public List<String> adequadoPara;
    public List<String> periodosPermitidos;
    public InformacaoNutricionalResponse informacoesNutricionais;

    /** Campos legados (Gson ignora se não existirem). */
    public String preparo;
    public String instrucoesPreparo;
}
