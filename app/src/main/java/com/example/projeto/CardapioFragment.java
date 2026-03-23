package com.example.projeto;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.*;

import com.example.projeto.adapter.RefeicaoAdapter;
import com.example.projeto.models.*;

import java.util.*;

public class CardapioFragment extends Fragment {

    RecyclerView recycler;

    // cardápio por dia
    Map<String, List<Refeicao>> cardapios = new HashMap<>();

    // pratos disponíveis por tipo
    Map<String, List<Prato>> pratosPorTipo = new HashMap<>();

    String diaAtual = "Segunda";

    List<Button> botoesDias = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle b) {

        View view = inflater.inflate(R.layout.activity_tela_cardapio, container, false);

        recycler = view.findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));

        criarDados();
        criarBotoesDias(view);
        atualizarLista();

        return view;
    }

    // 🔥 método que o adapter chama
    public void abrirSelecao(int posicao, String tipo) {

        Intent it = new Intent(getContext(), SelecionarPratoActivity.class);

        it.putExtra("posicao", posicao);
        it.putExtra("tipo", tipo);

        startActivityForResult(it, 1);
    }

    private void criarDados() {

        List<Prato> cafe = new ArrayList<>();
        cafe.add(new Prato("Omelete","Ovos e queijo","Frite tudo",250,10));
        cafe.add(new Prato("Torrada","Pão","Toste o pão",150,5));

        List<Prato> almoco = new ArrayList<>();
        almoco.add(new Prato("Arroz e Feijão","Arroz, feijão","Cozinhe",400,25));
        almoco.add(new Prato("Frango","Frango","Grelhe",350,20));

        List<Prato> lanche = new ArrayList<>();
        lanche.add(new Prato("Sanduíche","Pão e recheio","Monte",300,5));
        lanche.add(new Prato("Salada","Verduras","Misture",150,5));

        List<Prato> jantar = new ArrayList<>();
        jantar.add(new Prato("Macarrão","Massa e molho","Cozinhe",500,20));
        jantar.add(new Prato("Sopa","Legumes","Cozinhe",200,15));

        pratosPorTipo.put("Café da manhã", cafe);
        pratosPorTipo.put("Almoço", almoco);
        pratosPorTipo.put("Lanche", lanche);
        pratosPorTipo.put("Jantar", jantar);

        List<Refeicao> base = new ArrayList<>();

        base.add(new Refeicao("Café da manhã", cafe.get(0)));
        base.add(new Refeicao("Almoço", almoco.get(0)));
        base.add(new Refeicao("Lanche", lanche.get(0)));
        base.add(new Refeicao("Jantar", jantar.get(0)));

        String[] dias = {
                "Segunda","Terça","Quarta",
                "Quinta","Sexta","Sábado","Domingo"
        };

        for (String dia : dias) {

            List<Refeicao> copia = new ArrayList<>();

            for (Refeicao r : base) {

                Prato p = new Prato(
                        r.prato.nome,
                        r.prato.ingredientes,
                        r.prato.preparo,
                        r.prato.calorias,
                        r.prato.tempo
                );

                copia.add(new Refeicao(r.tipo, p));
            }

            cardapios.put(dia, copia);
        }
    }

    private void criarBotoesDias(View view) {

        LinearLayout layout = view.findViewById(R.id.layoutDias);

        String[] dias = {
                "Segunda","Terça","Quarta",
                "Quinta","Sexta","Sábado","Domingo"
        };

        for (String dia : dias) {

            Button btn = new Button(getContext());
            btn.setText(dia);

            btn.setBackgroundColor(0xFFE0E0E0);

            botoesDias.add(btn);

            btn.setOnClickListener(v -> {

                for (Button b : botoesDias) {
                    b.setBackgroundColor(0xFFE0E0E0);
                }

                btn.setBackgroundColor(0xFF4CAF50);

                diaAtual = dia;
                atualizarLista();
            });

            layout.addView(btn);
        }

        botoesDias.get(0).performClick();
    }

    private void atualizarLista() {
        recycler.setAdapter(new RefeicaoAdapter(getContext(), cardapios.get(diaAtual)));
    }

    // RECEBER RESULTADO DA SELEÇÃO
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {

            int pos = data.getIntExtra("posicao", -1);
            String nome = data.getStringExtra("nome");
            String tipo = data.getStringExtra("tipo");

            if (pos != -1 && tipo != null) {

                List<Prato> listaPratos = pratosPorTipo.get(tipo);

                for (Prato p : listaPratos) {
                    if (p.nome.equals(nome)) {

                        Prato novo = new Prato(
                                p.nome,
                                p.ingredientes,
                                p.preparo,
                                p.calorias,
                                p.tempo
                        );

                        cardapios.get(diaAtual).get(pos).prato = novo;
                        break;
                    }
                }

                atualizarLista();
            }
        }
    }
}