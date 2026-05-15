package com.example.projeto.Feature.Login;

public class LoginResponse {
    private String token;
    private Long usuarioId;
    private String nome;
    private String email;

    public String getToken() { return token; }
    public Long getUsuarioId() { return usuarioId; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
}