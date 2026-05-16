package com.example.projeto.Feature.Cardapio;

/**
 * Uma refeição escolhida em "Criar cardápio", serializada para exibir em {@link CardapioFragment}.
 */
public class CardapioItemPersistido {
    public Long refeicaoId;
    public String tipo;
    public String nome;
    public String tempo;
    public String kcal;
    public String ingredientes;
    public String preparo;

    public CardapioItemPersistido() {}

    public CardapioItemPersistido(String tipo, String nome, String tempo, String kcal,
                                  String ingredientes, String preparo) {
        this(null, tipo, nome, tempo, kcal, ingredientes, preparo);
    }

    public CardapioItemPersistido(Long refeicaoId, String tipo, String nome, String tempo, String kcal,
                                  String ingredientes, String preparo) {
        this.refeicaoId = refeicaoId;
        this.tipo = tipo;
        this.nome = nome;
        this.tempo = tempo;
        this.kcal = kcal;
        this.ingredientes = ingredientes;
        this.preparo = preparo;
    }
}
