package com.example.projeto.Feature.CriarCardapio;

public class CriarCardapioRefeicao {
    /** Id da refeição no banco local. */
    public Long refeicaoId;
    public String tipo;
    public String nome;
    public String tempo;
    public String kcal;
    public String ingredientesTexto;
    public String preparoTexto;

    public CriarCardapioRefeicao(String tipo, String nome, String tempo, String kcal) {
        this(null, tipo, nome, tempo, kcal, null, null);
    }

    public CriarCardapioRefeicao(Long refeicaoId, String tipo, String nome, String tempo, String kcal,
                                 String ingredientesTexto, String preparoTexto) {
        this.refeicaoId = refeicaoId;
        this.tipo = tipo;
        this.nome = nome;
        this.tempo = tempo;
        this.kcal = kcal;
        this.ingredientesTexto = ingredientesTexto;
        this.preparoTexto = preparoTexto;
    }
}
