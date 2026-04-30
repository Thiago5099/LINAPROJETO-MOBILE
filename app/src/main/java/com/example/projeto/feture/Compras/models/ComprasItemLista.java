package com.example.projeto.feture.Compras.models;

public class ComprasItemLista {

    public static final int TIPO_HEADER = 0;
    public static final int TIPO_ITEM = 1;

    private int tipo;
    private String categoria;
    private ComprasIngrediente ingrediente;

    // HEADER
    public ComprasItemLista(String categoria) {
        this.tipo = TIPO_HEADER;
        this.categoria = categoria;
    }

    // ITEM
    public ComprasItemLista(ComprasIngrediente ingrediente) {
        this.tipo = TIPO_ITEM;
        this.ingrediente = ingrediente;
    }

    public int getTipo() { return tipo; }
    public String getCategoria() { return categoria; }
    public ComprasIngrediente getIngrediente() { return ingrediente; }
}
