package com.example.projeto.Feature.Perfil;

import java.util.Collections;
import java.util.List;

/**
 * Corpo de PUT /usuarios/{id} — espelha {@code UsuarioUpdateDTO} do backend (nome, email, restricoes).
 */
public class UsuarioUpdateRequest {
    private final String nome;
    private final String email;
    private final List<String> restricoes;

    public UsuarioUpdateRequest(String nome, String email, List<String> restricoes) {
        this.nome = nome;
        this.email = email;
        this.restricoes = restricoes != null ? restricoes : Collections.emptyList();
    }

    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public List<String> getRestricoes() { return restricoes; }
}
