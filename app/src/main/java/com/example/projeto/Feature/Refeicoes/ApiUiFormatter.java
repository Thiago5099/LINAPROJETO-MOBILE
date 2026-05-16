package com.example.projeto.Feature.Refeicoes;

import androidx.annotation.Nullable;

import java.util.List;
import java.util.Locale;

/**
 * Formata campos da API para a UI; usa {@link #VAZIO} quando o dado não vier.
 */
public final class ApiUiFormatter {

    public static final String VAZIO = "_";

    private ApiUiFormatter() {}

    public static String texto(@Nullable String s) {
        if (s == null || s.trim().isEmpty()) {
            return VAZIO;
        }
        return s.trim();
    }

    public static String tempoMinutos(@Nullable Integer minutos) {
        if (minutos == null || minutos <= 0) {
            return VAZIO;
        }
        return minutos + " min";
    }

    public static String caloriasResumo(@Nullable Double calorias) {
        if (calorias == null || calorias <= 0) {
            return VAZIO;
        }
        return Math.round(calorias) + " kcal";
    }

    public static String listaIngredientes(@Nullable List<String> linhas) {
        if (linhas == null || linhas.isEmpty()) {
            return VAZIO;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < linhas.size(); i++) {
            String linha = linhas.get(i);
            if (linha == null || linha.trim().isEmpty()) {
                continue;
            }
            if (sb.length() > 0) {
                sb.append('\n');
            }
            sb.append("• ").append(linha.trim());
        }
        return sb.length() == 0 ? VAZIO : sb.toString();
    }

    public static String tagAdequado(@Nullable List<String> adequadoPara, String chave) {
        if (adequadoPara == null || adequadoPara.isEmpty()) {
            return VAZIO;
        }
        String alvo = chave.toLowerCase(Locale.ROOT);
        for (String tag : adequadoPara) {
            if (tag != null && tag.toLowerCase(Locale.ROOT).contains(alvo)) {
                return tag;
            }
        }
        return VAZIO;
    }

    public static String montarInformacoesNutricionais(
            @Nullable InformacaoNutricionalResponse info,
            @Nullable Double caloriasResumo) {

        if (info == null) {
            return caloriasResumo != null && caloriasResumo > 0
                    ? formatarNumero(caloriasResumo) + " kcal"
                    : VAZIO;
        }

        StringBuilder sb = new StringBuilder();

        String porcao = texto(info.porcaoLabel);
        if (!VAZIO.equals(porcao)) {
            sb.append(porcao).append('\n').append('\n');
        }

        String kcal = info.calorias != null
                ? formatarNumero(info.calorias) + " kcal"
                : caloriasResumo(caloriasResumo);
        appendLinha(sb, kcal);

        appendLinha(sb, linhaGramas("Proteína", info.proteinaG));
        appendLinha(sb, linhaGramas("Carboidratos", info.carboidratosG));
        appendSub(sb, linhaGramas("Fibra alimentar", info.fibrasG));
        appendSub(sb, linhaGramas("Açúcares", info.acucaresG));
        appendLinha(sb, linhaGramas("Gordura total", info.gorduraTotalG));
        appendSub(sb, linhaGramas("Gordura saturada", info.gorduraSaturadaG));
        appendSub(sb, linhaGramas("Gordura monoinsaturada", info.gorduraMonoinsaturadaG));
        appendSub(sb, linhaGramas("Gordura poliinsaturada", info.gorduraPoliinsaturadaG));

        sb.append('\n').append("Outro").append('\n');
        appendSub(sb, linhaMg("Colesterol", info.colesterolMg));
        appendSub(sb, linhaGramas("Sal", info.salG));
        appendSub(sb, linhaMg("Sódio", info.sodioMg));

        sb.append('\n').append("Minerais").append('\n');
        appendSub(sb, linhaMg("Potássio", info.potassioMg));

        String out = sb.toString().trim();
        return out.isEmpty() ? VAZIO : out;
    }

    private static void appendLinha(StringBuilder sb, String linha) {
        if (linha == null || linha.isEmpty()) {
            return;
        }
        if (sb.length() > 0) {
            sb.append('\n');
        }
        sb.append(linha);
    }

    private static void appendSub(StringBuilder sb, String linha) {
        if (linha == null || linha.isEmpty()) {
            return;
        }
        if (sb.length() > 0) {
            sb.append('\n');
        }
        sb.append("   ").append(linha);
    }

    private static String linhaGramas(String rotulo, @Nullable Double valor) {
        if (valor == null) {
            return rotulo + ": " + VAZIO;
        }
        return formatarNumero(valor) + " g " + rotulo;
    }

    private static String linhaMg(String rotulo, @Nullable Double valor) {
        if (valor == null) {
            return rotulo + ": " + VAZIO;
        }
        String prefix = valor < 1 ? "< " + formatarNumero(1) : formatarNumero(valor);
        return prefix + " mg " + rotulo;
    }

    private static String formatarNumero(double valor) {
        if (Math.rint(valor) == valor) {
            return String.valueOf((long) valor);
        }
        return String.format(Locale.forLanguageTag("pt-BR"), "%.1f", valor)
                .replace('.', ',');
    }
}
