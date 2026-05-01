package com.example.projeto.feture.Nutricionistas;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projeto.R;

import java.util.ArrayList;

public class NutricionistaAdapter extends RecyclerView.Adapter<NutricionistaAdapter.ViewHolder> {

    private ArrayList<Nutricionista> lista;
    private Context context;

    public NutricionistaAdapter(Context context, ArrayList<Nutricionista> lista) {
        this.context = context;
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

        // Estrelas dinâmicas
        LinearLayout layoutEstrelas = holder.itemView.findViewById(R.id.layoutEstrelas);
        layoutEstrelas.removeAllViews();
        int estrelas = Math.round(n.getAvaliacao());
        for (int i = 0; i < 5; i++) {
            ImageView img = new ImageView(holder.itemView.getContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(40, 40);
            params.setMargins(2, 0, 2, 0);
            img.setLayoutParams(params);
            if (i < estrelas) {
                img.setImageResource(R.drawable.ic_nutricionistasa_estrela_cheia);
            } else {
                img.setImageResource(R.drawable.ic_nutricionistas_estrela_vazia);
            }
            layoutEstrelas.addView(img);
        }

        holder.btnContato.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + n.getTelefone()));
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() { return lista.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNome, txtEspecialidade, txtCidade, txtTelefone, txtEmail, txtPacientes;
        Button btnContato;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNome = itemView.findViewById(R.id.txtNome);
            txtEspecialidade = itemView.findViewById(R.id.txtEspecialidade);
            txtCidade = itemView.findViewById(R.id.txtCidade);
            txtTelefone = itemView.findViewById(R.id.txtTelefone);
            txtEmail = itemView.findViewById(R.id.txtEmail);
            txtPacientes = itemView.findViewById(R.id.txtPacientes);
            btnContato = itemView.findViewById(R.id.btnContato);
        }
    }
}