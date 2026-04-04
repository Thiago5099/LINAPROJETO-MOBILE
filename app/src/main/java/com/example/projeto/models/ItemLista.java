package com.example.projeto.models;

public class ItemLista {

    public static final int TIPO_HEADER = 0;
    public static final int TIPO_ITEM = 1;

    private int tipo;
    private String categoria;
    private Ingrediente ingrediente;

    // HEADER
    public ItemLista(String categoria) {
        this.tipo = TIPO_HEADER;
        this.categoria = categoria;
    }

    // ITEM
    public ItemLista(Ingrediente ingrediente) {
        this.tipo = TIPO_ITEM;
        this.ingrediente = ingrediente;
    }

    public int getTipo() { return tipo; }
    public String getCategoria() { return categoria; }
    public Ingrediente getIngrediente() { return ingrediente; }
}
