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

        if (token.equals("Bearer ")) {
            Toast.makeText(requireContext(),
                    "Faça login novamente", Toast.LENGTH_SHORT).show();
            return;
        }

        PerfilApiService api = RetrofitClient.getInstance().create(PerfilApiService.class);
        api.buscarMeuPerfil(token).enqueue(new Callback<UsuarioResponse>() {
            @Override
            public void onResponse(Call<UsuarioResponse> call,
                                   Response<UsuarioResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UsuarioResponse usuario = response.body();

                    ((TextView) view.findViewById(R.id.textNome))
                            .setText(usuario.getNome());
                    ((TextView) view.findViewById(R.id.textEmail))
                            .setText(usuario.getEmail());

                    // Salva o ID para uso futuro (AtualizarPerfil etc)
                    prefs.edit().putLong("userId", usuario.getId()).apply();

                } else {
                    Log.e("API", "Erro HTTP: " + response.code());
                    Toast.makeText(requireContext(),
                            "Erro ao carregar perfil", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UsuarioResponse> call, Throwable t) {
                Log.e("API", "Falha: " + t.getMessage());
                Toast.makeText(requireContext(),
                        "Sem conexão com o servidor", Toast.LENGTH_SHORT).show();
            }
        });
    }
}