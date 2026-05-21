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
import com.example.projeto.Data.BancoHelper;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MudarCardapioActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mudar_cardapio);

        int posicao = getIntent().getIntExtra("posicao", -1);
        String tipo = getIntent().getStringExtra("tipo");
        String nomeAtual = getIntent().getStringExtra("nomeAtual");
        String infoAtual = getIntent().getStringExtra("infoAtual");

        final String periodo = tipo;

        ((TextView) findViewById(R.id.txtTipoAtual)).setText(tipo);
        ((TextView) findViewById(R.id.txtNomeAtual)).setText(nomeAtual);
        ((TextView) findViewById(R.id.txtInfoAtual)).setText(infoAtual);

        findViewById(R.id.btnVerReceitaAtual).setOnClickListener(v -> {
            Intent it = new Intent(this, ReceitaActivity.class);
            ReceitaActivity.putRecipeExtras(it,
                    tipo,
                    null,
                    nomeAtual,
                    extrairTempoFmt(infoAtual),
                    extrairKcalFmt(infoAtual),
                    null,
                    null,
                    tipo);
            startActivity(it);
        });

        RecyclerView recycler = findViewById(R.id.recyclerOpcoes);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        findViewById(R.id.buttonVoltarReceita).setOnClickListener(v -> finish());
        findViewById(R.id.btnContinuarMesma).setOnClickListener(v -> finish());

        List<Prato> opcoes = new BancoHelper(this).listarPratosPorPeriodo(periodo, nomeAtual);
        recycler.setAdapter(new OpcaoAdapter(this, opcoes, tipo, periodo, prato -> {
            Intent result = new Intent();
            result.putExtra("posicao", posicao);
            result.putExtra("tipo", tipo);
            result.putExtra("nome", prato.nome);
            result.putExtra("ingredientes", prato.ingredientes != null ? prato.ingredientes : "");
            result.putExtra("preparo", prato.preparo != null ? prato.preparo : "");
            result.putExtra("calorias", prato.calorias);
            result.putExtra("tempo", prato.tempo);
            result.putExtra("refeicaoId", prato.refeicaoId);
            setResult(Activity.RESULT_OK, result);
            finish();
        }));
    }

    private static String extrairTempoFmt(String infoAtual) {
        if (infoAtual == null) return null;
        Matcher m = Pattern.compile("(\\d+)\\s*min").matcher(infoAtual);
        return m.find() ? m.group(1) + " min" : null;
    }

    private static String extrairKcalFmt(String infoAtual) {
        if (infoAtual == null) return null;
        Matcher m = Pattern.compile("(\\d+)\\s*kcal").matcher(infoAtual);
        return m.find() ? m.group(1) + " kcal" : null;
    }

    static class OpcaoAdapter extends RecyclerView.Adapter<OpcaoAdapter.VH> {

        interface OnEscolha { void onEscolha(Prato prato); }

        Context context;
        List<Prato> lista;
        OnEscolha listener;
        String tipoMomento;
        String periodoQuery;

        OpcaoAdapter(Context c, List<Prato> l, String tipoMomento, String periodoQuery, OnEscolha listener) {
            context = c;
            lista = l;
            this.tipoMomento = tipoMomento;
            this.periodoQuery = periodoQuery;
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
                Intent it = new Intent(context, ReceitaActivity.class);
                Long refId = p.refeicaoId > 0L ? p.refeicaoId : null;
                ReceitaActivity.putRecipeExtras(it,
                        tipoMomento,
                        refId,
                        p.nome,
                        p.tempo > 0 ? p.tempo + " min" : null,
                        p.calorias > 0 ? p.calorias + " kcal" : null,
                        p.ingredientes,
                        p.preparo,
                        periodoQuery);
                context.startActivity(it);
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
