<<<<<<<< HEAD:app/src/main/java/com/example/projeto/Feature/Perfil/PerfilPremium.java
package com.example.projeto.Feature.Perfil;
========
package com.example.projeto.Feature.AtulizarPerfil;
>>>>>>>> main:app/src/main/java/com/example/projeto/Feature/AtulizarPerfil/AtualizarPerfil.java

import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.projeto.R;

public class AtualizarPerfil extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_atualizar_perfil);

        Button btnCancelarAssinatura = findViewById(R.id.btnCancelarAssinatura);
        btnCancelarAssinatura.setOnClickListener(v -> {
            getSharedPreferences("prefs", MODE_PRIVATE)
                    .edit()
                    .putBoolean("isPremium", false)
                    .apply();
            finish();
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}