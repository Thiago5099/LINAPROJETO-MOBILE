package com.example.projeto.Feature.CriarCardapio;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projeto.R;
import com.example.projeto.Data.BancoHelper;
import com.example.projeto.Feature.Cardapio.CardapioItemPersistido;
import com.example.projeto.Feature.Cardapio.CardapioLocalStore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CriarCardapioMainActivity extends AppCompatActivity {

    RecyclerView recycler;
    TextView contador, txtDia;
    Button btnProximo;

    int diaAtual = 0;

    List<String> dias = Arrays.asList(
            "Segunda", "Terça", "Quarta", "Quinta", "Sexta", "Sábado", "Domingo"
    );

    /** Só preenchido após GET /refeicoes; os 7 dias compartilham a mesma lista de opções. */
    List<List<CriarCardapioRefeicao>> cardapio;
    List<Set<Integer>> selecoesPorDia;

    private volatile boolean catalogoCarregado = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criar_cardapio_main);

        recycler = findViewById(R.id.recyclerRefeicoes);
        contador = findViewById(R.id.txtContador);
        txtDia = findViewById(R.id.txtDia);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageButton buttonVoltar = findViewById(R.id.buttonVoltarReceita);
        buttonVoltar.setOnClickListener(v -> finish());

        recycler.setLayoutManager(new LinearLayoutManager(this));

        selecoesPorDia = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            selecoesPorDia.add(new HashSet<>());
        }

        contador.setText("Carregando refeições...");
        setNavegacaoHabilitada(false);

        findViewById(R.id.chipSeg).setOnClickListener(v -> trocar(0));
        findViewById(R.id.chipTer).setOnClickListener(v -> trocar(1));
        findViewById(R.id.chipQua).setOnClickListener(v -> trocar(2));
        findViewById(R.id.chipQui).setOnClickListener(v -> trocar(3));
        findViewById(R.id.chipSex).setOnClickListener(v -> trocar(4));
        findViewById(R.id.chipSab).setOnClickListener(v -> trocar(5));
        findViewById(R.id.chipDom).setOnClickListener(v -> trocar(6));

        btnProximo.setOnClickListener(v -> {
            if (!catalogoCarregado) return;
            diaAtual++;
            if (diaAtual > 6) diaAtual = 0;
            atualizarTela();
        });

        findViewById(R.id.btnSalvarCardapio).setOnClickListener(v -> {
            if (!catalogoCarregado) return;
            salvarSemanaSeCompleta();
        });

        carregarOpcoesLocais();
    }

    private void setNavegacaoHabilitada(boolean habilitada) {
        btnProximo.setEnabled(habilitada);
        findViewById(R.id.btnSalvarCardapio).setEnabled(habilitada);
        int[] chips = {
                R.id.chipSeg, R.id.chipTer, R.id.chipQua, R.id.chipQui,
                R.id.chipSex, R.id.chipSab, R.id.chipDom
        };
        for (int id : chips) {
            findViewById(id).setEnabled(habilitada);
        }
    }

    private void carregarOpcoesLocais() {
        onCatalogoCarregado(new BancoHelper(this).listarOpcoesCriarCardapio());
    }

    private void onCatalogoCarregado(List<CriarCardapioRefeicao> itens) {
        if (itens.isEmpty()) {
            // Não fecha o app — mostra aviso e deixa a tela aberta para o usuário tentar novamente
            Toast.makeText(this, "Nenhuma refeição cadastrada no banco local.", Toast.LENGTH_LONG).show();
            contador.setText("Sem refeições disponíveis");
            return;
        }
        cardapio = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            cardapio.add(itens);
        }
        catalogoCarregado = true;
        setNavegacaoHabilitada(true);
        atualizarTela();
    }

    private void salvarSemanaSeCompleta() {
        if (cardapio == null) return;
        for (int d = 0; d < 7; d++) {
            int n = selecoesPorDia.get(d).size();
            if (n != 4) {
                Toast.makeText(this,
                        "Em " + dias.get(d) + ", escolha 1 café, 1 almoço, 1 lanche da tarde e 1 jantar (agora: " + n + "/4).",
                        Toast.LENGTH_LONG).show();
                diaAtual = d;
                atualizarTela();
                return;
            }
            List<CriarCardapioRefeicao> opcoesDia = cardapio.get(d);
            Set<String> tiposDistintos = new HashSet<>();
            for (int pos : selecoesPorDia.get(d)) {
                tiposDistintos.add(opcoesDia.get(pos).tipo);
            }
            if (tiposDistintos.size() != 4) {
                Toast.makeText(this,
                        "Em " + dias.get(d) + " deve haver uma escolha em cada período (sem repetir o mesmo período).",
                        Toast.LENGTH_LONG).show();
                diaAtual = d;
                atualizarTela();
                return;
            }
        }

        List<List<CardapioItemPersistido>> semana = new ArrayList<>();
        for (int d = 0; d < 7; d++) {
            List<CardapioItemPersistido> itens = new ArrayList<>();
            List<CriarCardapioRefeicao> opcoesDia = cardapio.get(d);
            Set<Integer> sel = selecoesPorDia.get(d);
            String ingPadrao = "Ajuste os ingredientes à sua dieta.";
            String prepPadrao = "Prepare conforme orientação nutricional.";
            for (int pos = 0; pos < opcoesDia.size(); pos++) {
                if (sel.contains(pos)) {
                    CriarCardapioRefeicao r = opcoesDia.get(pos);
                    String ing = (r.ingredientesTexto != null && !r.ingredientesTexto.isEmpty())
                            ? r.ingredientesTexto : ingPadrao;
                    String prep = (r.preparoTexto != null && !r.preparoTexto.isEmpty()
                            && !"—".equals(r.preparoTexto)) ? r.preparoTexto : prepPadrao;
                    itens.add(new CardapioItemPersistido(
                            r.refeicaoId,
                            r.tipo,
                            r.nome,
                            r.tempo,
                            r.kcal,
                            ing,
                            prep));
                }
            }
            CardapioLocalStore.ordenarPorPeriodo(itens);
            semana.add(itens);
        }

        CardapioLocalStore.salvarSemana(this, semana);
        Toast.makeText(this, "Cardápio salvo. Abra a aba Cardápio para ver.", Toast.LENGTH_LONG).show();
        finish();
    }

    private void trocar(int index) {
        if (!catalogoCarregado) return;
        diaAtual = index;
        atualizarTela();
    }

    private void atualizarTela() {
        txtDia.setText(dias.get(diaAtual));
        if (cardapio == null) {
            return;
        }
        Set<Integer> sel = selecoesPorDia.get(diaAtual);
        contador.setText(sel.size() + "/4 períodos");

        recycler.setAdapter(new CriarCardapioRefeicaoAdapter(
                cardapio.get(diaAtual),
                sel,
                total -> contador.setText(total + "/4 períodos")
        ));
    }
}
