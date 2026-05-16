package com.example.projeto.Feature.Cardapio;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.*;

import com.example.projeto.R;
import com.example.projeto.Feature.CriarCardapio.CriarCardapioMainActivity;
import com.example.projeto.Feature.Login.ApiAuthHeaders;
import com.example.projeto.Feature.Login.LoginCadastro;

import java.util.*;

public class CardapioFragment extends Fragment {

    RecyclerView recycler;
    TextView txtStatus;

    /** Dia da semana (rótulo) → refeições salvas pelo + Criar ou editadas por Mudar. */
    Map<String, List<Refeicao>> cardapios = new LinkedHashMap<>();

    String diaAtual = "Segunda";

    List<Button> botoesDias = new ArrayList<>();

    private ActivityResultLauncher<Intent> mudarCardapioLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mudarCardapioLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (!isAdded()) return;
                    if (result.getResultCode() != Activity.RESULT_OK || result.getData() == null) {
                        return;
                    }
                    Intent data = result.getData();
                    int pos = data.getIntExtra("posicao", -1);
                    String tipo = data.getStringExtra("tipo");
                    String nome = data.getStringExtra("nome");
                    if (pos < 0 || tipo == null || nome == null) return;

                    String ing = data.getStringExtra("ingredientes");
                    String prep = data.getStringExtra("preparo");
                    if (ing == null) ing = "";
                    if (prep == null) prep = "";
                    int cal = data.getIntExtra("calorias", 0);
                    int tempo = data.getIntExtra("tempo", 0);

                    aplicarTrocaNoDiaAtual(pos, tipo, nome, ing, prep, cal, tempo);
                });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle b) {

        View view = inflater.inflate(R.layout.activity_cardapio_tela, container, false);

        recycler = view.findViewById(R.id.recycler);
        txtStatus = view.findViewById(R.id.txtStatus);

        view.findViewById(R.id.btnCriarCadapio).setOnClickListener(v ->
                startActivity(new Intent(getContext(), CriarCardapioMainActivity.class)));

        view.findViewById(R.id.btnLogin).setOnClickListener(v ->
                startActivity(new Intent(getContext(), LoginCadastro.class)));

        recycler.setLayoutManager(new LinearLayoutManager(getContext()));

        recarregarDoArmazenamento();
        criarBotoesDias(view);
        atualizarStatus();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isAdded() || getView() == null) return;
        recarregarDoArmazenamento();
        atualizarLista();
        atualizarStatus();
    }

    private void recarregarDoArmazenamento() {
        cardapios.clear();
        cardapios.putAll(CardapioLocalStore.carregarMapaRefeicoes(requireContext()));
    }

    private void criarBotoesDias(View view) {

        LinearLayout layout = view.findViewById(R.id.layoutDias);
        layout.removeAllViews();
        botoesDias.clear();

        String[] dias = {"Segunda", "Terça", "Quarta", "Quinta", "Sexta", "Sábado", "Domingo"};

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

        if (!botoesDias.isEmpty()) {
            botoesDias.get(0).performClick();
        }
    }

    private void atualizarLista() {
        List<Refeicao> lista = cardapios.get(diaAtual);
        if (lista == null) {
            lista = Collections.emptyList();
        }
        recycler.setAdapter(new RefeicaoAdapter(getContext(), lista,
                (posicao, tipo, nomeAtual, infoAtual) ->
                        abrirMudar(posicao, tipo, nomeAtual, infoAtual)));
    }

    public void abrirMudar(int posicao, String tipo, String nomeAtual, String infoAtual) {
        if (!CardapioLocalStore.temCardapioSalvo(requireContext())) {
            Toast.makeText(requireContext(),
                    "Monte e salve um cardápio em + Criar antes de trocar refeições.",
                    Toast.LENGTH_LONG).show();
            return;
        }
        String auth = ApiAuthHeaders.bearerOrNull(requireContext());
        long userId = requireContext().getSharedPreferences("auth", Context.MODE_PRIVATE)
                .getLong("userId", 0L);
        if (auth == null || userId == 0L) {
            Toast.makeText(requireContext(),
                    "Faça login para buscar outras refeições no servidor.",
                    Toast.LENGTH_LONG).show();
            return;
        }
        Intent it = new Intent(requireContext(), MudarCardapioActivity.class);
        it.putExtra("posicao", posicao);
        it.putExtra("tipo", tipo);
        it.putExtra("nomeAtual", nomeAtual);
        it.putExtra("infoAtual", infoAtual);
        it.putExtra("usuarioId", userId);
        it.putExtra("authorization", auth);
        mudarCardapioLauncher.launch(it);
    }

    private void aplicarTrocaNoDiaAtual(int pos, String tipo, String nome,
            String ingredientes, String preparo, int calorias, int tempo) {
        List<Refeicao> lista = cardapios.get(diaAtual);
        if (lista == null || pos < 0 || pos >= lista.size()) return;

        Refeicao ref = lista.get(pos);
        if (ref.prato == null) {
            ref.prato = new Prato(nome, ingredientes, preparo, calorias, tempo);
        } else {
            ref.prato.nome = nome;
            ref.prato.ingredientes = ingredientes.isEmpty() ? "—" : ingredientes;
            ref.prato.preparo = preparo.isEmpty() ? "—" : preparo;
            ref.prato.calorias = calorias;
            ref.prato.tempo = tempo;
        }
        ref.tipo = tipo;

        CardapioLocalStore.salvarDoMapa(requireContext(), cardapios);
        atualizarLista();
        atualizarStatus();
        Toast.makeText(requireContext(), "Refeição atualizada", Toast.LENGTH_SHORT).show();
    }

    private void atualizarStatus() {
        if (!CardapioLocalStore.temCardapioSalvo(requireContext())) {
            txtStatus.setText("Nenhum cardápio salvo — use + Criar para montar a semana");
            return;
        }
        int total = 0;
        for (List<Refeicao> lista : cardapios.values()) {
            total += lista.size();
        }
        txtStatus.setText(total + " refeições na semana — use Mudar para trocar um prato");
    }
}
