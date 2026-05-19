package com.example.projeto.Feature.Compras.models;

public class ComprasItemLista {

    public static final int TIPO_HEADER = 0;
    public static final int TIPO_ITEM = 1;

    private int tipo;
    private String categoria;
    private int count;
    private ComprasIngrediente ingrediente;

    // HEADER com total de itens da categoria
    public ComprasItemLista(String categoria, int count) {
        this.tipo = TIPO_HEADER;
        this.categoria = categoria;
        this.count = count;
    }

    // ITEM
    public ComprasItemLista(ComprasIngrediente ingrediente) {
        this.tipo = TIPO_ITEM;
        this.ingrediente = ingrediente;
    }

    public int getTipo() { return tipo; }
    public String getCategoria() { return categoria; }
    public int getCount() { return count; }
    public ComprasIngrediente getIngrediente() { return ingrediente; }
}
