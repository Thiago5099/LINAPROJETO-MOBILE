package com.example.projeto.Feature.Cardapio;

public class Prato {
    public String nome, ingredientes, preparo;
    public int calorias, tempo;
    /** Id no banco local; 0 se desconhecido. */
    public long refeicaoId;

    public Prato(String n, String i, String p, int c, int t) {
        this(n, i, p, c, t, 0L);
    }

    public Prato(String n, String i, String p, int c, int t, long refeicaoId) {
        nome = n;
        ingredientes = i;
        preparo = p;
        calorias = c;
        tempo = t;
        this.refeicaoId = refeicaoId;
    }
}
