package com.example.projeto.feture.CardapioAlterarRefeicao;

public class PratoAlterarRefeicao {
    public String nome, ingredientes, preparo;
    public int calorias, tempo;

    public PratoAlterarRefeicao(String n, String i, String p, int c, int t) {
        nome = n;
        ingredientes = i;
        preparo = p;
        calorias = c;
        tempo = t;
    }
}