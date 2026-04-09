package com.example.projeto;

public class Nutricionista {
    private int id;
    private String nome, especialidade, cidade, telefone;

    public Nutricionista(int id, String nome, String especialidade, String cidade, String telefone) {
        this.id = id;
        this.nome = nome;
        this.especialidade = especialidade;
        this.cidade = cidade;
        this.telefone = telefone;
    }

    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getEspecialidade() { return especialidade; }
    public String getCidade() { return cidade; }
    public String getTelefone() { return telefone; }
}