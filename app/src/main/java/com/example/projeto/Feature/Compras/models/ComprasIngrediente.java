package com.example.projeto.Feature.Compras.models;

public class ComprasIngrediente {
    private String nome;
    private double quantidade;
    private String unidade;
    private String categoria;
    private boolean comprado;

    public ComprasIngrediente(String nome, double quantidade, String unidade, String categoria) {
        this.nome = nome;
        this.quantidade = quantidade;
        this.unidade = unidade != null ? unidade : "";
        this.categoria = categoria;
        this.comprado = false;
    }

    public String getNome() { return nome; }
    public double getQuantidade() { return quantidade; }
    public String getUnidade() { return unidade; }
    public String getCategoria() { return categoria; }
    public boolean isComprado() { return comprado; }

    public void setQuantidade(double quantidade) {
        this.quantidade = quantidade;
    }

    public void setComprado(boolean comprado) {
        this.comprado = comprado;
    }
}
