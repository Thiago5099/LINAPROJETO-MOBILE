package com.example.projeto.Feature.Nutricionistas;

public class Nutricionista {
    private int id;
    private String nome, especialidade, cidade, telefone, email;
    private int avaliacao;
    private int atendimentosRealizados; // ← novo

    // getters existentes mantidos...
    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getEspecialidade() { return especialidade; }
    public String getCidade() { return cidade; }
    public String getTelefone() { return telefone; }
    public String getEmail() { return email; }
    public float getAvaliacao() { return avaliacao; }

    // ← novo getter
    public String getPacientes() {
        return atendimentosRealizados + "+ pacientes atendidos";
    }
}