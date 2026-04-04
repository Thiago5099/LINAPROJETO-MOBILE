package com.example.projeto.models;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.projeto.R;
import com.example.projeto.adapter.ComprasAdapter;
import com.example.projeto.repository.RefeicaoRepository;

import java.util.*;

public class ComprasFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView txtProgresso;

    // Lista lógica
    private List<Ingrediente> listaFinal = new ArrayList<>();

    // Lista para exibição (com categorias)
    private List<ItemLista> listaExibicao = new ArrayList<>();

    private ComprasAdapter adapter;

    public ComprasFragment() {
        super(R.layout.fragment_compras);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        txtProgresso = view.findViewById(R.id.txtProgresso);

        // Pega dados do Repository
        List<Refeicao> refeicoes = RefeicaoRepository.getSelecionadas();

        View root = view;

        root.setOnApplyWindowInsetsListener((v, insets) -> {

            int topInset = insets.getSystemWindowInsetTop();

            v.setPadding(
                    v.getPaddingLeft(),
                    topInset,
                    v.getPaddingRight(),
                    v.getPaddingBottom()
            );

            return insets;
        });

        if (refeicoes != null && !refeicoes.isEmpty()) {
            processarIngredientes(refeicoes);
            montarListaComCategorias(listaFinal);
        }

        // Configura RecyclerView
        adapter = new ComprasAdapter(listaExibicao, this::atualizarProgresso);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        atualizarProgresso();
    }

    // Processa ingredientes (remove duplicados)
    private void processarIngredientes(List<Refeicao> refeicoes) {

        Map<String, Ingrediente> mapa = new HashMap<>();

        for (Refeicao r : refeicoes) {
            if (!r.isSelecionada()) continue;

            for (Ingrediente i : r.getIngredientes()) {

                if (mapa.containsKey(i.getNome())) {

                    Ingrediente existente = mapa.get(i.getNome());

                    existente.setQuantidade(
                            existente.getQuantidade() + i.getQuantidade()
                    );

                } else {

                    mapa.put(i.getNome(),
                            new Ingrediente(
                                    i.getNome(),
                                    i.getQuantidade(),
                                    i.getCategoria()
                            ));
                }
            }
        }

        listaFinal.clear();
        listaFinal.addAll(mapa.values());
    }

    // AGRUPA POR CATEGORIA
    private void montarListaComCategorias(List<Ingrediente> ingredientes) {

        Map<String, List<Ingrediente>> agrupado = new HashMap<>();

        for (Ingrediente i : ingredientes) {

            if (!agrupado.containsKey(i.getCategoria())) {
                agrupado.put(i.getCategoria(), new ArrayList<>());
            }

            agrupado.get(i.getCategoria()).add(i);
        }

        listaExibicao.clear();

        for (String categoria : agrupado.keySet()) {

            // HEADER
            listaExibicao.add(new ItemLista(categoria));

            // ITENS
            for (Ingrediente i : agrupado.get(categoria)) {
                listaExibicao.add(new ItemLista(i));
            }
        }
    }

    // Atualiza progresso
    private void atualizarProgresso() {

        int total = 0;
        int comprados = 0;

        for (ItemLista item : listaExibicao) {

            if (item.getTipo() == ItemLista.TIPO_ITEM) {

                total++;

                if (item.getIngrediente().isComprado()) {
                    comprados++;
                }
            }
        }

        int progresso = total == 0 ? 0 : (comprados * 100 / total);

        progressBar.setProgress(progresso);
        txtProgresso.setText(comprados + " de " + total + " itens comprados");
    }
}