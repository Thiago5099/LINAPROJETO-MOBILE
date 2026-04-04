package com.example.projeto.models;

import java.io.Serializable;
import java.util.List;

public class Refeicao implements Serializable {
    private String nome;
    private List<Ingrediente> ingredientes;
    private boolean selecionada;

    public List<Ingrediente> getIngredientes() {
        return ingredientes;
    }

    public boolean isSelecionada() {
        return selecionada;
    }
}
