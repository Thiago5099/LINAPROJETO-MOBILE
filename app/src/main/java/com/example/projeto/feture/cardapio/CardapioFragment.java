package com.example.projeto.feture.cardapio;

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

public class CardapioFragment extends Fragment {

    RecyclerView recycler;
    TextView txtStatus;

    Map<String, List<Refeicao>> cardapios = new HashMap<>();
    Map<String, List<Prato>> pratosPorTipo = new HashMap<>();

    String diaAtual = "Segunda";

    List<Button> botoesDias = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle b) {

        View view = inflater.inflate(R.layout.activity_cardapio_tela, container, false);

        recycler = view.findViewById(R.id.recycler);
        txtStatus = view.findViewById(R.id.txtStatus);

        recycler.setLayoutManager(new LinearLayoutManager(getContext()));

        criarDados();
        criarBotoesDias(view);
        atualizarLista();
        atualizarStatus();

        return view;
    }

    // CHAMADO PELO ADAPTER
    public void abrirSelecao(int posicao, String tipo) {
        Intent it = new Intent(getContext(), SelecionarPratoActivity.class);
        it.putExtra("posicao", posicao);
        it.putExtra("tipo", tipo);
        startActivityForResult(it, 1);
    }

    private void criarDados() {

        List<Prato> cafe = Arrays.asList(
                new Prato("Omelete","Ovos","Frite",250,10),
                new Prato("Torrada","Pão","Toste",150,5)
        );

        List<Prato> almoco = Arrays.asList(
                new Prato("Arroz e Feijão","Arroz","Cozinhe",400,25),
                new Prato("Frango","Frango","Grelhe",350,20)
        );

        List<Prato> lanche = Arrays.asList(
                new Prato("Sanduíche","Pão","Monte",300,5),
                new Prato("Salada","Verduras","Misture",150,5)
        );

        List<Prato> jantar = Arrays.asList(
                new Prato("Macarrão","Massa","Cozinhe",500,20),
                new Prato("Sopa","Legumes","Cozinhe",200,15)
        );

        pratosPorTipo.put("Café da manhã", cafe);
        pratosPorTipo.put("Almoço", almoco);
        pratosPorTipo.put("Lanche", lanche);
        pratosPorTipo.put("Jantar", jantar);

        List<Refeicao> base = Arrays.asList(
                new Refeicao("Café da manhã", cafe.get(0)),
                new Refeicao("Almoço", almoco.get(0)),
                new Refeicao("Lanche", lanche.get(0)),
                new Refeicao("Jantar", jantar.get(0))
        );

        String[] dias = {"Segunda","Terça","Quarta","Quinta","Sexta","Sábado","Domingo"};

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

        String[] dias = {"Segunda","Terça","Quarta","Quinta","Sexta","Sábado","Domingo"};

        for (String dia : dias) {

            Button btn = new Button(getContext());
            btn.setText(dia);

            btn.setBackgroundResource(R.drawable.bg_cardapio_dia_nao_selecionado);
            btn.setTextColor(0xFF000000);
            btn.setPadding(40,15,40,15);

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
        recycler.setAdapter(new RefeicaoAdapter(
                getContext(),
                cardapios.get(diaAtual),
                (posicao, tipo) -> abrirSelecao(posicao, tipo)
        ));
    }

    private void atualizarStatus() {

        int total = 0;
        int preenchidos = 0;

        for (List<Refeicao> lista : cardapios.values()) {
            total += lista.size();

            for (Refeicao r : lista) {
                if (r.prato != null) {
                    preenchidos++;
                }
            }
        }

        txtStatus.setText(preenchidos + "/" + total + " refeições planejadas");
    }

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
                atualizarStatus();
            }
        }
    }
}