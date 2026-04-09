package com.example.projeto;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    BancoHelper banco;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 🔗 Conecta com o RecyclerView do XML
        recyclerView = findViewById(R.id.recyclerView);

        // 💾 Inicializa o banco
        banco = new BancoHelper(this);

        // 📋 Busca os dados do banco
        ArrayList<Nutricionista> lista = banco.listar();

        // 📐 Define o tipo de lista (vertical)
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 🔄 Liga os dados ao RecyclerView
        NutricionistaAdapter adapter = new NutricionistaAdapter(lista);
        recyclerView.setAdapter(adapter);
    }
}