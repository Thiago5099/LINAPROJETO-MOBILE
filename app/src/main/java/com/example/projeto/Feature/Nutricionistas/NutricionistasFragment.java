package com.example.projeto.Feature.Nutricionistas;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projeto.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NutricionistasFragment extends Fragment {

    private NutricionistaAdapter adapter;
    private ArrayList<Nutricionista> listaTodos = new ArrayList<>();
    private ArrayList<Nutricionista> listaFiltrada = new ArrayList<>();

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

        // RecyclerView
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

        // Spinner Avaliação
        Spinner spinnerAvaliacao = view.findViewById(R.id.spinnerAvaliacao);
        spinnerAvaliacao.setAdapter(new NutricionistasEstrelasSpinnerAdapter(requireContext()));

        // Listeners dos spinners
        AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
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

    @Override
    public void onResume() {
        super.onResume();
        // JWT gravado no login (SharedPreferences auth/token) — recarrega ao voltar para a aba
        carregarDaApi();
    }

    /**
     * Header Authorization do usuário logado (mesmo token do {@code LoginCadastro}).
     * Evita "Bearer Bearer ..." se o valor já vier prefixado.
     */
    @Nullable
    private String authorizationHeader() {
        Context ctx = getContext();
        if (ctx == null) return null;
        String raw = ctx.getSharedPreferences("auth", Context.MODE_PRIVATE)
                .getString("token", "");
        if (raw == null || raw.trim().isEmpty()) {
            return null;
        }
        raw = raw.trim();
        if (raw.regionMatches(true, 0, "Bearer ", 0, 7)) {
            return raw;
        }
        return "Bearer " + raw;
    }

    private void carregarDaApi() {
        Context ctx = getContext();
        if (ctx == null) return;

        String auth = authorizationHeader();
        if (auth == null) {
            Log.w("API", "Sem token — faça login para ver nutricionistas");
            listaTodos.clear();
            listaFiltrada.clear();
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
            if (isAdded()) {
                Toast.makeText(requireContext(),
                        "Faça login para ver nutricionistas.", Toast.LENGTH_LONG).show();
            }
            return;
        }

        NutricionistaApiService api = RetrofitClient.getInstance()
                .create(NutricionistaApiService.class);

        api.listar(auth).enqueue(new Callback<List<Nutricionista>>() {
            @Override
            public void onResponse(Call<List<Nutricionista>> call,
                                   Response<List<Nutricionista>> response) {
                if (!isAdded()) return;
                if (response.isSuccessful() && response.body() != null) {
                    listaTodos.clear();
                    listaTodos.addAll(response.body());
                    aplicarFiltros();
                } else {
                    Log.e("API", "Erro HTTP: " + response.code());
                    String msg;
                    if (response.code() == 401) {
                        msg = "Sessão expirada. Faça login novamente.";
                    } else if (response.code() == 403) {
                        msg = "Acesso negado.";
                    } else {
                        msg = "Erro ao carregar nutricionistas (" + response.code() + ")";
                    }
                    Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<Nutricionista>> call, Throwable t) {
                if (!isAdded()) return;
                Log.e("API", "Falha na chamada: " + t.getMessage());
                Toast.makeText(requireContext(),
                        "Sem conexão com o servidor", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void aplicarFiltros() {
        ArrayList<Nutricionista> resultado = new ArrayList<>();

        for (Nutricionista n : listaTodos) {
            boolean passaCidade = cidadeSelecionada.equals("Cidade")
                    || n.getCidade().equalsIgnoreCase(cidadeSelecionada);
            boolean passaAvaliacao = avaliacaoSelecionada == 0
                    || Math.round(n.getAvaliacao()) == avaliacaoSelecionada;

            if (passaCidade && passaAvaliacao) {
                resultado.add(n);
            }
        }

        listaFiltrada.clear();
        listaFiltrada.addAll(resultado);
        adapter.notifyDataSetChanged();
    }
}