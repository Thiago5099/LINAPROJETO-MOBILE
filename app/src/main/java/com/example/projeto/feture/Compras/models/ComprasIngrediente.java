package com.example.projeto.feture.Compras.models;

public class ComprasIngrediente {
    private String nome;
    private int quantidade;
    private String categoria;
    private boolean comprado;

    public ComprasIngrediente(String nome, int quantidade, String categoria) {
        this.nome = nome;
        this.quantidade = quantidade;
        this.categoria = categoria;
        this.comprado = false;
    }

    public String getNome() { return nome; }
    public int getQuantidade() { return quantidade; }
    public String getCategoria() { return categoria; }
    public boolean isComprado() { return comprado; }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public void setComprado(boolean comprado) {
        this.comprado = comprado;
    }
}
