package com.example.projeto.feture.MudarCardapio;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.projeto.R;

public class ItemRefeicao extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_item_refeicao);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Recebe os dados enviados pela MudarCardapio
        String nome     = getIntent().getStringExtra("nome");
        String tipo     = getIntent().getStringExtra("tipo");
        String tempo    = getIntent().getStringExtra("tempo");
        String calorias = getIntent().getStringExtra("calorias");

        // Toolbar com botão voltar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(nome != null ? nome : "Receita");
        }

        // Preenche as views com os dados recebidos
        TextView tvTipo     = findViewById(R.id.tvTipo);
        TextView tvNome     = findViewById(R.id.tvNome);
        TextView tvTempo    = findViewById(R.id.tvTempo);
        TextView tvCalorias = findViewById(R.id.tvCalorias);

        if (tvTipo != null)     tvTipo.setText(tipo);
        if (tvNome != null)     tvNome.setText(nome);
        if (tvTempo != null)    tvTempo.setText(tempo);
        if (tvCalorias != null) tvCalorias.setText(calorias);
    }

    @Override
    public boolean onSupportNavigateUp() {
        getOnBackPressedDispatcher().onBackPressed();
        return true;
    }
}