package com.example.projeto;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    EditText editNome, editEspecialidade, editCidade, editTelefone;
    Button btnSalvar;
    ListView listView;

    BancoHelper banco;
    ArrayAdapter<String> adapter;
    ArrayList<String> lista;
    ArrayList<Integer> listaIds;

    private void carregarDados() {
        Cursor cursor = banco.listar();

        lista = new ArrayList<>();
        listaIds = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String nome = cursor.getString(1);
                String esp = cursor.getString(2);
                String cidade = cursor.getString(3);
                String tel = cursor.getString(4);

                lista.add(id + " - " + nome + " - " + esp + " - " + cidade + " - " + tel);
                listaIds.add(id);

            } while (cursor.moveToNext());
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, lista);
        listView.setAdapter(adapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editNome = findViewById(R.id.editNome);
        editEspecialidade = findViewById(R.id.editEspecialidade);
        editCidade = findViewById(R.id.editCidade);
        editTelefone = findViewById(R.id.editTelefone);

        btnSalvar = findViewById(R.id.btnSalvar);
        listView = findViewById(R.id.listView);

        banco = new BancoHelper(this);

        final int[] selecionado = {-1};

        // BOTÃO SALVAR
        btnSalvar.setOnClickListener(v -> {

            String nome = editNome.getText().toString();
            String esp = editEspecialidade.getText().toString();
            String cidade = editCidade.getText().toString();
            String tel = editTelefone.getText().toString();

            if (nome.isEmpty() || esp.isEmpty() || cidade.isEmpty() || tel.isEmpty()) {
                Toast.makeText(this, "Preencha tudo!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selecionado[0] == -1) {

                long res = banco.inserir(nome, esp, cidade, tel);

                Toast.makeText(this,
                        res != -1 ? "Salvo!" : "Erro!",
                        Toast.LENGTH_SHORT).show();

            } else {

                int res = banco.atualizar(selecionado[0], nome, esp, cidade, tel);

                Toast.makeText(this,
                        res > 0 ? "Atualizado!" : "Erro!",
                        Toast.LENGTH_SHORT).show();

                selecionado[0] = -1;
                btnSalvar.setText("Salvar");
            }

            limpar();
            carregarDados();
        });

        // CLICK EDITAR
        listView.setOnItemClickListener((parent, view, position, id) -> {

            selecionado[0] = listaIds.get(position);

            Cursor c = banco.buscarPorId(selecionado[0]);

            if (c.moveToFirst()) {
                editNome.setText(c.getString(1));
                editEspecialidade.setText(c.getString(2));
                editCidade.setText(c.getString(3));
                editTelefone.setText(c.getString(4));

                btnSalvar.setText("Atualizar");
            }
        });

        // CLICK LONGO (DELETAR)
        listView.setOnItemLongClickListener((parent, view, position, id) -> {

            int idItem = listaIds.get(position);

            int del = banco.excluir(idItem);

            if (del > 0) {
                Toast.makeText(this, "Excluído!", Toast.LENGTH_SHORT).show();
                carregarDados();
            }

            return true;
        });

        carregarDados();
    }

    private void limpar() {
        editNome.setText("");
        editEspecialidade.setText("");
        editCidade.setText("");
        editTelefone.setText("");
    }
}