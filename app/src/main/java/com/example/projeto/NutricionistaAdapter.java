package com.example.projeto;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class NutricionistaAdapter extends RecyclerView.Adapter<NutricionistaAdapter.ViewHolder> {

    private ArrayList<Nutricionista> lista;

    public NutricionistaAdapter(ArrayList<Nutricionista> lista) {
        this.lista = lista;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_nutricionista, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Nutricionista n = lista.get(position);
        holder.txtNome.setText(n.getNome());
        holder.txtEspecialidade.setText(n.getEspecialidade());
        holder.txtCidade.setText(n.getCidade());
        holder.txtTelefone.setText(n.getTelefone());
        holder.txtEmail.setText(n.getEmail());
        holder.txtPacientes.setText(n.getPacientes());


        holder.btnContato.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + n.getTelefone()));
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() { return lista.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNome, txtEspecialidade, txtCidade, txtTelefone, txtEmail, txtPacientes; // ← adicione txtEmail e txtPacientes
        Button btnContato;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNome = itemView.findViewById(R.id.txtNome);
            txtEspecialidade = itemView.findViewById(R.id.txtEspecialidade);
            txtCidade = itemView.findViewById(R.id.txtCidade);
            txtTelefone = itemView.findViewById(R.id.txtTelefone);
            txtEmail = itemView.findViewById(R.id.txtEmail);         // ← adicione
            txtPacientes = itemView.findViewById(R.id.txtPacientes);
            btnContato = itemView.findViewById(R.id.btnContato);
        }
    }
}