package com.example.projeto.feture.Cardapio;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.projeto.R;

public class ReceitaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_cardapio_receita);

        ((TextView)findViewById(R.id.textReceitaMomento)).setText(getIntent().getStringExtra("Refeição do momento"));
        ((TextView)findViewById(R.id.textTituloReceita)).setText(getIntent().getStringExtra("Título da receita"));
        ((TextView)findViewById(R.id.textTagTempo)).setText(getIntent().getStringExtra("Tag(Tempo de preparo)"));
        ((TextView)findViewById(R.id.textTagKcal)).setText(getIntent().getStringExtra("Tag(kcal)"));
        ((TextView)findViewById(R.id.textTagSemGluten)).setText(getIntent().getStringExtra("Sem Glúten"));
        ((TextView)findViewById(R.id.textTagSemLactose)).setText(getIntent().getStringExtra("Sem Lactose"));
        ((TextView)findViewById(R.id.textIngredientes)).setText(getIntent().getStringExtra("Ingredientes"));
        ((TextView)findViewById(R.id.textModoDePreparo)).setText(getIntent().getStringExtra("Modo de preparo"));
        ((TextView)findViewById(R.id.textInformacoesNutricionais)).setText(getIntent().getStringExtra("Informações nutricionais"));
    }
}