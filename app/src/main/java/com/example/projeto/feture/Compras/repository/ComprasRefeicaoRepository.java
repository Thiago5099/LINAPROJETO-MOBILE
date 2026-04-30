package com.example.projeto.feture.Compras.repository;

import com.example.projeto.feture.Compras.models.ComprasRefeicao;

import java.util.ArrayList;
import java.util.List;

public class ComprasRefeicaoRepository {

    private static List<ComprasRefeicao> refeicoesSelecionadas = new ArrayList<>();

    // salvar seleções
    public static void setSelecionadas(List<ComprasRefeicao> lista) {
        refeicoesSelecionadas = lista;
    }

    // recuperar seleções
    public static List<ComprasRefeicao> getSelecionadas() {
        return refeicoesSelecionadas;
    }

    // limpar (opcional)
    public static void limpar() {
        refeicoesSelecionadas.clear();
    }
}
