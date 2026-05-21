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

import com.example.projeto.Feature.Nutricionistas.RetrofitClient;
import com.example.projeto.R;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PerfilPremiumFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_perfil_premium, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.buttonConta).setOnClickListener(v ->
                startActivity(new Intent(requireActivity(),
                        com.example.projeto.Feature.AtulizarPerfil.AtualizarPerfil.class)));
    }

    @Override
    public void onResume() {
        super.onResume();
        View v = getView();
        if (v != null) {
            carregarPerfil(v);
        }
    }

    private void carregarPerfil(View view) {
        SharedPreferences prefs = requireContext()
                .getSharedPreferences("auth", Context.MODE_PRIVATE);
        String token = "Bearer " + prefs.getString("token", "");

        if (token.equals("Bearer ")) {
            Toast.makeText(requireContext(),
                    "Faça login novamente", Toast.LENGTH_SHORT).show();
            return;
        }

        long userId = prefs.getLong("userId", 0L);
        if (userId == 0L) {
            Toast.makeText(requireContext(),
                    "Faça login novamente para carregar o perfil", Toast.LENGTH_LONG).show();
            return;
        }

        PerfilApiService api = RetrofitClient.getInstance().create(PerfilApiService.class);
        api.buscarPerfil(token, userId).enqueue(new Callback<UsuarioResponse>() {
            @Override
            public void onResponse(Call<UsuarioResponse> call,
                                   Response<UsuarioResponse> response) {
                if (!isAdded()) return;
                if (response.isSuccessful() && response.body() != null) {
                    UsuarioResponse usuario = response.body();

                    TextView tvNome = view.findViewById(R.id.textNome);
                    if (tvNome != null) {
                        tvNome.setText(usuario.getNome());
                    }

                    TextView tvEmail = view.findViewById(R.id.textEmail);
                    if (tvEmail != null) {
                        tvEmail.setText(usuario.getEmail());
                    }

                    // Preenche idade calculada a partir da data de nascimento (vinda da API)
                    String dataNasc = usuario.getDataNascimento();
                    Log.d("PerfilPremium", "dataNascimento recebida da API: [" + dataNasc + "]");
                    
                    // Procura o TextView de idade - como não tem ID no XML, vamos procurar pelo texto
                    TextView tvIdade = encontrarTextViewPorTexto(view, "22 Anos");
                    
                    if (tvIdade != null && dataNasc != null && !dataNasc.isEmpty()) {
                        try {
                            // Suporta "YYYY-MM-DD" e "YYYY-MM-DDTHH:mm:ss" (com ou sem hora)
                            String apenasData = dataNasc.length() >= 10
                                    ? dataNasc.substring(0, 10)
                                    : dataNasc;
                            LocalDate nascimento = LocalDate.parse(apenasData);
                            long anos = ChronoUnit.YEARS.between(nascimento, LocalDate.now());
                            tvIdade.setText(anos + " Anos");
                            Log.d("PerfilPremium", "Idade calculada: " + anos);
                        } catch (Exception e) {
                            Log.e("PerfilPremium", "Data de nascimento inválida: " + dataNasc, e);
                            tvIdade.setText("-- Anos");
                        }
                    } else {
                        Log.w("PerfilPremium", "dataNascimento nula ou vazia");
                        if (tvIdade != null) {
                            tvIdade.setText("-- Anos");
                        }
                    }

                    // Procura o TextView de restrição
                    TextView tvRestricao = encontrarTextViewPorTexto(view, "celíaca(a)");
                    
                    if (tvRestricao != null) {
                        List<String> restricoes = usuario.getRestricoes();
                        if (restricoes != null && !restricoes.isEmpty()) {
                            tvRestricao.setText(traduzirRestricao(restricoes.get(0)));
                        } else {
                            tvRestricao.setText("Nenhuma");
                        }
                    }
                    
                    // Salva o ID para uso futuro (AtualizarPerfil etc)
                    prefs.edit().putLong("userId", usuario.getId()).apply();

                } else {
                    Log.e("PERFIL_PREMIUM", "Erro HTTP: " + response.code() + " - " + response.message());
                    String erro;
                    switch (response.code()) {
                        case 401: erro = "Sessão expirada, faça login novamente"; break;
                        case 403: erro = "Acesso negado (403)"; break;
                        case 404: erro = "Usuário não encontrado (404)"; break;
                        default:  erro = "Erro do servidor: " + response.code();
                    }
                    if (isAdded()) {
                        Toast.makeText(requireContext(), erro, Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<UsuarioResponse> call, Throwable t) {
                Log.e("PERFIL_PREMIUM", "Erro de conexão: " + t.getClass().getSimpleName() + " - " + t.getMessage());
                String msg = t.getMessage() != null ? t.getMessage() : "Erro desconhecido";
                if (isAdded()) {
                    Toast.makeText(requireContext(),
                            "Falha: " + msg, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private TextView encontrarTextViewPorTexto(View parent, String texto) {
        if (parent instanceof TextView) {
            TextView tv = (TextView) parent;
            if (tv.getText().toString().contains(texto)) {
                return tv;
            }
        } else if (parent instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) parent;
            for (int i = 0; i < group.getChildCount(); i++) {
                TextView found = encontrarTextViewPorTexto(group.getChildAt(i), texto);
                if (found != null) return found;
            }
        }
        return null;
    }

    private String traduzirRestricao(String restricao) {
        if (restricao == null) return "Nenhuma";
        switch (restricao) {
            case "CELIACO":     return "Sem Glúten";
            case "LACTOSE":     return "Sem Lactose";
            case "VEGETARIANO": return "Vegetariano";
            case "VEGANO":      return "Vegano";
            case "DIABETICO":   return "Sem açúcar / diabético";
            default:            return restricao;
        }
    }
}
