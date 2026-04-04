package com.example.projeto.repository;

import com.example.projeto.models.Refeicao;

import java.util.ArrayList;
import java.util.List;

public class RefeicaoRepository {

    private static List<Refeicao> refeicoesSelecionadas = new ArrayList<>();

    // salvar seleções
    public static void setSelecionadas(List<Refeicao> lista) {
        refeicoesSelecionadas = lista;
    }

    // recuperar seleções
    public static List<Refeicao> getSelecionadas() {
        return refeicoesSelecionadas;
    }

    // limpar (opcional)
    public static void limpar() {
        refeicoesSelecionadas.clear();
    }
}
