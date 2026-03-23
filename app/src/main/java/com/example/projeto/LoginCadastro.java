package com.example.projeto;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        layoutLogin = findViewById(R.id.layoutLogin);
        layoutCadastro = findViewById(R.id.layoutCadastro);
        btnLoginTab = findViewById(R.id.buttonLoginTab);
        btnCadastroTab = findViewById(R.id.buttonCadastroTab);

        btnLoginTab.setOnClickListener(v -> {
            layoutLogin.setVisibility(View.VISIBLE);
            layoutCadastro.setVisibility(View.GONE);
        });

        btnCadastroTab.setOnClickListener(v -> {
            layoutCadastro.setVisibility(View.VISIBLE);
            layoutLogin.setVisibility(View.GONE);
        });
///Text View
        ///Log.d("CicloDeVida", "onCreate() chamado");
        ///TextView textViewMensagem = findViewById(R.id.textViewMensagem);
        ///textViewMensagem.setText("Novo texto exibido!");
///Text Edit
        ///EditText editText1 = findViewById(R.id.editTextLoginEmail);
        ///editText.setHint("Digite o seu e-mail!");
        ///EditText editText2 = findViewById(R.id.editTextLoginSenha);
        ///editText.setHint("Digite o sua senha!");
        ///EditText editText3 = findViewById(R.id.editTextNome);
        ///editText.setHint("Digite o seu nome!");
        ///EditText editText4 = findViewById(R.id.editTextCadastroEmail);
        ///editText.setHint("Digite o seu e-mail!");
        ///EditText editText5 = findViewById(R.id.editTextCadastroSenha);
        ///editText.setHint("Digite o sua senha!");
///Botão dinâminco
        ///Button buttonEntrar = findViewById(R.id.buttonLogar);
        ///buttonEntrar.setOnClickListener(v -> {
            ///Toast.makeText(getApplicationContext(), "Você entrou!",
                    ///Toast.LENGTH_SHORT).show();
        ///});
        ///Button buttonResgistrar = findViewById(R.id.buttonRegistrar);
        ///buttonRegistrar.setOnClickListener(v -> {
            ///Toast.makeText(getApplicationContext(), "Você se registrou!",
                    ///Toast.LENGTH_SHORT).show();
        ///});
///ImageView
/// ImageView imageViewLogo = findViewById(R.id.imageViewLogo);
/// imageViewLogo.setImageResource(R.drawable.nova_imagem);

    }
///Todos os modos
    @Override
    protected void onStart(){
        super.onStart();
        Log.d("CicloDeVida", "onStart() chamado");
    }
    @Override
    protected void onRestart(){
        super.onRestart();
        Log.d("CicloDeVida", "onRestart() chamado");
    }
    @Override
    protected void onResume(){
        super.onResume();
        Log.d("CicloDeVida", "onResume() chamado");
    }
    @Override
    protected void onPause(){
        super.onPause();
        Log.d("CicloDeVida", "onPause() chamado");
    }
    @Override
    protected void onStop(){
        super.onStop();
        Log.d("CicloDeVida", "onStop() chamado");
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        Log.d("CicloDeVida", "onDestroy() chamado");
    }
///Botão
    ///public void logar(View view)
    ///{
        ///Intent intent = new Intent(MainActivity.this, NovaActivity.class);
        ///startActivity(intent); ///Esse método abre uma nova activity
    ///}
///Botão
    ///public void registrar(View view)
    ///{
        ///Intent intent = new Intent(MainActivity.this, NovaActivity.class);
        ///startActivity(intent); ///Esse método abre uma nova activity
    ///}
}