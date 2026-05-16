
package com.example.projeto.Feature.AtulizarPerfil;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.projeto.Feature.Nutricionistas.RetrofitClient;
import com.example.projeto.Feature.Perfil.PerfilApiService;
import com.example.projeto.Feature.Perfil.UsuarioResponse;
import com.example.projeto.Feature.Perfil.UsuarioUpdateRequest;
import com.example.projeto.R;

import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AtualizarPerfil extends AppCompatActivity {

    private EditText editNome;
    private EditText editEmail;
    private Spinner spinnerRestricoes;
    private Button btnSalvar;
    /** E-mail vindo da API; se o usuário salvar outro, o token JWT deixa de bater com o back. */
    private String emailCarregado = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atualizar_perfil);

        editNome = findViewById(R.id.editTextNome);
        editEmail = findViewById(R.id.editTextCadastroEmail);
        spinnerRestricoes = findViewById(R.id.spinnerRestricoes);
        btnSalvar = findViewById(R.id.buttonRegistrar);

        findViewById(R.id.buttonFotoPerfil).setOnClickListener(v ->
                Toast.makeText(this, "Em breve", Toast.LENGTH_SHORT).show());

        findViewById(R.id.btnCancelarAssinatura).setOnClickListener(v -> {
            getSharedPreferences("prefs", MODE_PRIVATE)
                    .edit()
                    .putBoolean("isPremium", false)
                    .apply();
            finish();
        });

        btnSalvar.setOnClickListener(v -> salvar());

        carregarPerfil();

        android.view.View root = findViewById(R.id.main);
        if (root != null) {
            ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }
    }

    private SharedPreferences authPrefs() {
        return getSharedPreferences("auth", Context.MODE_PRIVATE);
    }

    private void carregarPerfil() {
        SharedPreferences prefs = authPrefs();
        String raw = prefs.getString("token", "");
        String token = "Bearer " + raw;
        if (raw.isEmpty()) {
            Toast.makeText(this, "Faça login novamente", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        long userId = prefs.getLong("userId", 0L);
        if (userId == 0L) {
            Toast.makeText(this, "Faça login novamente", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        Toast.makeText(this, R.string.atualizar_perfil_carregando, Toast.LENGTH_SHORT).show();

        PerfilApiService api = RetrofitClient.getInstance().create(PerfilApiService.class);
        api.buscarPerfil(token, userId).enqueue(new Callback<UsuarioResponse>() {
            @Override
            public void onResponse(Call<UsuarioResponse> call, Response<UsuarioResponse> response) {
                if (isFinishing()) return;
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(AtualizarPerfil.this,
                            "Não foi possível carregar o perfil (" + response.code() + ")",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                UsuarioResponse u = response.body();
                editNome.setText(u.getNome());
                editEmail.setText(u.getEmail());
                emailCarregado = u.getEmail() != null ? u.getEmail() : "";
                aplicarRestricaoNoSpinner(u.getRestricoes());
                if (u.getId() != null) {
                    prefs.edit().putLong("userId", u.getId()).apply();
                }
            }

            @Override
            public void onFailure(Call<UsuarioResponse> call, Throwable t) {
                if (isFinishing()) return;
                Toast.makeText(AtualizarPerfil.this,
                        "Falha ao carregar: " + (t.getMessage() != null ? t.getMessage() : "rede"),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void aplicarRestricaoNoSpinner(List<String> restricoes) {
        String[] valores = getResources().getStringArray(R.array.opcoes_restricoes);
        int idx = 0;
        if (restricoes != null && !restricoes.isEmpty()) {
            String first = restricoes.get(0);
            for (int i = 1; i < valores.length; i++) {
                if (valores[i].equals(first)) {
                    idx = i;
                    break;
                }
            }
        }
        spinnerRestricoes.setSelection(idx);
    }

    private void salvar() {
        String nome = editNome.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        if (nome.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Preencha nome e email", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences prefs = authPrefs();
        String raw = prefs.getString("token", "");
        String token = "Bearer " + raw;
        long userId = prefs.getLong("userId", 0L);
        if (raw.isEmpty() || userId == 0L) {
            Toast.makeText(this, "Faça login novamente", Toast.LENGTH_LONG).show();
            return;
        }

        int pos = spinnerRestricoes.getSelectedItemPosition();
        String[] valores = getResources().getStringArray(R.array.opcoes_restricoes);
        // Índice 0 = NENHUMA (não existe no enum do back) → lista vazia limpa restrições
        List<String> restLista = (pos <= 0 || pos >= valores.length)
                ? Collections.emptyList()
                : Collections.singletonList(valores[pos]);

        btnSalvar.setEnabled(false);
        UsuarioUpdateRequest body = new UsuarioUpdateRequest(nome, email, restLista);

        PerfilApiService api = RetrofitClient.getInstance().create(PerfilApiService.class);
        api.atualizarPerfil(token, userId, body).enqueue(new Callback<UsuarioResponse>() {
            @Override
            public void onResponse(Call<UsuarioResponse> call, Response<UsuarioResponse> response) {
                if (isFinishing()) return;
                btnSalvar.setEnabled(true);
                if (response.isSuccessful() && response.body() != null) {
                    SharedPreferences.Editor ed = prefs.edit();
                    ed.putString("userEmail", email);
                    if (!email.equalsIgnoreCase(emailCarregado)) {
                        ed.remove("token").remove("userId");
                        ed.apply();
                        Toast.makeText(AtualizarPerfil.this,
                                "E-mail alterado. Faça login novamente.",
                                Toast.LENGTH_LONG).show();
                        Intent login = new Intent(AtualizarPerfil.this,
                                com.example.projeto.Feature.Login.LoginCadastro.class);
                        login.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(login);
                        finish();
                    } else {
                        ed.apply();
                        Toast.makeText(AtualizarPerfil.this, R.string.atualizar_perfil_salvo, Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    return;
                }
                String msg;
                switch (response.code()) {
                    case 401: msg = "Sessão expirada"; break;
                    case 403: msg = "Acesso negado"; break;
                    default: msg = "Erro ao salvar (" + response.code() + ")";
                }
                Toast.makeText(AtualizarPerfil.this, msg, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<UsuarioResponse> call, Throwable t) {
                if (isFinishing()) return;
                btnSalvar.setEnabled(true);
                Toast.makeText(AtualizarPerfil.this,
                        "Falha: " + (t.getMessage() != null ? t.getMessage() : "rede"),
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}
