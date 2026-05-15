package com.example.projeto.Feature.Perfil;

import java.util.List;

public class UsuarioResponse {
    private Long id;
    private String nome;
    private String email;
    private List<String> restricoes;
    private boolean assinante;
    private String dataNascimento;
    private String genero;

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public List<String> getRestricoes() { return restricoes; }
    public boolean isAssinante() { return assinante; }
    public String getDataNascimento() { return dataNascimento; }
    public String getGenero() { return genero; }
}