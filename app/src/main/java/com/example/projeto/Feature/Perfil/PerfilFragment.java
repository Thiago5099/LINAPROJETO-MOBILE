package com.example.projeto.Feature.Perfil;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.projeto.Feature.Nutricionistas.RetrofitClient;
import com.example.projeto.R;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PerfilFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_perfil, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.btnAssinatura).setOnClickListener(v ->
                startActivity(new Intent(requireActivity(),
                        com.example.projeto.Feature.Pagamento.Pagamento.class)));

        view.findViewById(R.id.buttonConta).setOnClickListener(v ->
                startActivity(new Intent(requireActivity(),
                        com.example.projeto.Feature.AtulizarPerfil.AtualizarPerfil.class)));

        carregarPerfil(view);
    }

    private void carregarPerfil(View view) {
        SharedPreferences prefs = requireContext()
                .getSharedPreferences("auth", Context.MODE_PRIVATE);
        String token = "Bearer " + prefs.getString("token", "");
        long userId = prefs.getLong("userId", -1);

        if (token.equals("Bearer ")) {
            Toast.makeText(requireContext(),
                    "Faça login novamente", Toast.LENGTH_SHORT).show();
            return;
        }

        if (userId == -1) {
            Toast.makeText(requireContext(),
                    "Sessão expirada. Faça login novamente.", Toast.LENGTH_SHORT).show();
            return;
        }

        PerfilApiService api = RetrofitClient.getInstance().create(PerfilApiService.class);
        api.buscarPorId(token, userId).enqueue(new Callback<UsuarioResponse>() {
            @Override
            public void onResponse(Call<UsuarioResponse> call,
                                   Response<UsuarioResponse> response) {
                if (!isAdded()) return;

                if (response.isSuccessful() && response.body() != null) {
                    UsuarioResponse usuario = response.body();

                    // Salva o ID para uso futuro
                    prefs.edit().putLong("userId", usuario.getId()).apply();

                    // Se for premium, troca para o fragment premium
                    if (usuario.isAssinante()) {
                        FragmentTransaction ft = requireActivity()
                                .getSupportFragmentManager()
                                .beginTransaction();
                        ft.replace(((ViewGroup) requireView().getParent()).getId(),
                                new PerfilPremiumFragment());
                        ft.commit();
                        return;
                    }

                    // Preenche nome e email
                    ((TextView) view.findViewById(R.id.textNome))
                            .setText(usuario.getNome());
                    ((TextView) view.findViewById(R.id.textEmail))
                            .setText(usuario.getEmail());

                    // Preenche idade calculada a partir da data de nascimento (vinda da API)
                    String dataNasc = usuario.getDataNascimento();
                    TextView tvIdade = view.findViewById(R.id.textIdade);
                    if (tvIdade != null && dataNasc != null && !dataNasc.isEmpty()) {
                        try {
                            LocalDate nascimento = LocalDate.parse(dataNasc);
                            long anos = ChronoUnit.YEARS.between(nascimento, LocalDate.now());
                            tvIdade.setText(anos + " Anos");
                        } catch (Exception e) {
                            Log.e("Perfil", "Data de nascimento inválida: " + dataNasc);
                        }
                    }

                    // Preenche restrição (chip)
                    List<String> restricoes = usuario.getRestricoes();
                    if (restricoes != null && !restricoes.isEmpty()) {
                        TextView tvRestricao = view.findViewById(R.id.textRestricao);
                        if (tvRestricao != null) {
                            tvRestricao.setText(traduzirRestricao(restricoes.get(0)));
                        }
                    }

                } else {
                    Log.e("API", "Erro HTTP: " + response.code());
                    Toast.makeText(requireContext(),
                            "Erro ao carregar perfil", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UsuarioResponse> call, Throwable t) {
                if (!isAdded()) return;
                Log.e("API", "Falha: " + t.getMessage());
                Toast.makeText(requireContext(),
                        "Sem conexão com o servidor", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String traduzirRestricao(String restricao) {
        switch (restricao) {
            case "CELIACO": return "Celíaco(a)";
            case "LACTOSE": return "Intolerante à Lactose";
            case "VEGANO": return "Vegano(a)";
            case "VEGETARIANO": return "Vegetariano(a)";
            default: return restricao;
        }
    }
}