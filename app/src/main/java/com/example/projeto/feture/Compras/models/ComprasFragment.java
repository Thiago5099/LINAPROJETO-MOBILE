package com.example.projeto.feture.Compras.models;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.projeto.R;
import com.example.projeto.feture.Compras.adapter.ComprasAdapter;

import java.util.*;

public class ComprasFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView txtProgresso;

    // Lista lógica
    private List<ComprasIngrediente> listaFinal = new ArrayList<>();

    // Lista para exibição (com categorias)
    private List<ComprasItemLista> listaExibicao = new ArrayList<>();

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
        //List<Refeicao> refeicoes = RefeicaoRepository.getSelecionadas();
        List<ComprasRefeicao> refeicoes = gerarDadosFake();

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
    private void processarIngredientes(List<ComprasRefeicao> refeicoes) {

        Map<String, ComprasIngrediente> mapa = new HashMap<>();

        for (ComprasRefeicao r : refeicoes) {
            if (!r.isSelecionada()) continue;

            for (ComprasIngrediente i : r.getIngredientes()) {

                if (mapa.containsKey(i.getNome())) {

                    ComprasIngrediente existente = mapa.get(i.getNome());

                    existente.setQuantidade(
                            existente.getQuantidade() + i.getQuantidade()
                    );

                } else {

                    mapa.put(i.getNome(),
                            new ComprasIngrediente(
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
    private void montarListaComCategorias(List<ComprasIngrediente> ingredientes) {

        Map<String, List<ComprasIngrediente>> agrupado = new HashMap<>();

        for (ComprasIngrediente i : ingredientes) {

            if (!agrupado.containsKey(i.getCategoria())) {
                agrupado.put(i.getCategoria(), new ArrayList<>());
            }

            agrupado.get(i.getCategoria()).add(i);
        }

        listaExibicao.clear();

        for (String categoria : agrupado.keySet()) {

            // HEADER
            listaExibicao.add(new ComprasItemLista(categoria));

            // ITENS
            for (ComprasIngrediente i : agrupado.get(categoria)) {
                listaExibicao.add(new ComprasItemLista(i));
            }
        }
    }

    // Atualiza progresso
    private void atualizarProgresso() {

        int total = 0;
        int comprados = 0;

        for (ComprasItemLista item : listaExibicao) {

            if (item.getTipo() == ComprasItemLista.TIPO_ITEM) {

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

    private List<ComprasRefeicao> gerarDadosFake() {

        List<ComprasRefeicao> lista = new ArrayList<>();

        // Ingredientes
        List<ComprasIngrediente> ingredientes1 = new ArrayList<>();
        ingredientes1.add(new ComprasIngrediente("Alface", 4, "Frutas e Vegetais"));
        ingredientes1.add(new ComprasIngrediente("Brócolis", 2, "Frutas e Vegetais"));
        ingredientes1.add(new ComprasIngrediente("Tomate", 5, "Frutas e Vegetais"));

        List<ComprasIngrediente> ingredientes2 = new ArrayList<>();
        ingredientes2.add(new ComprasIngrediente("Leite de Amêndoa", 3, "Laticínios"));
        ingredientes2.add(new ComprasIngrediente("Leite desnatado", 2, "Laticínios"));

        List<ComprasIngrediente> ingredientes3 = new ArrayList<>();
        ingredientes3.add(new ComprasIngrediente("Pão Integral", 8, "Grãos e Cereais"));
        ingredientes3.add(new ComprasIngrediente("Quinoa", 5, "Grãos e Cereais"));

        List<ComprasIngrediente> ingredientes4 = new ArrayList<>();
        ingredientes4.add(new ComprasIngrediente("Ovos", 8, "Proteínas"));
        ingredientes4.add(new ComprasIngrediente("Peito de Frango", 5, "Proteínas"));
        ingredientes4.add(new ComprasIngrediente("Atum em Lata", 5, "Proteínas"));

        // Refeições fake
        lista.add(new ComprasRefeicao("Refeição 1", ingredientes1, true));
        lista.add(new ComprasRefeicao("Refeição 2", ingredientes2, true));
        lista.add(new ComprasRefeicao("Refeição 3", ingredientes3, true));
        lista.add(new ComprasRefeicao("Refeição 4", ingredientes4, true));

        return lista;
    }
}