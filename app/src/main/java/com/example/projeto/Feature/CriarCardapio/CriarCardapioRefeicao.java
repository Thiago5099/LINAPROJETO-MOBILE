package com.example.projeto.Feature.CriarCardapio;

import com.example.projeto.Feature.Refeicoes.ApiUiFormatter;
import com.example.projeto.Feature.Refeicoes.PeriodoMapeador;
import com.example.projeto.Feature.Refeicoes.RefeicaoConverters;
import com.example.projeto.Feature.Refeicoes.RefeicaoResponse;

import java.util.Collections;
import java.util.List;

public class CriarCardapioRefeicao {
    /** Id da refeição no backend; null se dado só local. */
    public Long refeicaoId;
    public String tipo;
    public String nome;
    public String tempo;
    public String kcal;
    public String ingredientesTexto;
    public String preparoTexto;

    public CriarCardapioRefeicao(String tipo, String nome, String tempo, String kcal) {
        this(null, tipo, nome, tempo, kcal, null, null);
    }

    public CriarCardapioRefeicao(Long refeicaoId, String tipo, String nome, String tempo, String kcal,
                                 String ingredientesTexto, String preparoTexto) {
        this.refeicaoId = refeicaoId;
        this.tipo = tipo;
        this.nome = nome;
        this.tempo = tempo;
        this.kcal = kcal;
        this.ingredientesTexto = ingredientesTexto;
        this.preparoTexto = preparoTexto;
    }

    public static CriarCardapioRefeicao fromDto(RefeicaoResponse dto, String periodoQuery) {
        if (dto == null) {
            return new CriarCardapioRefeicao(null, "", "", "", "", "", ApiUiFormatter.VAZIO);
        }
        String tipoLabel = PeriodoMapeador.queryParaLabelCriarCardapio(periodoQuery);
        double cal = dto.calorias != null ? dto.calorias : 0d;
        int tmp = dto.tempoPreparo != null ? dto.tempoPreparo : 0;
        List<String> ingList = dto.ingredientes != null ? dto.ingredientes : Collections.emptyList();
        String nome = dto.nome != null ? dto.nome : "";
        String prep = RefeicaoConverters.textoPreparo(dto);
        String ingFmt = ApiUiFormatter.listaIngredientes(ingList);
        return new CriarCardapioRefeicao(
                dto.id,
                tipoLabel,
                nome,
                tmp > 0 ? tmp + " min" : null,
                cal > 0 ? Math.round(cal) + " kcal" : null,
                ingFmt,
                prep);
    }
}
