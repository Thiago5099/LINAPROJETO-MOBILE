package com.example.projeto.Feature.Login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.projeto.Feature.Nutricionistas.RetrofitClient;
import com.example.projeto.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginCadastro extends AppCompatActivity {

    private LinearLayout layoutLogin, layoutCadastro;
    private Button btnEntrarTab, btnRegistrarTab;
    private Button btnEntrar, btnRegistrar;
    private EditText editTextLoginEmail, editTextLoginSenha;
    private EditText editTextNome, editTextCadastroEmail, editTextCadastroSenha;
    private Spinner spinnerGenero, spinnerRestricoes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.layoutLogin), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.btnCardapio).setOnClickListener(v ->
                startActivity(new Intent(this, com.example.projeto.Feature.Menu.Menu.class)));

        // Referências
        layoutLogin = findViewById(R.id.layoutLogin);
        layoutCadastro = findViewById(R.id.layoutCadastro);
        btnEntrarTab = findViewById(R.id.buttonEntrarTab);
        btnRegistrarTab = findViewById(R.id.buttonRegistrarTab);
        btnEntrar = findViewById(R.id.buttonEntrar);
        btnRegistrar = findViewById(R.id.buttonRegistrar);
        editTextLoginEmail = findViewById(R.id.editTextLoginEmail);
        editTextLoginSenha = findViewById(R.id.editTextLoginSenha);
        editTextNome = findViewById(R.id.editTextNome);
        editTextCadastroEmail = findViewById(R.id.editTextCadastroEmail);
        editTextCadastroSenha = findViewById(R.id.editTextCadastroSenha);
        spinnerGenero = findViewById(R.id.spinnerGenero);
        spinnerRestricoes = findViewById(R.id.spinnerRestricoes);

        // Popula os spinners
        ArrayAdapter<CharSequence> adapterGenero = ArrayAdapter.createFromResource(
                this, R.array.opcoes_genero, android.R.layout.simple_spinner_item);
        adapterGenero.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGenero.setAdapter(adapterGenero);

        ArrayAdapter<CharSequence> adapterRestricoes = ArrayAdapter.createFromResource(
                this, R.array.opcoes_restricoes, android.R.layout.simple_spinner_item);
        adapterRestricoes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRestricoes.setAdapter(adapterRestricoes);

        // Alternar abas
        btnEntrarTab.setOnClickListener(v -> {
            layoutLogin.setVisibility(LinearLayout.VISIBLE);
            layoutCadastro.setVisibility(LinearLayout.GONE);
            btnEntrarTab.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
            btnRegistrarTab.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#CBCBCB")));
        });

        btnRegistrarTab.setOnClickListener(v -> {
            layoutCadastro.setVisibility(LinearLayout.VISIBLE);
            layoutLogin.setVisibility(LinearLayout.GONE);
            btnRegistrarTab.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
            btnEntrarTab.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#CBCBCB")));
        });

        // Botão ENTRAR
        btnEntrar.setOnClickListener(v -> {
            String email = editTextLoginEmail.getText().toString().trim();
            String senha = editTextLoginSenha.getText().toString().trim();

            if (email.isEmpty() || senha.isEmpty()) {
                Toast.makeText(this, "Preencha email e senha", Toast.LENGTH_SHORT).show();
                return;
            }

            AuthApiService api = RetrofitClient.getInstance().create(AuthApiService.class);
            api.login(new LoginRequest(email, senha)).enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call,
                                       Response<LoginResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String token = response.body().getToken();

                        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
                        prefs.edit().putString("token", token).apply();

                        // Extrai o email do JWT e salva
                        try {
                            String payload = token.split("\\.")[1];
                            byte[] decoded = android.util.Base64.decode(
                                    payload, android.util.Base64.URL_SAFE);
                            String json = new String(decoded);
                            String emailJwt = json.split("\"sub\":\"")[1].split("\"")[0];
                            prefs.edit().putString("userEmail", emailJwt).apply();
                            Log.d("JWT_PAYLOAD", "Email salvo: " + emailJwt);
                        } catch (Exception e) {
                            Log.e("JWT", "Erro: " + e.getMessage());
                        }

                        Toast.makeText(LoginCadastro.this,
                                "Login realizado!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginCadastro.this,
                                com.example.projeto.Feature.Menu.Menu.class));
                        finish();
                    } else {
                        Toast.makeText(LoginCadastro.this,
                                "Email ou senha incorretos", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    Toast.makeText(LoginCadastro.this,
                            "Sem conexão com o servidor", Toast.LENGTH_SHORT).show();
                }
            });
        });

        // Botão REGISTRAR
        btnRegistrar.setOnClickListener(v -> {
            String nome = editTextNome.getText().toString().trim();
            String email = editTextCadastroEmail.getText().toString().trim();
            String senha = editTextCadastroSenha.getText().toString().trim();
            String genero = spinnerGenero.getSelectedItem().toString();
            String restricao = spinnerRestricoes.getSelectedItem().toString();

            if (nome.isEmpty() || email.isEmpty() || senha.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                return;
            }

            AuthApiService api = RetrofitClient.getInstance().create(AuthApiService.class);
            api.cadastrar(new CadastroRequest(nome, email, senha, genero, restricao))
                    .enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(LoginCadastro.this,
                                        "Cadastro realizado! Faça login.",
                                        Toast.LENGTH_SHORT).show();
                                btnEntrarTab.performClick();
                            } else {
                                Toast.makeText(LoginCadastro.this,
                                        "Erro no cadastro", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(LoginCadastro.this,
                                    "Sem conexão com o servidor", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        // Esqueci minha senha
        CardView cardForm = findViewById(R.id.cardForm);
        CardView cardEsqueciSenha = findViewById(R.id.cardEsqueciSenha);
        TextView textViewEsqueciSenha = findViewById(R.id.textViewEsqueciSenha);
        Button buttonVoltarLogin = findViewById(R.id.buttonVoltarLogin);

        textViewEsqueciSenha.setOnClickListener(v -> {
            cardForm.setVisibility(View.GONE);
            cardEsqueciSenha.setVisibility(View.VISIBLE);
        });

        buttonVoltarLogin.setOnClickListener(v -> {
            cardEsqueciSenha.setVisibility(View.GONE);
            cardForm.setVisibility(View.VISIBLE);
        });

        // Foto de perfil
        Button buttonFotoPerfil = findViewById(R.id.buttonFotoPerfil);
        buttonFotoPerfil.setOnClickListener(v ->
                Toast.makeText(this,
                        "Função de adicionar foto em breve!", Toast.LENGTH_SHORT).show());
    }
}