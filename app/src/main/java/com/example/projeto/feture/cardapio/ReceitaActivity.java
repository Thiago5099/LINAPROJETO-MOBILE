package com.example.projeto.feture.cardapio;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.projeto.R;

public class ReceitaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_cardapio_receita);

        ((TextView)findViewById(R.id.nome)).setText(getIntent().getStringExtra("nome"));
        ((TextView)findViewById(R.id.ingredientes)).setText(getIntent().getStringExtra("ingredientes"));
        ((TextView)findViewById(R.id.preparo)).setText(getIntent().getStringExtra("preparo"));
    }
}