package com.example.projeto.Feature.Perfil;

import java.util.List;

public class UsuarioResponse {
    private Long id;
    private String nome;
    private String email;
    private String dataNascimento;
    private List<String> restricoes;

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getDataNascimento() { return dataNascimento; }
    public List<String> getRestricoes() { return restricoes; }
}