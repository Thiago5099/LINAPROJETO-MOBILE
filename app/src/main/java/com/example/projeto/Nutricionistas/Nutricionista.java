package com.example.projeto.Nutricionistas;

public class Nutricionista {
    private int id;
    private String nome, especialidade, cidade, telefone, email, pacientes;
    private float avaliacao;
    public Nutricionista(int id, String nome, String especialidade, String cidade, String telefone, String email, String pacientes, float avaliacao) {
        this.id = id;
        this.nome = nome;
        this.especialidade = especialidade;
        this.cidade = cidade;
        this.telefone = telefone;
        this.email = email;
        this.pacientes = pacientes;
        this.avaliacao = avaliacao;
    }

    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getEspecialidade() { return especialidade; }
    public String getCidade() { return cidade; }
    public String getTelefone() { return telefone; }
    public String getEmail() {return email;}
    public String getPacientes() {return pacientes;}
    public float getAvaliacao() { return avaliacao; }

}