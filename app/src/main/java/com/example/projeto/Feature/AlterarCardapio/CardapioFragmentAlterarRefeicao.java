package com.example.projeto.Feature.AlterarCardapio;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.*;

import com.example.projeto.R;

import java.util.*;

public class CardapioFragmentAlterarRefeicao extends Fragment {

    RecyclerView recycler;
    TextView txtStatus;

    Map<String, List<RefeicaoAlterarReifecao>> cardapios = new HashMap<>();
    Map<String, List<PratoAlterarRefeicao>> pratosPorTipo = new HashMap<>();

    String diaAtual = "Segunda";

    List<Button> botoesDias = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle b) {

        View view = inflater.inflate(R.layout.activity_cardapio_tela, container, false);

        recycler   = view.findViewById(R.id.recycler);
        txtStatus  = view.findViewById(R.id.txtStatus);

        recycler.setLayoutManager(new LinearLayoutManager(getContext()));

        criarDados();
        criarBotoesDias(view);
        atualizarLista();
        atualizarStatus();

        return view;
    }

    // Chamado pelo adapter via listener — abre MudarCardapioActivity com startActivityForResult
    public void abrirMudar(int posicao, String tipo, String nomeAtual, String infoAtual) {
        Intent it = new Intent(getContext(), MudarCardapioActivityAlterarRefeicao.class);
        it.putExtra("posicao",   posicao);
        it.putExtra("tipo",      tipo);
        it.putExtra("nomeAtual", nomeAtual);
        it.putExtra("infoAtual", infoAtual);
        startActivityForResult(it, 1);
    }

    private void criarDados() {

        List<PratoAlterarRefeicao> cafe = Arrays.asList(
                new PratoAlterarRefeicao("Omelete",           "Ovos",           "Frite os ovos",           250, 10),
                new PratoAlterarRefeicao("Torrada",           "Pão integral",   "Toste o pão",             150,  5),
                new PratoAlterarRefeicao("Vitamina de Frutas","Frutas, leite",  "Bata no liquidificador",  200, 10),
                new PratoAlterarRefeicao("Iogurte com Granola","Iogurte, granola","Misture na tigela",     180,  3)
        );

        List<PratoAlterarRefeicao> almoco = Arrays.asList(
                new PratoAlterarRefeicao("Arroz e Feijão",  "Arroz, feijão", "Cozinhe separado",          400, 25),
                new PratoAlterarRefeicao("Frango",          "Frango",        "Grelhe temperado",           350, 20),
                new PratoAlterarRefeicao("Macarrão",        "Massa, tomate", "Cozinhe e tempere",          500, 20),
                new PratoAlterarRefeicao("Salada de Atum",  "Atum, legumes", "Misture os ingredientes",   250, 10)
        );

        List<PratoAlterarRefeicao> lanche = Arrays.asList(
                new PratoAlterarRefeicao("Sanduíche",        "Pão, frango",    "Monte as camadas",         300,  5),
                new PratoAlterarRefeicao("Salada",           "Verduras",       "Misture e tempere",        150,  5),
                new PratoAlterarRefeicao("Salada de Frutas", "Frutas variadas","Pique e misture",          120,  5),
                new PratoAlterarRefeicao("Iogurte com Granola","Iogurte, granola","Misture na tigela",     180,  3)
        );

        List<PratoAlterarRefeicao> jantar = Arrays.asList(
                new PratoAlterarRefeicao("Macarrão",         "Massa",          "Cozinhe e tempere",        500, 20),
                new PratoAlterarRefeicao("Sopa",             "Legumes",        "Cozinhe e tempere",        200, 15),
                new PratoAlterarRefeicao("Omelete Simples",  "Ovos",           "Frite os ovos",            220, 10),
                new PratoAlterarRefeicao("Arroz com Legumes","Arroz, legumes", "Refogue e cozinhe",        350, 20)
        );

        pratosPorTipo.put("Café da manhã", cafe);
        pratosPorTipo.put("Almoço",        almoco);
        pratosPorTipo.put("Lanche",        lanche);
        pratosPorTipo.put("Jantar",        jantar);

        List<RefeicaoAlterarReifecao> base = Arrays.asList(
                new RefeicaoAlterarReifecao("Café da manhã", cafe.get(0)),
                new RefeicaoAlterarReifecao("Almoço",        almoco.get(0)),
                new RefeicaoAlterarReifecao("Lanche",        lanche.get(0)),
                new RefeicaoAlterarReifecao("Jantar",        jantar.get(0))
        );

        String[] dias = {"Segunda","Terça","Quarta","Quinta","Sexta","Sábado","Domingo"};

        for (String dia : dias) {
            List<RefeicaoAlterarReifecao> copia = new ArrayList<>();
            for (RefeicaoAlterarReifecao r : base) {
                PratoAlterarRefeicao p = new PratoAlterarRefeicao(
                        r.prato.nome, r.prato.ingredientes,
                        r.prato.preparo, r.prato.calorias, r.prato.tempo);
                copia.add(new RefeicaoAlterarReifecao(r.tipo, p));
            }
            cardapios.put(dia, copia);
        }
    }

    private void criarBotoesDias(View view) {

        LinearLayout layout = view.findViewById(R.id.layoutDias);
        String[] dias = {"Segunda","Terça","Quarta","Quinta","Sexta","Sábado","Domingo"};

        for (String dia : dias) {
            Button btn = new Button(getContext());
            btn.setText(dia);
            btn.setBackgroundResource(R.drawable.bg_cardapio_dia_nao_selecionado);
            btn.setTextColor(0xFF000000);
            btn.setPadding(40, 15, 40, 15);
            botoesDias.add(btn);

            btn.setOnClickListener(v -> {
                for (Button b : botoesDias) {
                    b.setBackgroundResource(R.drawable.bg_cardapio_dia_nao_selecionado);
                    b.setTextColor(0xFF000000);
                }
                btn.setBackgroundResource(R.drawable.bg_cardapio_dia_selecionado);
                btn.setTextColor(0xFFFFFFFF);
                diaAtual = dia;
                atualizarLista();
            });

            layout.addView(btn);
        }

        botoesDias.get(0).performClick();
    }

    private void atualizarLista() {
        recycler.setAdapter(new RefeicaoAdapterAlterarRefeicao(
                getContext(),
                cardapios.get(diaAtual),
                // Listener agora recebe nomeAtual e infoAtual também
                (posicao, tipo, nomeAtual, infoAtual) ->
                        abrirMudar(posicao, tipo, nomeAtual, infoAtual)
        ));
    }

    private void atualizarStatus() {
        int total = 0, preenchidos = 0;
        for (List<RefeicaoAlterarReifecao> lista : cardapios.values()) {
            total += lista.size();
            for (RefeicaoAlterarReifecao r : lista) {
                if (r.prato != null) preenchidos++;
            }
        }
        txtStatus.setText(preenchidos + "/" + total + " refeições planejadas");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {

            int pos      = data.getIntExtra("posicao", -1);
            String nome  = data.getStringExtra("nome");
            String tipo  = data.getStringExtra("tipo");

            if (pos == -1 || tipo == null || nome == null) return;

            List<PratoAlterarRefeicao> listaPratos = pratosPorTipo.get(tipo);
            if (listaPratos == null) return;

            for (PratoAlterarRefeicao p : listaPratos) {
                if (p.nome.equals(nome)) {
                    PratoAlterarRefeicao novo = new PratoAlterarRefeicao(
                            p.nome, p.ingredientes,
                            p.preparo, p.calorias, p.tempo);
                    cardapios.get(diaAtual).get(pos).prato = novo;
                    break;
                }
            }

            atualizarLista();
            atualizarStatus();
        }
    }
}
