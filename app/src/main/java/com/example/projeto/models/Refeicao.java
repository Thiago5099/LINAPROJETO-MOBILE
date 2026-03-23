package com.example.projeto.models;

public class Refeicao {
    public String tipo;
    public com.example.projeto.models.Prato prato;

    public Refeicao(String t, com.example.projeto.models.Prato p) {
        tipo = t;
        prato = p;
    }
}