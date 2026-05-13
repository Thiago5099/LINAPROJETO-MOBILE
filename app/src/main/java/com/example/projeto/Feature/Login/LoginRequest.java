package com.example.projeto.Feature.Login;

public class LoginRequest {
    private String email;
    private String senha;

    public LoginRequest(String email, String senha) {
        this.email = email;
        this.senha = senha;
    }

    // Getters necessários para o Retrofit/Gson serializar o JSON corretamente
    public String getEmail() { return email; }
    public String getSenha() { return senha; }
}
