package com.example.projeto;

import android.content.res.ColorStateList;
import android.graphics.Color;
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
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginCadastro extends AppCompatActivity {

    private LinearLayout layoutLogin, layoutCadastro;
    private Button btnEntrarTab, btnRegistrarTab;
    private Button btnEntrar, btnRegistrar;
    private EditText editTextLoginEmail, editTextLoginSenha;
    private EditText editTextNome, editTextCadastroEmail, editTextCadastroSenha;
    private Spinner spinnerGenero, spinnerRestricoes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_cadastro);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.layoutLogin), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

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

        // Popula os spinners com arrays do strings.xml
        ArrayAdapter<CharSequence> adapterGenero = ArrayAdapter.createFromResource(
                this,
                R.array.opcoes_genero,
                android.R.layout.simple_spinner_item
        );
        adapterGenero.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGenero.setAdapter(adapterGenero);

        ArrayAdapter<CharSequence> adapterRestricoes = ArrayAdapter.createFromResource(
                this,
                R.array.opcoes_restricoes,
                android.R.layout.simple_spinner_item
        );
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

        // Botões de ação
        btnEntrar.setOnClickListener(v -> {
            String email = editTextLoginEmail.getText().toString();
            String senha = editTextLoginSenha.getText().toString();
            Toast.makeText(getApplicationContext(), "Login com: " + email, Toast.LENGTH_SHORT).show();
        });

        btnRegistrar.setOnClickListener(v -> {
            String nome = editTextNome.getText().toString();
            String email = editTextCadastroEmail.getText().toString();
            String senha = editTextCadastroSenha.getText().toString();
            String genero = spinnerGenero.getSelectedItem().toString();
            String restricao = spinnerRestricoes.getSelectedItem().toString();

            Toast.makeText(getApplicationContext(),
                    "Registrado: " + nome + " (" + email + ") - Gênero: " + genero + " - Restrição: " + restricao,
                    Toast.LENGTH_LONG).show();
        });
        LinearLayout layoutEsqueciSenha = findViewById(R.id.layoutEsqueciSenha);
        TextView textViewEsqueciSenha = findViewById(R.id.textViewEsqueciSenha); // aquele link abaixo do botão Entrar
        Button buttonRecuperarSenha = findViewById(R.id.buttonRecuperarSenha);

        // Quando clicar em "Esqueci minha senha"
        textViewEsqueciSenha.setOnClickListener(v -> {
            layoutLogin.setVisibility(View.GONE);
            layoutCadastro.setVisibility(View.GONE);
            layoutEsqueciSenha.setVisibility(View.VISIBLE);
        });

        // Botão de recuperação
        buttonRecuperarSenha.setOnClickListener(v -> {
            String emailRecuperar = ((EditText) findViewById(R.id.editTextRecuperarEmail)).getText().toString();
            Toast.makeText(getApplicationContext(), "Link enviado para: " + emailRecuperar, Toast.LENGTH_SHORT).show();
        });
    }
}
