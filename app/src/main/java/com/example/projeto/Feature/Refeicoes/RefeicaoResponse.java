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
    public List<String> ingredientes;
    public List<String> restricoes;
}
