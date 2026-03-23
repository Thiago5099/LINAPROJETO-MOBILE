package com.example.projeto;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ReceitaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_receita);

        ((TextView)findViewById(R.id.nome)).setText(getIntent().getStringExtra("nome"));
        ((TextView)findViewById(R.id.ingredientes)).setText(getIntent().getStringExtra("ingredientes"));
        ((TextView)findViewById(R.id.preparo)).setText(getIntent().getStringExtra("preparo"));
    }
}