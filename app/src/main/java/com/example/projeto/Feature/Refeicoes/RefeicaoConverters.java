package com.example.projeto.Feature.Refeicoes;

import com.example.projeto.Feature.Cardapio.Prato;

import java.util.Collections;
import java.util.List;

public final class RefeicaoConverters {

    private RefeicaoConverters() {}

    public static Prato paraPrato(RefeicaoResponse r) {
        if (r == null) {
            return new Prato("", ApiUiFormatter.VAZIO, ApiUiFormatter.VAZIO, 0, 0, 0L);
        }
        int cal = r.calorias != null ? (int) Math.round(r.calorias) : 0;
        int t = r.tempoPreparo != null ? r.tempoPreparo : 0;
        List<String> ingList = r.ingredientes != null ? r.ingredientes : Collections.emptyList();
        String ing = ApiUiFormatter.listaIngredientes(ingList);
        String nome = r.nome != null ? r.nome : "";
        String prep = textoPreparo(r);
        long id = r.id != null ? r.id : 0L;
        return new Prato(nome, ing, prep, cal, t, id);
    }

    public static String textoPreparo(RefeicaoResponse r) {
        if (r == null) {
            return ApiUiFormatter.VAZIO;
        }
        String[] cand = { r.modoPreparo, r.preparo, r.instrucoesPreparo };
        for (String s : cand) {
            if (s != null) {
                String t = s.trim();
                if (!t.isEmpty()) {
                    return t;
                }
            }
        }
        return ApiUiFormatter.VAZIO;
    }
}
