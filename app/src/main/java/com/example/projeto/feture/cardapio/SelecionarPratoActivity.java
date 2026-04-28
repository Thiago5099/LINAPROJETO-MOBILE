package com.example.projeto.feture.cardapio;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.projeto.R;

import java.util.*;

public class SelecionarPratoActivity extends AppCompatActivity {

    ListView listaView;

    int posicao;
    String tipo;

    List<Prato> listaPratos = new ArrayList<>();

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

            listaPratos.add(new Prato("Omelete","Ovos e queijo","Frite",250,10));
            listaPratos.add(new Prato("Torrada","Pão","Toste",150,5));

        } else if (tipo.equals("Almoço")) {

            listaPratos.add(new Prato("Arroz e Feijão","Arroz e feijão","Cozinhe",400,25));
            listaPratos.add(new Prato("Frango","Frango grelhado","Grelhe",350,20));

        } else if (tipo.equals("Lanche")) {

            listaPratos.add(new Prato("Sanduíche","Pão e recheio","Monte",300,5));
            listaPratos.add(new Prato("Salada","Verduras","Misture",150,5));

        } else if (tipo.equals("Jantar")) {

            listaPratos.add(new Prato("Macarrão","Massa","Cozinhe",500,20));
            listaPratos.add(new Prato("Sopa","Legumes","Cozinhe",200,15));
        }

        // mostra nomes

        List<String> nomes = new ArrayList<>();
        for (Prato p : listaPratos) {
            nomes.add(p.nome);
        }

        listaView.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                nomes
        ));

        listaView.setOnItemClickListener((parent, view, position, id) -> {

            Prato selecionado = listaPratos.get(position);

            Intent it = new Intent();

            it.putExtra("nome", selecionado.nome);
            it.putExtra("tipo", tipo);
            it.putExtra("posicao", posicao);

            setResult(RESULT_OK, it);
            finish();
        });
    }
}