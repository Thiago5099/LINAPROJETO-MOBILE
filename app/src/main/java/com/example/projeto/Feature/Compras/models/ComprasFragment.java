package com.example.projeto.Feature.Compras.models;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projeto.Feature.Compras.CategoriaComprasMapeador;
import com.example.projeto.Feature.Compras.ComprasListaCarregador;
import com.example.projeto.Feature.Compras.adapter.ComprasAdapter;
import com.example.projeto.Feature.Login.ApiAuthHeaders;
import com.example.projeto.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ComprasFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView txtProgresso;

    private final List<ComprasIngrediente> listaFinal = new ArrayList<>();
    private final List<ComprasItemLista> listaExibicao = new ArrayList<>();

    private ComprasAdapter adapter;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public ComprasFragment() {
        super(R.layout.fragment_compras);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        txtProgresso = view.findViewById(R.id.txtProgresso);

        adapter = new ComprasAdapter(listaExibicao, this::atualizarProgresso);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        view.setOnApplyWindowInsetsListener((v, insets) -> {
            int topInset = insets.getSystemWindowInsetTop();
            v.setPadding(
                    v.getPaddingLeft(),
                    topInset,
                    v.getPaddingRight(),
                    v.getPaddingBottom()
            );
            return insets;
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        carregarLista();
    }

    private void carregarLista() {
        if (!isAdded()) return;

        String authorization = ApiAuthHeaders.bearerOrNull(requireContext());
        if (authorization == null) {
            Toast.makeText(requireContext(),
                    "Faça login para ver a lista de compras", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences prefs = requireContext()
                .getSharedPreferences("auth", Context.MODE_PRIVATE);
        long userId = prefs.getLong("userId", 0L);
        if (userId == 0L) {
            Toast.makeText(requireContext(),
                    "Faça login novamente", Toast.LENGTH_SHORT).show();
            return;
        }

        recyclerView.setAlpha(0.4f);

        executor.execute(() -> {
            try {
                List<ComprasIngrediente> ingredientes =
                        ComprasListaCarregador.carregar(requireContext(), authorization, userId);

                if (!isAdded()) return;

                requireActivity().runOnUiThread(() -> {
                    recyclerView.setAlpha(1f);
                    listaFinal.clear();
                    listaFinal.addAll(ingredientes);
                    montarListaComCategorias(listaFinal);
                    adapter.notifyDataSetChanged();
                    atualizarProgresso();

                    if (ingredientes.isEmpty()) {
                        Toast.makeText(requireContext(),
                                "Monte seu cardápio para gerar a lista de compras",
                                Toast.LENGTH_LONG).show();
                    }
                });
            } catch (Exception e) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    recyclerView.setAlpha(1f);
                    Toast.makeText(requireContext(),
                            "Não foi possível carregar a lista de compras",
                            Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void montarListaComCategorias(List<ComprasIngrediente> ingredientes) {
        Map<String, List<ComprasIngrediente>> agrupado = new HashMap<>();

        for (ComprasIngrediente i : ingredientes) {
            agrupado.computeIfAbsent(i.getCategoria(), k -> new ArrayList<>()).add(i);
        }

        List<String> categorias = new ArrayList<>(agrupado.keySet());
        Collections.sort(categorias, Comparator.comparingInt(CategoriaComprasMapeador::ordem));

        listaExibicao.clear();

        for (String categoria : categorias) {
            List<ComprasIngrediente> itens = agrupado.get(categoria);
            listaExibicao.add(new ComprasItemLista(categoria, itens.size()));
            for (ComprasIngrediente i : itens) {
                listaExibicao.add(new ComprasItemLista(i));
            }
        }
    }

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
}
