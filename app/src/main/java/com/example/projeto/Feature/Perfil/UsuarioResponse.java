package com.example.projeto.Feature.Perfil;

import java.util.List;

public class UsuarioResponse {
    private Long id;
    private String nome;
    private String email;
    private List<String> restricoes;

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public List<String> getRestricoes() { return restricoes; }
}