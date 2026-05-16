package com.example.projeto.Feature.Cardapio;

/**
 * Uma refeição escolhida em "Criar cardápio", serializada para exibir em {@link CardapioFragment}.
 */
public class CardapioItemPersistido {
    public String tipo;
    public String nome;
    /** Ex.: "10 min" */
    public String tempo;
    /** Ex.: "250 kcal" */
    public String kcal;
    public String ingredientes;
    public String preparo;

    public CardapioItemPersistido() {}

    public CardapioItemPersistido(String tipo, String nome, String tempo, String kcal,
                                  String ingredientes, String preparo) {
        this.tipo = tipo;
        this.nome = nome;
        this.tempo = tempo;
        this.kcal = kcal;
        this.ingredientes = ingredientes;
        this.preparo = preparo;
    }
}
