package com.example.projeto.Feature.AlterarCardapio;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.projeto.R;

import java.util.*;

public class SelecionarPratoActivityAlterarRefeicao extends AppCompatActivity {

    ListView listaView;

    int posicao;
    String tipo;

    List<PratoAlterarRefeicao> listaPratos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_cardapio_selecionar_prato);

        listaView = findViewById(R.id.lista);

        // recebe dados
        posicao = getIntent().getIntExtra("posicao", -1);
        tipo = getIntent().getStringExtra("tipo");

        // define pratos por tipo

        if (tipo.equals("Café da manhã")) {

            listaPratos.add(new PratoAlterarRefeicao("Omelete","Ovos e queijo","Frite",250,10));
            listaPratos.add(new PratoAlterarRefeicao("Torrada","Pão","Toste",150,5));

        } else if (tipo.equals("Almoço")) {

            listaPratos.add(new PratoAlterarRefeicao("Arroz e Feijão","Arroz e feijão","Cozinhe",400,25));
            listaPratos.add(new PratoAlterarRefeicao("Frango","Frango grelhado","Grelhe",350,20));

        } else if (tipo.equals("Lanche")) {

            listaPratos.add(new PratoAlterarRefeicao("Sanduíche","Pão e recheio","Monte",300,5));
            listaPratos.add(new PratoAlterarRefeicao("Salada","Verduras","Misture",150,5));

        } else if (tipo.equals("Jantar")) {

            listaPratos.add(new PratoAlterarRefeicao("Macarrão","Massa","Cozinhe",500,20));
            listaPratos.add(new PratoAlterarRefeicao("Sopa","Legumes","Cozinhe",200,15));
        }

        // mostra nomes

        List<String> nomes = new ArrayList<>();
        for (PratoAlterarRefeicao p : listaPratos) {
            nomes.add(p.nome);
        }

        listaView.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                nomes
        ));

        listaView.setOnItemClickListener((parent, view, position, id) -> {

            PratoAlterarRefeicao selecionado = listaPratos.get(position);

            Intent it = new Intent();

            it.putExtra("nome", selecionado.nome);
            it.putExtra("tipo", tipo);
            it.putExtra("posicao", posicao);

            setResult(RESULT_OK, it);
            finish();
        });
    }
}