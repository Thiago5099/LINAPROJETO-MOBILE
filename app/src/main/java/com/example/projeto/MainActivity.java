package com.example.projeto;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private BancoHelper banco;
    private NutricionistaAdapter adapter;
    private ArrayList<Nutricionista> listaFiltrada;

    private String cidadeSelecionada = "Cidade";
    private int avaliacaoSelecionada = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        banco = new BancoHelper(this);

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
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NutricionistaAdapter(this, listaFiltrada);
        recyclerView.setAdapter(adapter);

        // Spinner Cidade
        Spinner spinnerCidade = findViewById(R.id.spinnerCidade);
        ArrayAdapter<String> adapterCidade = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"Cidade", "São Paulo", "Campinas", "Santos"});
        adapterCidade.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCidade.setAdapter(adapterCidade);

        // Spinner Avaliação com estrelas
        Spinner spinnerAvaliacao = findViewById(R.id.spinnerAvaliacao);
        spinnerAvaliacao.setAdapter(new EstrelasSpinnerAdapter(this));

        // Listeners
        AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getId() == R.id.spinnerCidade) {
                    cidadeSelecionada = parent.getItemAtPosition(position).toString();
                } else if (parent.getId() == R.id.spinnerAvaliacao) {
                    avaliacaoSelecionada = position; // 0 = sem filtro, 1-5 = estrelas
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