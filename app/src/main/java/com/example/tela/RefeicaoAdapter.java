package com.example.tela;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.projeto.R;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RefeicaoAdapter extends RecyclerView.Adapter<RefeicaoAdapter.ViewHolder> {

    private List<String> lista;
    private Set<Integer> selecionados = new HashSet<>();
    private final int LIMITE = 4;

    public RefeicaoAdapter(List<String> lista) {
        this.lista = lista;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titulo;
        MaterialCardView card;

        public ViewHolder(View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.txtTitulo);
            card = itemView.findViewById(R.id.cardItem);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_refeicao, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.titulo.setText(lista.get(position));

        if (selecionados.contains(position)) {
            holder.card.setCardBackgroundColor(Color.parseColor("#C8E6C9"));
        } else {
            holder.card.setCardBackgroundColor(Color.WHITE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (selecionados.contains(position)) {
                selecionados.remove(position);
            } else {
                if (selecionados.size() < LIMITE) {
                    selecionados.add(position);
                }
            }
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }
}