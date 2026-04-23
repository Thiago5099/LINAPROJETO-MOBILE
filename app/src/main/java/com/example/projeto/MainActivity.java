package com.example.projeto;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tela.RefeicaoAdapter;
import com.google.android.material.chip.Chip;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView recycler;
    TextView contador;
    Button btnProximo;

    int diaAtual = 0;

    List<String> dias = Arrays.asList("Seg", "Ter", "Qua", "Qui", "Sex", "Sab", "Dom");

    List<List<Refeicao>> cardapio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recycler = findViewById(R.id.recyclerRefeicoes);
        contador = findViewById(R.id.txtContador);
        btnProximo = findViewById(R.id.btnProximo);

        recycler.setLayoutManager(new LinearLayoutManager(this));

        // 5 refeições
        List<Refeicao> base1 = Arrays.asList(
                new Refeicao("Omelete", "15 min", "280 kcal"),
                new Refeicao("Frango", "20 min", "320 kcal"),
                new Refeicao("Sopa", "25 min", "200 kcal"),
                new Refeicao("Panqueca", "10 min", "250 kcal"),
                new Refeicao("Salada", "15 min", "220 kcal")
        );

        List<Refeicao> base2 = Arrays.asList(
                new Refeicao("Macarrão", "20 min", "300 kcal"),
                new Refeicao("Tilápia", "25 min", "280 kcal"),
                new Refeicao("Wrap", "15 min", "260 kcal"),
                new Refeicao("Omelete Fit", "10 min", "230 kcal"),
                new Refeicao("Batata Doce", "30 min", "350 kcal")
        );

        cardapio = Arrays.asList(base1, base2, base1, base2, base1, base2, base1);

        atualizarTela();

        // CHIPS
        Chip seg = findViewById(R.id.chipSeg);
        Chip ter = findViewById(R.id.chipTer);
        Chip qua = findViewById(R.id.chipQua);
        Chip qui = findViewById(R.id.chipQui);
        Chip sex = findViewById(R.id.chipSex);
        Chip sab = findViewById(R.id.chipSab);
        Chip dom = findViewById(R.id.chipDom);

        seg.setOnClickListener(v -> trocar(0));
        ter.setOnClickListener(v -> trocar(1));
        qua.setOnClickListener(v -> trocar(2));
        qui.setOnClickListener(v -> trocar(3));
        sex.setOnClickListener(v -> trocar(4));
        sab.setOnClickListener(v -> trocar(5));
        dom.setOnClickListener(v -> trocar(6));

        // BOTÃO PRÓXIMO DIA
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
        recycler.setAdapter(new RefeicaoAdapter(
                cardapio.get(diaAtual),
                total -> contador.setText(total + "/4 refeições")
        ));
    }
}