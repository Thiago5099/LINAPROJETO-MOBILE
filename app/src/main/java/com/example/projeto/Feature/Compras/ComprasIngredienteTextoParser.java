package com.example.projeto.Feature.Compras;

import com.example.projeto.Feature.Refeicoes.ApiUiFormatter;

import java.util.ArrayList;
import java.util.List;

/**
 * Extrai nomes de ingredientes a partir do texto salvo no cardápio local (linhas com •).
 */
public final class ComprasIngredienteTextoParser {

    private ComprasIngredienteTextoParser() {}

    public static List<String> linhasDeTexto(String texto) {
        List<String> out = new ArrayList<>();
        if (texto == null || texto.isEmpty()) return out;
        String t = texto.trim();
        if (ApiUiFormatter.VAZIO.equals(t) || "—".equals(t)) return out;

        String[] partes = t.split("\\r?\\n");
        for (String parte : partes) {
            String linha = parte.trim();
            if (linha.isEmpty()) continue;
            if (linha.startsWith("•")) {
                linha = linha.substring(1).trim();
            }
            if (linha.startsWith("-")) {
                linha = linha.substring(1).trim();
            }
            if (!linha.isEmpty()) {
                out.add(linha);
            }
        }
        return out;
    }
}
