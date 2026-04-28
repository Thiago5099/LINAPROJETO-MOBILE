package com.example.projeto.feture.Cardapio;

public class Prato {
    public String nome, ingredientes, preparo;
    public int calorias, tempo;

    public Prato(String n, String i, String p, int c, int t) {
        nome = n;
        ingredientes = i;
        preparo = p;
        calorias = c;
        tempo = t;
    }
}