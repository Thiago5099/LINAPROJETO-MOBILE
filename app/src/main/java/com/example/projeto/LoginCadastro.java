package com.example.projeto;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginCadastro extends AppCompatActivity {

    private LinearLayout layoutLogin, layoutCadastro;
    private Button btnLoginTab, btnCadastroTab;

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
        LinearLayout layoutLogin = findViewById(R.id.layoutLogin);
        LinearLayout layoutCadastro = findViewById(R.id.layoutCadastro);
        Button btnEntrarTab = findViewById(R.id.buttonEntrarTab);
        Button btnRegistrarTab = findViewById(R.id.buttonRegistrarTab);

        Button btnEntrar = findViewById(R.id.buttonEntrar);
        Button btnRegistrar = findViewById(R.id.buttonRegistrar);

        EditText editTextLoginEmail = findViewById(R.id.editTextLoginEmail);
        EditText editTextLoginSenha = findViewById(R.id.editTextLoginSenha);
        EditText editTextNome = findViewById(R.id.editTextNome);
        EditText editTextCadastroEmail = findViewById(R.id.editTextCadastroEmail);
        EditText editTextCadastroSenha = findViewById(R.id.editTextCadastroSenha);
        CheckBox checkBoxDiabetico = findViewById(R.id.checkBoxDiabetico);

        // Alternar abas
        btnEntrarTab.setOnClickListener(v -> {
            layoutLogin.setVisibility(View.VISIBLE);
            layoutCadastro.setVisibility(View.GONE);
            btnEntrarTab.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
            btnRegistrarTab.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#CBCBCB")));
        });

        btnRegistrarTab.setOnClickListener(v -> {
            layoutCadastro.setVisibility(View.VISIBLE);
            layoutLogin.setVisibility(View.GONE);
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
            boolean diabetico = checkBoxDiabetico.isChecked();
            Toast.makeText(getApplicationContext(), "Registrado: " + nome + " (" + email + ")", Toast.LENGTH_SHORT).show();
        });

    }
}