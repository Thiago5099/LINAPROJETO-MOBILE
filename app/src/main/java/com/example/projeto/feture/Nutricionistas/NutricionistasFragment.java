package com.example.projeto.feture.Nutricionistas;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projeto.R;

import java.util.ArrayList;

public class NutricionistasFragment extends Fragment {

    private NutricionistasBancoHelper banco;
    private NutricionistaAdapter adapter;
    private ArrayList<Nutricionista> listaFiltrada;

    private String cidadeSelecionada = "Cidade";
    private int avaliacaoSelecionada = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_nutricionistas, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        banco = new NutricionistasBancoHelper(requireContext());

        if (banco.listarTodos().isEmpty()) {
            banco.inserir("Ana Lima", "Esportiva", "São Paulo", "(11) 91234-5678", "ana@gmail.com", "150 + pacientes atendidos", 1.0f);
            banco.inserir("Carlos Souza", "Clínica", "Campinas", "(19) 98765-4321", "carlos@gmail.com", "50 + pacientes atendidos", 2.0f);
            banco.inserir("Maria Silva", "Infantil", "Santos", "(13) 99876-5432", "maria@gmail.com", "200 + pacientes atendidos", 3.0f);
            banco.inserir("João Santos", "Clínica", "São Paulo", "(11) 91234-5678", "joao@gmail.com", "100 + pacientes atendidos", 4.0f);
            banco.inserir("Maria Oliveira", "Infantil", "Campinas", "(19) 98765-4321", "maria2@gmail.com", "50 + pacientes atendidos", 3.0f);
            banco.inserir("Pedro Almeida", "Clínica", "Santos", "(13) 99876-5432", "pedro@gmail.com", "200 + pacientes atendidos", 4.0f);
            banco.inserir("Ana Pereira", "Esportiva", "São Paulo", "(11) 91234-5678", "anap@gmail.com", "150 + pacientes atendidos", 5.0f);
            banco.inserir("Carlos Silva", "Clínica", "Campinas", "(19) 98765-4321", "carlosp@gmail.com", "50 + pacientes atendidos", 5.0f);
            banco.inserir("Maria Santos", "Infantil", "Santos", "(13) 99876-5432", "marias@gmail.com", "200 + pacientes atendidos", 3.0f);
        }

        // RecyclerView
        listaFiltrada = banco.listarTodos();
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new NutricionistaAdapter(requireContext(), listaFiltrada);
        recyclerView.setAdapter(adapter);

        // Spinner Cidade
        Spinner spinnerCidade = view.findViewById(R.id.spinnerCidade);
        ArrayAdapter<String> adapterCidade = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item,
                new String[]{"Cidade", "São Paulo", "Campinas", "Santos"});
        adapterCidade.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCidade.setAdapter(adapterCidade);

        // Spinner Avaliação com estrelas
        Spinner spinnerAvaliacao = view.findViewById(R.id.spinnerAvaliacao);
        spinnerAvaliacao.setAdapter(new NutricionistasEstrelasSpinnerAdapter(requireContext()));

        // Listeners
        AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getId() == R.id.spinnerCidade) {
                    cidadeSelecionada = parent.getItemAtPosition(position).toString();
                } else if (parent.getId() == R.id.spinnerAvaliacao) {
                    avaliacaoSelecionada = position;
                }
                aplicarFiltros();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        };

        spinnerCidade.setOnItemSelectedListener(listener);
        spinnerAvaliacao.setOnItemSelectedListener(listener);
    }

    private void aplicarFiltros() {
        ArrayList<Nutricionista> todos = banco.listarTodos();
        ArrayList<Nutricionista> resultado = new ArrayList<>();

        for (Nutricionista n : todos) {
            boolean passaCidade = cidadeSelecionada.equals("Cidade") || n.getCidade().equals(cidadeSelecionada);
            boolean passaAvaliacao = true;

            if (avaliacaoSelecionada != 0) {
                passaAvaliacao = Math.round(n.getAvaliacao()) == avaliacaoSelecionada;
            }

            if (passaCidade && passaAvaliacao) {
                resultado.add(n);
            }
        }

        listaFiltrada.clear();
        listaFiltrada.addAll(resultado);
        adapter.notifyDataSetChanged();
    }
}