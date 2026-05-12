package com.example.projeto.Feature.Cardapio;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.projeto.R;

public class ReceitaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_cardapio_receita);

        // Botão voltar — fecha a Activity e retorna ao cardápio
        ImageButton btnVoltar = findViewById(R.id.buttonVoltarReceita);
        btnVoltar.setOnClickListener(v -> finish());

        // Preenche os campos com os dados recebidos
        ((TextView) findViewById(R.id.textReceitaMomento))
                .setText(getIntent().getStringExtra(ReceitaIntentKeys.MOMENTO));

        ((TextView) findViewById(R.id.textTituloReceita))
                .setText(getIntent().getStringExtra(ReceitaIntentKeys.TITULO));

        ((TextView) findViewById(R.id.textTagTempo))
                .setText(getIntent().getStringExtra(ReceitaIntentKeys.TEMPO));

        ((TextView) findViewById(R.id.textTagKcal))
                .setText(getIntent().getStringExtra(ReceitaIntentKeys.KCAL));

        ((TextView) findViewById(R.id.textTagSemGluten))
                .setText(getIntent().getStringExtra(ReceitaIntentKeys.SEM_GLUTEN));

        ((TextView) findViewById(R.id.textTagSemLactose))
                .setText(getIntent().getStringExtra(ReceitaIntentKeys.SEM_LACTOSE));

        ((TextView) findViewById(R.id.textIngredientes))
                .setText(getIntent().getStringExtra(ReceitaIntentKeys.INGREDIENTES));

        ((TextView) findViewById(R.id.textModoDePreparo))
                .setText(getIntent().getStringExtra(ReceitaIntentKeys.PREPARO));

        ((TextView) findViewById(R.id.textInformacoesNutricionais))
                .setText(getIntent().getStringExtra(ReceitaIntentKeys.NUTRICIONAL));
    }
}