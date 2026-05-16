package com.example.projeto.Feature.Refeicoes;

import com.example.projeto.Feature.Cardapio.Prato;

import java.util.Collections;
import java.util.List;

public final class RefeicaoConverters {

    private RefeicaoConverters() {}

    public static Prato paraPrato(RefeicaoResponse r) {
        if (r == null) {
            return new Prato("", "—", "—", 0, 0);
        }
        int cal = r.calorias != null ? (int) Math.round(r.calorias) : 0;
        int t = r.tempoPreparo != null ? r.tempoPreparo : 0;
        List<String> ingList = r.ingredientes != null ? r.ingredientes : Collections.emptyList();
        String ing = ingList.isEmpty() ? "—" : String.join(", ", ingList);
        String nome = r.nome != null ? r.nome : "";
        return new Prato(nome, ing, "—", cal, t);
    }
}
