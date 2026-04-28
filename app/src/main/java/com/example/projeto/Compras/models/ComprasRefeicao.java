package com.example.projeto.Compras.models;

import java.io.Serializable;
import java.util.List;

public class ComprasRefeicao implements Serializable {
    private String nome;
    private List<ComprasIngrediente> ingredientes;
    private boolean selecionada;

    public List<ComprasIngrediente> getIngredientes() {
        return ingredientes;
    }

    public boolean isSelecionada() {
        return selecionada;
    }
    public ComprasRefeicao(String nome, List<ComprasIngrediente> ingredientes, boolean selecionada) {
        this.nome = nome;
        this.ingredientes = ingredientes;
        this.selecionada = selecionada;
    }
}
