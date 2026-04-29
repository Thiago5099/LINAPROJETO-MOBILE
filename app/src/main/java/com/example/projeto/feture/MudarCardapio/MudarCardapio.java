package com.example.projeto.feture.MudarCardapio;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projeto.R;
// REMOVA essa linha:
import com.example.projeto.feture.Cardapio.RefeicaoAdapter;
import com.example.projeto.feture.Cardapio.Refeicao;
import java.util.ArrayList;
import java.util.List;

public class MudarCardapio extends AppCompatActivity {

    private TextView tvNomeAtual, tvTempoAtual, tvCaloriasAtual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mudar_cardapio);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Toolbar com botão voltar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Mudar Cardápio");
        }

        // Views do card atual (topo)
        tvNomeAtual     = findViewById(R.id.tvNomeAtual);
        tvTempoAtual    = findViewById(R.id.tvTempoAtual);
        tvCaloriasAtual = findViewById(R.id.tvCaloriasAtual);

        // Lista de opções para trocar
        List<Refeicao> opcoes = new ArrayList<>();
        opcoes.add(new Refeicao("Omelete com Vegetais", "☀ Café da Manhã", "⏱ 15 min", "⚡ 280 kcal"));
        opcoes.add(new Refeicao("Panqueca de Aveia",    "☀ Café da Manhã", "⏱ 20 min", "⚡ 320 kcal"));
        opcoes.add(new Refeicao("Iogurte com Frutas",   "☀ Café da Manhã", "⏱ 5 min",  "⚡ 180 kcal"));

        // RecyclerView com as opções
        RecyclerView rvOpcoes = findViewById(R.id.rvOpcoes);
        rvOpcoes.setLayoutManager(new LinearLayoutManager(this));

        RefeicaoAdapter adapter = new RefeicaoAdapter(opcoes, refeicao -> {
            // Ao clicar em "Ver Receita" de uma opção:
            // 1. Atualiza o card do topo
            tvNomeAtual.setText(refeicao.getNome());
            tvTempoAtual.setText(refeicao.getTempo());
            tvCaloriasAtual.setText(refeicao.getCalorias());

            // 2. Navega para a tela de receita (ItemRefeicao)
            Intent intent = new Intent(this, ItemRefeicao.class);
            intent.putExtra("nome",     refeicao.getNome());
            intent.putExtra("tipo",     refeicao.getTipo());
            intent.putExtra("tempo",    refeicao.getTempo());
            intent.putExtra("calorias", refeicao.getCalorias());
            startActivity(intent);
        });

        rvOpcoes.setAdapter(adapter);

        // Botão "Ver Receita" do card atual do topo
        Button btnVerReceitaAtual = findViewById(R.id.btnVerReceitaAtual);
        btnVerReceitaAtual.setOnClickListener(v -> {
            Intent intent = new Intent(this, ItemRefeicao.class);
            intent.putExtra("nome", tvNomeAtual.getText().toString());
            startActivity(intent);
        });

        // Botão "Continuar com a mesma refeição"
        Button btnContinuar = findViewById(R.id.btnContinuar);
        btnContinuar.setOnClickListener(v -> {
            Intent resultado = new Intent();
            resultado.putExtra("refeicao_nome", tvNomeAtual.getText().toString());
            setResult(RESULT_OK, resultado);
            finish();
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        getOnBackPressedDispatcher().onBackPressed();
        return true;
    }
}