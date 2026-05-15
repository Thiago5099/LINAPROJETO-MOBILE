package com.example.projeto.Feature.Login;

public class CadastroRequest {
    private String nome;
    private String email;
    private String senha;
    private String genero;
    private String restricaoAlimentar;
    private String dataNascimento;

    public CadastroRequest(String nome, String email, String senha,
                           String genero, String restricaoAlimentar,
                           String dataNascimento) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.genero = genero;
        this.restricaoAlimentar = restricaoAlimentar;
        this.dataNascimento = dataNascimento;
    }

    // Getters necessários para o Retrofit/Gson serializar o JSON corretamente
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getSenha() { return senha; }
    public String getGenero() { return genero; }
    public String getRestricaoAlimentar() { return restricaoAlimentar; }
    public String getDataNascimento() { return dataNascimento; }
}
