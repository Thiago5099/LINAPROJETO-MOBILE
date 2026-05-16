package com.example.projeto.Feature.Cardapio;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projeto.R;
import com.example.projeto.Feature.Nutricionistas.RetrofitClient;
import com.example.projeto.Feature.Refeicoes.PeriodoMapeador;
import com.example.projeto.Feature.Refeicoes.RefeicaoApiService;
import com.example.projeto.Feature.Refeicoes.RefeicaoConverters;
import com.example.projeto.Feature.Refeicoes.RefeicaoResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Response;

public class MudarCardapioActivity extends AppCompatActivity {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mudar_cardapio);

        int posicao = getIntent().getIntExtra("posicao", -1);
        String tipo = getIntent().getStringExtra("tipo");
        String nomeAtual = getIntent().getStringExtra("nomeAtual");
        String infoAtual = getIntent().getStringExtra("infoAtual");
        long usuarioId = getIntent().getLongExtra("usuarioId", 0L);
        String authorization = getIntent().getStringExtra("authorization");

        ((TextView) findViewById(R.id.txtTipoAtual)).setText(tipo);
        ((TextView) findViewById(R.id.txtNomeAtual)).setText(nomeAtual);
        ((TextView) findViewById(R.id.txtInfoAtual)).setText(infoAtual);

        findViewById(R.id.btnVerReceitaAtual).setOnClickListener(v -> {
            Intent it = new Intent(this, ReceitaActivity.class);
            it.putExtra("nome", nomeAtual);
            startActivity(it);
        });

        RecyclerView recycler = findViewById(R.id.recyclerOpcoes);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        findViewById(R.id.btnVoltar).setOnClickListener(v -> finish());
        findViewById(R.id.btnContinuarMesma).setOnClickListener(v -> finish());

        String periodo = PeriodoMapeador.uiParaQuery(tipo);
        if (periodo == null || usuarioId == 0L || authorization == null || authorization.isEmpty()) {
            Toast.makeText(this, "Sessão inválida. Faça login novamente.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        executor.execute(() -> {
            try {
                RefeicaoApiService api = RetrofitClient.getInstance().create(RefeicaoApiService.class);
                Response<List<RefeicaoResponse>> resp =
                        api.listar(authorization, periodo, usuarioId).execute();
                if (!resp.isSuccessful()) {
                    runOnUiThread(() -> {
                        Toast.makeText(this,
                                "Erro ao carregar opções (" + resp.code() + ")",
                                Toast.LENGTH_LONG).show();
                        finish();
                    });
                    return;
                }
                List<RefeicaoResponse> body = resp.body();
                if (body == null) body = Collections.emptyList();
                List<Prato> opcoes = new ArrayList<>();
                for (RefeicaoResponse r : body) {
                    Prato p = RefeicaoConverters.paraPrato(r);
                    if (nomeAtual == null || !p.nome.equals(nomeAtual)) {
                        opcoes.add(p);
                    }
                }
                runOnUiThread(() -> {
                    recycler.setAdapter(new OpcaoAdapter(this, opcoes, prato -> {
                        Intent result = new Intent();
                        result.putExtra("posicao", posicao);
                        result.putExtra("tipo", tipo);
                        result.putExtra("nome", prato.nome);
                        result.putExtra("ingredientes",
                                prato.ingredientes != null ? prato.ingredientes : "");
                        result.putExtra("preparo", prato.preparo != null ? prato.preparo : "");
                        result.putExtra("calorias", prato.calorias);
                        result.putExtra("tempo", prato.tempo);
                        setResult(Activity.RESULT_OK, result);
                        finish();
                    }));
                });
            } catch (IOException e) {
                runOnUiThread(() -> {
                    Toast.makeText(this,
                            "Falha de rede: " + (e.getMessage() != null ? e.getMessage() : "erro"),
                            Toast.LENGTH_LONG).show();
                    finish();
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdownNow();
    }

    static class OpcaoAdapter extends RecyclerView.Adapter<OpcaoAdapter.VH> {

        interface OnEscolha { void onEscolha(Prato prato); }

        Context context;
        List<Prato> lista;
        OnEscolha listener;

        OpcaoAdapter(Context c, List<Prato> l, OnEscolha listener) {
            context = c;
            lista = l;
            this.listener = listener;
        }

        static class VH extends RecyclerView.ViewHolder {
            TextView nome, info;
            Button btnVerReceita;

            VH(View v) {
                super(v);
                nome = v.findViewById(R.id.nome);
                info = v.findViewById(R.id.info);
                btnVerReceita = v.findViewById(R.id.btnVerReceita);
                TextView t = v.findViewById(R.id.tipo);
                if (t != null) t.setVisibility(View.GONE);
            }
        }

        @Override
        public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(context)
                    .inflate(R.layout.item_mudar_cardapio_opcao, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(VH h, int i) {
            Prato p = lista.get(i);
            h.nome.setText(p.nome);
            h.info.setText("⏱ " + p.tempo + " min  ⚡ " + p.calorias + " kcal");

            h.btnVerReceita.setOnClickListener(v -> {
                if (listener != null) listener.onEscolha(p);
            });

            h.itemView.setOnClickListener(v -> {
                if (listener != null) listener.onEscolha(p);
            });
        }

        @Override
        public int getItemCount() {
            return lista.size();
        }
    }
}
