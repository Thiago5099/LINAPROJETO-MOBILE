package com.example.projeto.Feature.Cardapio;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.projeto.R;
import com.example.projeto.Feature.Login.ApiAuthHeaders;
import com.example.projeto.Feature.Nutricionistas.RetrofitClient;
import com.example.projeto.Feature.Refeicoes.RefeicaoApiService;
import com.example.projeto.Feature.Refeicoes.RefeicaoConverters;
import com.example.projeto.Feature.Refeicoes.RefeicaoResponse;
import com.example.projeto.feture.cardapio.ReceitaIntentKeys;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Response;

public class ReceitaActivity extends AppCompatActivity {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private TextView textMomento;
    private TextView textTitulo;
    private TextView textTempo;
    private TextView textKcal;
    private TextView textGluten;
    private TextView textLactose;
    private TextView textIngredientes;
    private TextView textPreparo;
    private TextView textNutricional;

    /**
     * Preenche extras para {@link ReceitaActivity}.
     *
     * @param periodoQuery quando não nulo (ex.: {@code ALMOCO}), a tela tenta atualizar pelo GET /refeicoes.
     */
    public static void putRecipeExtras(Intent it,
            @Nullable String momentoUi,
            @Nullable String titulo,
            @Nullable String tempoFmt,
            @Nullable String kcalFmt,
            @Nullable String ingredientes,
            @Nullable String preparo,
            @Nullable String tagGluten,
            @Nullable String tagLactose,
            @Nullable String nutricional,
            @Nullable String periodoQuery) {
        it.putExtra(ReceitaIntentKeys.MOMENTO, momentoUi);
        it.putExtra(ReceitaIntentKeys.TITULO, titulo);
        it.putExtra(ReceitaIntentKeys.TEMPO, tempoFmt);
        it.putExtra(ReceitaIntentKeys.KCAL, kcalFmt);
        it.putExtra(ReceitaIntentKeys.INGREDIENTES, ingredientes);
        it.putExtra(ReceitaIntentKeys.PREPARO, preparo);
        it.putExtra(ReceitaIntentKeys.SEM_GLUTEN, tagGluten);
        it.putExtra(ReceitaIntentKeys.SEM_LACTOSE, tagLactose);
        it.putExtra(ReceitaIntentKeys.NUTRICIONAL, nutricional);

        boolean fetch = periodoQuery != null && !periodoQuery.trim().isEmpty();
        it.putExtra(ReceitaIntentKeys.FETCH_FROM_BACKEND, fetch);
        if (fetch) {
            it.putExtra(ReceitaIntentKeys.PERIODO_QUERY, periodoQuery.trim());
        }
    }

    static String nz(@Nullable String s) {
        return (s != null && !s.trim().isEmpty()) ? s.trim() : "—";
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

        aplicarCamposLocais(getIntent());

        boolean fetch = getIntent().getBooleanExtra(ReceitaIntentKeys.FETCH_FROM_BACKEND, false);
        String periodo = getIntent().getStringExtra(ReceitaIntentKeys.PERIODO_QUERY);
        String nomeBusca = getIntent().getStringExtra(ReceitaIntentKeys.TITULO);

        if (!fetch || periodo == null || nomeBusca == null || nomeBusca.trim().isEmpty()) {
            return;
        }

        String auth = ApiAuthHeaders.bearerOrNull(this);
        long uid = getSharedPreferences("auth", MODE_PRIVATE).getLong("userId", 0L);
        if (auth == null || uid == 0L) {
            return;
        }

        executor.execute(() -> buscarDoBackend(auth, uid, periodo, nomeBusca.trim()));
    }

    private void aplicarCamposLocais(Intent intent) {
        textMomento.setText(nz(intent.getStringExtra(ReceitaIntentKeys.MOMENTO)));
        textTitulo.setText(nz(intent.getStringExtra(ReceitaIntentKeys.TITULO)));
        textTempo.setText(nz(intent.getStringExtra(ReceitaIntentKeys.TEMPO)));
        textKcal.setText(nz(intent.getStringExtra(ReceitaIntentKeys.KCAL)));
        textGluten.setText(nz(intent.getStringExtra(ReceitaIntentKeys.SEM_GLUTEN)));
        textLactose.setText(nz(intent.getStringExtra(ReceitaIntentKeys.SEM_LACTOSE)));
        textIngredientes.setText(nz(intent.getStringExtra(ReceitaIntentKeys.INGREDIENTES)));
        textPreparo.setText(nz(intent.getStringExtra(ReceitaIntentKeys.PREPARO)));
        textNutricional.setText(nz(intent.getStringExtra(ReceitaIntentKeys.NUTRICIONAL)));
    }

    private void buscarDoBackend(String authorization, long usuarioId, String periodo, String nomeBusca) {
        try {
            RefeicaoApiService api = RetrofitClient.getInstance().create(RefeicaoApiService.class);
            Response<List<RefeicaoResponse>> resp =
                    api.listar(authorization, periodo, usuarioId).execute();
            if (!resp.isSuccessful()) {
                runOnUiThread(() -> Toast.makeText(this,
                        "Não foi possível carregar a receita (" + resp.code() + ").",
                        Toast.LENGTH_SHORT).show());
                return;
            }
            List<RefeicaoResponse> body = resp.body();
            if (body == null) body = Collections.emptyList();

            RefeicaoResponse match = null;
            for (RefeicaoResponse r : body) {
                if (r != null && r.nome != null
                        && r.nome.trim().equalsIgnoreCase(nomeBusca)) {
                    match = r;
                    break;
                }
            }

            final RefeicaoResponse escolhido = match;
            runOnUiThread(() -> {
                if (escolhido != null) {
                    aplicarDto(escolhido);
                } else {
                    Toast.makeText(this,
                            "Este prato não foi encontrado no servidor para este período.",
                            Toast.LENGTH_SHORT).show();
                }
            });
        } catch (IOException e) {
            runOnUiThread(() -> Toast.makeText(this,
                    "Falha de rede ao carregar receita.",
                    Toast.LENGTH_SHORT).show());
        }
    }

    private void aplicarDto(RefeicaoResponse dto) {
        Prato p = RefeicaoConverters.paraPrato(dto);
        textTitulo.setText(nz(p.nome));
        textTempo.setText(p.tempo > 0 ? p.tempo + " min" : "—");
        textKcal.setText(p.calorias > 0 ? p.calorias + " kcal" : "—");
        textIngredientes.setText(nz(p.ingredientes));
        textPreparo.setText(nz(p.preparo));

        List<String> rest = dto.restricoes != null ? dto.restricoes : Collections.emptyList();
        textGluten.setText(tagPorPalavra(rest, "gluten", "Sem glúten"));
        textLactose.setText(tagPorPalavra(rest, "lactose", "Sem lactose"));

        String nutricional = (dto.calorias != null ? Math.round(dto.calorias) + " kcal" : "—");
        if (!rest.isEmpty()) {
            nutricional += " • " + String.join(", ", rest);
        }
        textNutricional.setText(nutricional);
    }

    /** Retorna {@code rotulo} se alguma restrição contém {@code palavra}; caso contrário "—". */
    private static String tagPorPalavra(List<String> restricoes, String palavra, String rotulo) {
        if (restricoes == null || restricoes.isEmpty()) return "—";
        String p = palavra.toLowerCase();
        for (String s : restricoes) {
            if (s != null && s.toLowerCase().contains(p)) {
                return rotulo;
            }
        }
        return "—";
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdownNow();
    }
}
