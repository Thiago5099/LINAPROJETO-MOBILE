package com.example.projeto.Feature.Cardapio;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.projeto.R;
import com.example.projeto.Data.BancoHelper;

public class ReceitaActivity extends AppCompatActivity {

    private static final String VAZIO = "—";

    private TextView textMomento;
    private TextView textTitulo;
    private TextView textTempo;
    private TextView textKcal;
    private TextView textGluten;
    private TextView textLactose;
    private TextView textIngredientes;
    private TextView textPreparo;
    private TextView textNutricional;
    private ImageView imageReceita;

    /**
     * Preenche extras para {@link ReceitaActivity}.
     *
     * @param refeicaoId     id no banco local
     * @param periodoQuery   período da refeição usado no banco local
     */
    public static void putRecipeExtras(Intent it,
                                       @Nullable String momentoUi,
                                       @Nullable Long refeicaoId,
                                       @Nullable String titulo,
                                       @Nullable String tempoFmt,
                                       @Nullable String kcalFmt,
                                       @Nullable String ingredientes,
                                       @Nullable String preparo,
                                       @Nullable String periodoQuery) {

        it.putExtra(ReceitaIntentKeys.MOMENTO, momentoUi);
        it.putExtra(ReceitaIntentKeys.TITULO, titulo);
        it.putExtra(ReceitaIntentKeys.TEMPO, tempoFmt);
        it.putExtra(ReceitaIntentKeys.KCAL, kcalFmt);
        it.putExtra(ReceitaIntentKeys.INGREDIENTES, ingredientes);
        it.putExtra(ReceitaIntentKeys.PREPARO, preparo);

        if (refeicaoId != null && refeicaoId > 0L) {
            it.putExtra(ReceitaIntentKeys.REFEICAO_ID, refeicaoId);
        }

        if (periodoQuery != null && !periodoQuery.trim().isEmpty()) {
            it.putExtra(ReceitaIntentKeys.PERIODO_QUERY, periodoQuery.trim());
        }
    }

    static String vazio(@Nullable String s) {
        return s == null || s.trim().isEmpty() ? VAZIO : s.trim();
    }

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_cardapio_receita);

        ImageButton btnVoltar = findViewById(R.id.buttonVoltarReceita);
        btnVoltar.setOnClickListener(v -> finish());

        textMomento = findViewById(R.id.textReceitaMomento);
        textTitulo = findViewById(R.id.textTituloReceita);
        textTempo = findViewById(R.id.textTagTempo);
        textKcal = findViewById(R.id.textTagKcal);
        textGluten = findViewById(R.id.textTagSemGluten);
        textLactose = findViewById(R.id.textTagSemLactose);
        textIngredientes = findViewById(R.id.textIngredientes);
        textPreparo = findViewById(R.id.textModoDePreparo);
        textNutricional = findViewById(R.id.textInformacoesNutricionais);
        imageReceita = findViewById(R.id.imageReceita);

        aplicarCamposLocais(getIntent());

        long refeicaoId = getIntent().hasExtra(ReceitaIntentKeys.REFEICAO_ID)
                ? getIntent().getLongExtra(ReceitaIntentKeys.REFEICAO_ID, 0L)
                : 0L;
        String periodo = getIntent().getStringExtra(ReceitaIntentKeys.PERIODO_QUERY);
        String nomeBusca = getIntent().getStringExtra(ReceitaIntentKeys.TITULO);
        BancoHelper.ReceitaDetalhe detalhe =
                new BancoHelper(this).buscarReceita(refeicaoId, periodo, nomeBusca);
        if (detalhe != null) {
            aplicarDetalheLocal(detalhe);
        }
    }

    private void aplicarCamposLocais(Intent intent) {
        textMomento.setText(vazio(intent.getStringExtra(ReceitaIntentKeys.MOMENTO)));
        textTitulo.setText(vazio(intent.getStringExtra(ReceitaIntentKeys.TITULO)));
        textTempo.setText(vazio(intent.getStringExtra(ReceitaIntentKeys.TEMPO)));
        textKcal.setText(vazio(intent.getStringExtra(ReceitaIntentKeys.KCAL)));
        textGluten.setText(VAZIO);
        textLactose.setText(VAZIO);
        textIngredientes.setText(vazio(intent.getStringExtra(ReceitaIntentKeys.INGREDIENTES)));
        textPreparo.setText(vazio(intent.getStringExtra(ReceitaIntentKeys.PREPARO)));
        textNutricional.setText(VAZIO);
    }

    private void aplicarDetalheLocal(BancoHelper.ReceitaDetalhe detalhe) {
        textMomento.setText(vazio(detalhe.periodo));
        textTitulo.setText(vazio(detalhe.nome));
        textTempo.setText(detalhe.tempo + " min");
        textKcal.setText(detalhe.calorias + " kcal");
        String[] restricoes = detalhe.restricoes != null ? detalhe.restricoes.split("\\n") : new String[0];
        textGluten.setText(restricoes.length > 0 ? restricoes[0] : VAZIO);
        textLactose.setText(restricoes.length > 1 ? restricoes[1] : VAZIO);
        textIngredientes.setText(vazio(detalhe.ingredientes));
        textPreparo.setText(vazio(detalhe.preparo));
        textNutricional.setText(vazio(detalhe.nutricional));
        aplicarImagem();
    }

    private void aplicarImagem() {
        if (imageReceita == null) {
            return;
        }
        imageReceita.setImageResource(R.drawable.ic_refeicao);
    }

}
