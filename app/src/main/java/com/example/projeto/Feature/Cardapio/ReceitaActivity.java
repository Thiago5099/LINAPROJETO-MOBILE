package com.example.projeto.Feature.Cardapio;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.projeto.R;
import com.example.projeto.Feature.Login.ApiAuthHeaders;
import com.example.projeto.Feature.Nutricionistas.RetrofitClient;
import com.example.projeto.Feature.Refeicoes.ApiUiFormatter;
import com.example.projeto.Feature.Refeicoes.RefeicaoApiService;
import com.example.projeto.Feature.Refeicoes.RefeicaoConverters;
import com.example.projeto.Feature.Refeicoes.RefeicaoResponse;

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
    private ImageView imageReceita;

    /**
     * Preenche extras para {@link ReceitaActivity}.
     *
     * @param refeicaoId     id no backend; se &gt; 0, usa GET /refeicoes/{id}
     * @param periodoQuery   ex.: CAFE_DA_MANHA (opcional no detalhe)
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

        boolean fetch = (refeicaoId != null && refeicaoId > 0L)
                || (periodoQuery != null && !periodoQuery.trim().isEmpty()
                && titulo != null && !titulo.trim().isEmpty());
        it.putExtra(ReceitaIntentKeys.FETCH_FROM_BACKEND, fetch);
        if (periodoQuery != null && !periodoQuery.trim().isEmpty()) {
            it.putExtra(ReceitaIntentKeys.PERIODO_QUERY, periodoQuery.trim());
        }
    }

    static String vazio(@Nullable String s) {
        return ApiUiFormatter.texto(s);
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

        if (!getIntent().getBooleanExtra(ReceitaIntentKeys.FETCH_FROM_BACKEND, false)) {
            return;
        }

        String auth = ApiAuthHeaders.bearerOrNull(this);
        long uid = getSharedPreferences("auth", MODE_PRIVATE).getLong("userId", 0L);
        if (auth == null || uid == 0L) {
            return;
        }

        long refeicaoId = getIntent().hasExtra(ReceitaIntentKeys.REFEICAO_ID)
                ? getIntent().getLongExtra(ReceitaIntentKeys.REFEICAO_ID, 0L)
                : 0L;
        String periodo = getIntent().getStringExtra(ReceitaIntentKeys.PERIODO_QUERY);
        String nomeBusca = getIntent().getStringExtra(ReceitaIntentKeys.TITULO);

        executor.execute(() -> buscarDetalhe(auth, uid, refeicaoId, periodo, nomeBusca));
    }

    private void aplicarCamposLocais(Intent intent) {
        textMomento.setText(vazio(intent.getStringExtra(ReceitaIntentKeys.MOMENTO)));
        textTitulo.setText(vazio(intent.getStringExtra(ReceitaIntentKeys.TITULO)));
        textTempo.setText(vazio(intent.getStringExtra(ReceitaIntentKeys.TEMPO)));
        textKcal.setText(vazio(intent.getStringExtra(ReceitaIntentKeys.KCAL)));
        textGluten.setText(ApiUiFormatter.VAZIO);
        textLactose.setText(ApiUiFormatter.VAZIO);
        textIngredientes.setText(vazio(intent.getStringExtra(ReceitaIntentKeys.INGREDIENTES)));
        textPreparo.setText(vazio(intent.getStringExtra(ReceitaIntentKeys.PREPARO)));
        textNutricional.setText(ApiUiFormatter.VAZIO);
    }

    private void buscarDetalhe(String authorization, long usuarioId,
                               long refeicaoId, @Nullable String periodo,
                               @Nullable String nomeBusca) {
        try {
            RefeicaoApiService api = RetrofitClient.getInstance().create(RefeicaoApiService.class);

            if (refeicaoId <= 0L && periodo != null && nomeBusca != null && !nomeBusca.trim().isEmpty()) {
                Response<List<RefeicaoResponse>> lista =
                        api.listar(authorization, periodo, usuarioId).execute();
                if (lista.isSuccessful() && lista.body() != null) {
                    String alvo = nomeBusca.trim();
                    for (RefeicaoResponse r : lista.body()) {
                        if (r != null && r.id != null && r.id > 0L
                                && r.nome != null && r.nome.trim().equalsIgnoreCase(alvo)) {
                            refeicaoId = r.id;
                            break;
                        }
                    }
                }
            }

            if (refeicaoId <= 0L) {
                runOnUiThread(() -> Toast.makeText(this,
                        "Não foi possível identificar a receita no servidor.",
                        Toast.LENGTH_SHORT).show());
                return;
            }

            Response<RefeicaoResponse> resp = api.buscarPorId(
                    authorization, refeicaoId, usuarioId, periodo).execute();

            if (!resp.isSuccessful()) {
                runOnUiThread(() -> Toast.makeText(this,
                        "Não foi possível carregar a receita (" + resp.code() + ").",
                        Toast.LENGTH_SHORT).show());
                return;
            }

            final RefeicaoResponse dto = resp.body();
            runOnUiThread(() -> {
                if (dto != null) {
                    aplicarDto(dto);
                } else {
                    Toast.makeText(this,
                            "Receita não encontrada no servidor.",
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
        String momento = dto.periodoLabel != null && !dto.periodoLabel.isBlank()
                ? dto.periodoLabel
                : getIntent().getStringExtra(ReceitaIntentKeys.MOMENTO);
        textMomento.setText(vazio(momento));
        textTitulo.setText(vazio(dto.nome));
        textTempo.setText(ApiUiFormatter.tempoMinutos(dto.tempoPreparo));
        textKcal.setText(ApiUiFormatter.caloriasResumo(dto.calorias));

        List<String> adequado = dto.adequadoPara != null
                ? dto.adequadoPara
                : Collections.emptyList();
        textGluten.setText(ApiUiFormatter.tagAdequado(adequado, "glúten"));
        textLactose.setText(ApiUiFormatter.tagAdequado(adequado, "lactose"));

        List<String> ing = dto.ingredientes != null ? dto.ingredientes : Collections.emptyList();
        textIngredientes.setText(ApiUiFormatter.listaIngredientes(ing));
        textPreparo.setText(vazio(RefeicaoConverters.textoPreparo(dto)));
        textNutricional.setText(
                ApiUiFormatter.montarInformacoesNutricionais(
                        dto.informacoesNutricionais,
                        dto.calorias));

        aplicarImagem(dto.imagemUrl);
    }

    private void aplicarImagem(@Nullable String url) {
        if (imageReceita == null) {
            return;
        }
        if (url == null || url.trim().isEmpty()
                || !url.trim().toLowerCase().startsWith("http")) {
            imageReceita.setImageResource(R.drawable.ic_refeicao);
            return;
        }
        // URLs do Drive e outras exigem tratamento específico; mantém ícone padrão por ora.
        imageReceita.setImageResource(R.drawable.ic_refeicao);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdownNow();
    }
}
