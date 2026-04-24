package com.example.projeto;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BancoHelper banco = new BancoHelper(this);

        // Insere dados de exemplo apenas se o banco estiver vazio
        if (banco.listarTodos().isEmpty()) {
            banco.inserir
                    ("Ana Lima", "Esportiva", "São Paulo", "(11) 91234-5678",
                            "marina@gmail.com", "150 + pacientes antendidos", 5.0f);

            banco.inserir
                    ("Carlos Souza", "Clínica", "Campinas", "(19) 98765-4321",
                            "carlos@gmail.com", "50 + pacientes antendidos", 4.0f);

            banco.inserir
                    ("Maria Silva", "Infantil", "Santos", "(13) 99876-5432",
                            "maria@gmail.com", "200 + pacientes antendidos",3.0f);
        }


        ArrayList<Nutricionista> lista = banco.listarTodos();
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new NutricionistaAdapter(lista));
    }
}