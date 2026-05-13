package com.example.projeto.Feature.Login;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.content.Intent;
import android.os.Bundle;
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

        // CORREÇÃO: WindowInsets deve ser aplicado na view raiz, não no layoutLogin
        View rootView = findViewById(android.R.id.content);
        ViewCompat.setOnApplyWindowInsetsListener(rootView, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Referências
        layoutLogin    = findViewById(R.id.layoutLogin);
        layoutCadastro = findViewById(R.id.layoutCadastro);
        btnEntrarTab   = findViewById(R.id.buttonEntrarTab);
        btnRegistrarTab = findViewById(R.id.buttonRegistrarTab);
        btnEntrar      = findViewById(R.id.buttonEntrar);
        btnRegistrar   = findViewById(R.id.buttonRegistrar);

        editTextLoginEmail    = findViewById(R.id.editTextLoginEmail);
        editTextLoginSenha    = findViewById(R.id.editTextLoginSenha);
        editTextNome          = findViewById(R.id.editTextNome);
        editTextCadastroEmail = findViewById(R.id.editTextCadastroEmail);
        editTextCadastroSenha = findViewById(R.id.editTextCadastroSenha);

        spinnerGenero     = findViewById(R.id.spinnerGenero);
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

        // CORREÇÃO: Alternar abas Entrar / Registrar
        btnEntrarTab.setOnClickListener(v -> {
            layoutLogin.setVisibility(View.VISIBLE);
            layoutCadastro.setVisibility(View.GONE);
            btnEntrarTab.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(android.graphics.Color.WHITE));
            btnRegistrarTab.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#CBCBCB")));
        });

        btnRegistrarTab.setOnClickListener(v -> {
            layoutLogin.setVisibility(View.GONE);
            layoutCadastro.setVisibility(View.VISIBLE);
            btnRegistrarTab.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(android.graphics.Color.WHITE));
            btnEntrarTab.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#CBCBCB")));
        });

        // Botão Entrar — chama a API
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
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String token = response.body().getToken();

                        getSharedPreferences("auth", MODE_PRIVATE)
                                .edit()
                                .putString("token", token)
                                .apply();

                        Toast.makeText(LoginCadastro.this, "Login realizado!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginCadastro.this,
                                com.example.projeto.Feature.Menu.Menu.class));
                        finish();
                    } else {
                        Toast.makeText(LoginCadastro.this,
                                "Email ou senha incorretos (código " + response.code() + ")",
                                Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    Toast.makeText(LoginCadastro.this,
                            "Sem conexão com o servidor: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });

        // Botão Registrar — chama a API
        btnRegistrar.setOnClickListener(v -> {
            String nome      = editTextNome.getText().toString().trim();
            String email     = editTextCadastroEmail.getText().toString().trim();
            String senha     = editTextCadastroSenha.getText().toString().trim();
            String genero    = spinnerGenero.getSelectedItem().toString();
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
                                        "Cadastro realizado! Faça login.", Toast.LENGTH_SHORT).show();
                                btnEntrarTab.performClick();
                            } else {
                                Toast.makeText(LoginCadastro.this,
                                        "Erro no cadastro (código " + response.code() + ")",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(LoginCadastro.this,
                                    "Sem conexão com o servidor: " + t.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        });

        // Esqueci minha senha
        CardView cardForm         = findViewById(R.id.cardForm);
        CardView cardEsqueciSenha = findViewById(R.id.cardEsqueciSenha);
        TextView textViewEsqueciSenha = findViewById(R.id.textViewEsqueciSenha);
        Button buttonVoltarLogin  = findViewById(R.id.buttonVoltarLogin);

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
                Toast.makeText(getApplicationContext(),
                        "Função de adicionar foto em breve!", Toast.LENGTH_SHORT).show());

        // Botão menu
        findViewById(R.id.btnCardapio).setOnClickListener(v ->
                startActivity(new Intent(this, com.example.projeto.Feature.Menu.Menu.class)));
    }
}
