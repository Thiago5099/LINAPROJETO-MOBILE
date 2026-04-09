package com.example.projeto;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.projeto.Nutricionista;
import com.example.projeto.R;

import java.util.ArrayList;

public class NutricionistaAdapter extends RecyclerView.Adapter<NutricionistaAdapter.ViewHolder> {

    ArrayList<Nutricionista> lista;

    public NutricionistaAdapter(ArrayList<Nutricionista> lista) {
        this.lista = lista;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nome, especialidade, cidade, telefone;
        Button btn;

        public ViewHolder(View v) {
            super(v);
            nome = v.findViewById(R.id.txtNome);
            especialidade = v.findViewById(R.id.txtEspecialidade);
            cidade = v.findViewById(R.id.txtCidade);
            telefone = v.findViewById(R.id.txtTelefone);
            btn = v.findViewById(R.id.btnContato);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_nutricionista, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Nutricionista n = lista.get(position);

        holder.nome.setText(n.nome);
        holder.especialidade.setText(n.especialidade);
        holder.cidade.setText(n.cidade);
        holder.telefone.setText(n.telefone);

        holder.btn.setOnClickListener(v ->
                Toast.makeText(v.getContext(),
                        "Contato: " + n.telefone,
                        Toast.LENGTH_SHORT).show());
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }
}