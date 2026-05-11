package com.example.projeto.Feature.AlterarCardapio;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MudarCardapioActivityAlterarRefeicao extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mudar_cardapio);

        int posicao      = getIntent().getIntExtra("posicao", -1);
        String tipo      = getIntent().getStringExtra("tipo");
        String nomeAtual = getIntent().getStringExtra("nomeAtual");
        String infoAtual = getIntent().getStringExtra("infoAtual");

        // Preenche card do prato atual
        ((TextView) findViewById(R.id.txtTipoAtual)).setText(tipo);
        ((TextView) findViewById(R.id.txtNomeAtual)).setText(nomeAtual);
        ((TextView) findViewById(R.id.txtInfoAtual)).setText(infoAtual);

        // Botão ver receita do prato atual
        findViewById(R.id.btnVerReceitaAtual).setOnClickListener(v -> {
            Intent it = new Intent(this, ReceitaActivityAlterarRefeicao.class);
            it.putExtra("nome", nomeAtual);
            startActivity(it);
        });

        // Monta lista de opções JÁ sem o prato atual
        List<PratoAlterarRefeicao> todasOpcoes = obterOpcoesPorTipo(tipo);
        List<PratoAlterarRefeicao> opcoesFiltradas = new ArrayList<>();
        for (PratoAlterarRefeicao p : todasOpcoes) {
            if (!p.nome.equals(nomeAtual)) {
                opcoesFiltradas.add(p);
            }
        }

        // RecyclerView das opções
        RecyclerView recycler = findViewById(R.id.recyclerOpcoes);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(new OpcaoAdapter(this, opcoesFiltradas, prato -> {
            // Devolve o prato escolhido para o CardapioFragment via result
            Intent result = new Intent();
            result.putExtra("posicao", posicao);
            result.putExtra("tipo",    tipo);
            result.putExtra("nome",    prato.nome);
            setResult(Activity.RESULT_OK, result);
            finish();
        }));

        // Botão voltar
        findViewById(R.id.btnVoltar).setOnClickListener(v -> finish());

        // Continuar com a mesma refeição
        findViewById(R.id.btnContinuarMesma).setOnClickListener(v -> finish());
    }

    // Retorna todas as opções disponíveis para o tipo de refeição
    // Substitua pela sua fonte de dados real (repositório, BD, etc.)
    private List<PratoAlterarRefeicao> obterOpcoesPorTipo(String tipo) {
        if (tipo == null) return new ArrayList<>();
        switch (tipo) {
            case "Café da manhã":
                return Arrays.asList(
                    new PratoAlterarRefeicao("Omelete",          "Ovos",           "Frite os ovos",            250, 10),
                    new PratoAlterarRefeicao("Torrada",           "Pão integral",   "Toste o pão",              150,  5),
                    new PratoAlterarRefeicao("Vitamina de Frutas","Frutas, leite",  "Bata no liquidificador",   200, 10),
                    new PratoAlterarRefeicao("Iogurte com Granola","Iogurte, granola","Misture na tigela",      180,  3)
                );
            case "Almoço":
                return Arrays.asList(
                    new PratoAlterarRefeicao("Arroz e Feijão",   "Arroz, feijão",  "Cozinhe separado",         400, 25),
                    new PratoAlterarRefeicao("Frango",            "Frango",         "Grelhe temperado",         350, 20),
                    new PratoAlterarRefeicao("Macarrão",          "Massa, tomate",  "Cozinhe e tempere",        500, 20),
                    new PratoAlterarRefeicao("Salada de Atum",    "Atum, legumes",  "Misture os ingredientes",  250, 10)
                );
            case "Lanche":
                return Arrays.asList(
                    new PratoAlterarRefeicao("Sanduíche",         "Pão, frango",    "Monte as camadas",         300,  5),
                    new PratoAlterarRefeicao("Salada",            "Verduras",       "Misture e tempere",        150,  5),
                    new PratoAlterarRefeicao("Salada de Frutas",  "Frutas variadas","Pique e misture",          120,  5),
                    new PratoAlterarRefeicao("Iogurte com Granola","Iogurte, granola","Misture na tigela",      180,  3)
                );
            case "Jantar":
                return Arrays.asList(
                    new PratoAlterarRefeicao("Macarrão",          "Massa",          "Cozinhe e tempere",        500, 20),
                    new PratoAlterarRefeicao("Sopa",              "Legumes",        "Cozinhe e tempere",        200, 15),
                    new PratoAlterarRefeicao("Omelete Simples",   "Ovos",           "Frite os ovos",            220, 10),
                    new PratoAlterarRefeicao("Arroz com Legumes", "Arroz, legumes", "Refogue e cozinhe",        350, 20)
                );
            default:
                return new ArrayList<>();
        }
    }

    // Adapter interno das opções de troca
    static class OpcaoAdapter extends RecyclerView.Adapter<OpcaoAdapter.VH> {

        interface OnEscolha { void onEscolha(PratoAlterarRefeicao prato); }

        Context context;
        List<PratoAlterarRefeicao> lista;
        OnEscolha listener;

        OpcaoAdapter(Context c, List<PratoAlterarRefeicao> l, OnEscolha listener) {
            context  = c;
            lista    = l;
            this.listener = listener;
        }

        static class VH extends RecyclerView.ViewHolder {
            TextView nome, info;
            Button btnVerReceita;
            VH(View v) {
                super(v);
                nome        = v.findViewById(R.id.nome);
                info        = v.findViewById(R.id.info);
                btnVerReceita = v.findViewById(R.id.btnVerReceita);
                // oculta o campo tipo pois já está visível no card atual
                TextView tipo = v.findViewById(R.id.tipo);
                if (tipo != null) tipo.setVisibility(View.GONE);
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
            PratoAlterarRefeicao p = lista.get(i);
            h.nome.setText(p.nome);
            h.info.setText("⏱ " + p.tempo + " min  ⚡ " + p.calorias + " kcal");

            // Clique em "Ver Receita" seleciona o prato
            h.btnVerReceita.setOnClickListener(v -> {
                if (listener != null) listener.onEscolha(p);
            });

            // Clique no card também seleciona
            h.itemView.setOnClickListener(v -> {
                if (listener != null) listener.onEscolha(p);
            });
        }

        @Override
        public int getItemCount() { return lista.size(); }
    }
}
