package com.example.projeto;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tela.RefeicaoAdapter;

import java.util.*;

public class MainActivity extends AppCompatActivity {

    RecyclerView recycler;
    TextView contador, txtDia;
    Button btnProximo;

    int diaAtual = 0;

    List<String> dias = Arrays.asList(
            "Segunda", "Terça", "Quarta", "Quinta", "Sexta", "Sábado", "Domingo"
    );

    List<List<Refeicao>> cardapio;
    List<Set<Integer>> selecoesPorDia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recycler = findViewById(R.id.recyclerRefeicoes);
        contador = findViewById(R.id.txtContador);
        txtDia = findViewById(R.id.txtDia);
        btnProximo = findViewById(R.id.btnProximo);

        recycler.setLayoutManager(new LinearLayoutManager(this));

        // 🔥 SEGUNDA
        List<Refeicao> seg = Arrays.asList(
                new Refeicao("Café da manhã", "Pão com ovo", "10 min", "250 kcal"),
                new Refeicao("Café da manhã", "Vitamina de banana", "8 min", "200 kcal"),

                new Refeicao("Almoço", "Frango grelhado", "20 min", "320 kcal"),
                new Refeicao("Almoço", "Arroz + feijão", "25 min", "350 kcal"),

                new Refeicao("Lanche da tarde", "Iogurte", "5 min", "180 kcal"),
                new Refeicao("Lanche da tarde", "Sanduíche natural", "10 min", "220 kcal"),

                new Refeicao("Jantar", "Sopa de legumes", "20 min", "200 kcal"),
                new Refeicao("Jantar", "Omelete leve", "15 min", "180 kcal")
        );

        // 🔥 TERÇA
        List<Refeicao> ter = Arrays.asList(
                new Refeicao("Café da manhã", "Café + pão", "10 min", "220 kcal"),
                new Refeicao("Café da manhã", "Aveia com frutas", "8 min", "210 kcal"),

                new Refeicao("Almoço", "Carne assada", "30 min", "400 kcal"),
                new Refeicao("Almoço", "Macarrão", "20 min", "350 kcal"),

                new Refeicao("Lanche da tarde", "Fruta", "5 min", "120 kcal"),
                new Refeicao("Lanche da tarde", "Barra de cereal", "5 min", "150 kcal"),

                new Refeicao("Jantar", "Sopa", "20 min", "180 kcal"),
                new Refeicao("Jantar", "Salada com frango", "15 min", "200 kcal")
        );

        // 🔁 repetir padrão
        cardapio = Arrays.asList(seg, ter, seg, ter, seg, ter, seg);

        // 🔥 salvar seleção por dia
        selecoesPorDia = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            selecoesPorDia.add(new HashSet<>());
        }

        atualizarTela();

        // CHIPS
        findViewById(R.id.chipSeg).setOnClickListener(v -> trocar(0));
        findViewById(R.id.chipTer).setOnClickListener(v -> trocar(1));
        findViewById(R.id.chipQua).setOnClickListener(v -> trocar(2));
        findViewById(R.id.chipQui).setOnClickListener(v -> trocar(3));
        findViewById(R.id.chipSex).setOnClickListener(v -> trocar(4));
        findViewById(R.id.chipSab).setOnClickListener(v -> trocar(5));
        findViewById(R.id.chipDom).setOnClickListener(v -> trocar(6));

        btnProximo.setOnClickListener(v -> {
            diaAtual++;
            if (diaAtual > 6) diaAtual = 0;
            atualizarTela();
        });
    }

    private void trocar(int index) {
        diaAtual = index;
        atualizarTela();
    }

    private void atualizarTela() {
        txtDia.setText(dias.get(diaAtual));

        recycler.setAdapter(new RefeicaoAdapter(
                cardapio.get(diaAtual),
                selecoesPorDia.get(diaAtual),
                total -> contador.setText(total + "/4 refeições")
        ));
    }
}