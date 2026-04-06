package com.example.projeto;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.chip.Chip;
import com.example.tela.RefeicaoAdapter;
import com.example.projeto.R;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView recycler;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recycler = findViewById(R.id.recyclerRefeicoes);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        List<String> listaSegunda = Arrays.asList(
                "Omelete com Vegetais",
                "Frango com Quinoa",
                "Sopa de Legumes",
                "Panqueca de Banana",
                "Salada Proteica",
                "Arroz Integral com Ovo"
        );

        List<String> listaTerca = Arrays.asList(
                "Iogurte com Granola",
                "Macarrão Integral",
                "Tilápia com Legumes",
                "Wrap de Frango",
                "Omelete Fit",
                "Batata Doce com Carne"
        );

        List<String> listaQuarta = Arrays.asList(
                "Panqueca Fit",
                "Arroz com Frango",
                "Salada Completa",
                "Cuscuz com Ovo",
                "Frango Grelhado",
                "Sopa Detox"
        );

        List<String> listaQuinta = Arrays.asList(
                "Cuscuz Nordestino",
                "Carne com Legumes",
                "Atum com Salada",
                "Omelete de Queijo",
                "Arroz + Feijão",
                "Macarrão Fit"
        );

        List<String> listaSexta = Arrays.asList(
                "Hambúrguer Caseiro",
                "Pizza Caseira",
                "Batata Frita",
                "Lasanha",
                "Escondidinho",
                "Frango Crocante"
        );

        List<String> listaSabado = Arrays.asList(
                "Churrasco",
                "Macarrão",
                "Sobremesa",
                "Feijoada",
                "Arroz Carreteiro",
                "Pudim"
        );

        List<String> listaDomingo = Arrays.asList(
                "Café Especial",
                "Lasanha",
                "Sobremesa",
                "Frango Assado",
                "Macarronada",
                "Bolo Caseiro"
        );

        recycler.setAdapter(new RefeicaoAdapter(listaSegunda));

        Chip chipSeg = findViewById(R.id.chipSeg);
        Chip chipTer = findViewById(R.id.chipTer);
        Chip chipQua = findViewById(R.id.chipQua);
        Chip chipQui = findViewById(R.id.chipQui);
        Chip chipSex = findViewById(R.id.chipSex);
        Chip chipSab = findViewById(R.id.chipSab);
        Chip chipDom = findViewById(R.id.chipDom);

        chipSeg.setChecked(true);

        chipSeg.setOnClickListener(v -> recycler.setAdapter(new RefeicaoAdapter(listaSegunda)));
        chipTer.setOnClickListener(v -> recycler.setAdapter(new RefeicaoAdapter(listaTerca)));
        chipQua.setOnClickListener(v -> recycler.setAdapter(new RefeicaoAdapter(listaQuarta)));
        chipQui.setOnClickListener(v -> recycler.setAdapter(new RefeicaoAdapter(listaQuinta)));
        chipSex.setOnClickListener(v -> recycler.setAdapter(new RefeicaoAdapter(listaSexta)));
        chipSab.setOnClickListener(v -> recycler.setAdapter(new RefeicaoAdapter(listaSabado)));
        chipDom.setOnClickListener(v -> recycler.setAdapter(new RefeicaoAdapter(listaDomingo)));
    }
}