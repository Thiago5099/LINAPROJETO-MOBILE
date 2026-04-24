package com.example.tela;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.projeto.R;
import com.example.projeto.Refeicao;
import com.google.android.material.card.MaterialCardView;

import java.util.List;
import java.util.Set;

public class RefeicaoAdapter extends RecyclerView.Adapter<RefeicaoAdapter.ViewHolder> {

    private List<Refeicao> lista;
    private Set<Integer> selecionados;
    private final int LIMITE = 4;

    public interface OnSelecaoChange {
        void onChange(int total);
    }

    private OnSelecaoChange listener;

    public RefeicaoAdapter(List<Refeicao> lista, Set<Integer> selecionados, OnSelecaoChange listener) {
        this.lista = lista;
        this.selecionados = selecionados;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tipo, titulo, info;
        Button botao;
        MaterialCardView card;

        public ViewHolder(View itemView) {
            super(itemView);
            tipo = itemView.findViewById(R.id.txtTipo);
            titulo = itemView.findViewById(R.id.txtTitulo);
            info = itemView.findViewById(R.id.txtInfo);
            botao = itemView.findViewById(R.id.btnReceita);
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

        Refeicao r = lista.get(position);

        // 🔥 SETANDO DADOS
        holder.tipo.setText(r.tipo);
        holder.titulo.setText(r.nome);
        holder.info.setText("⏱\uFE0F"+ r.tempo + " • " +"⚡"+ r.kcal);

        // 🔥 COR DE SELEÇÃO
        if (selecionados.contains(position)) {
            holder.card.setCardBackgroundColor(Color.parseColor("#C8E6C9")); // verde claro
        } else {
            holder.card.setCardBackgroundColor(Color.WHITE);
        }

        // 🔥 CLICK NO CARD (selecionar)
        holder.card.setOnClickListener(v -> {
            if (selecionados.contains(position)) {
                selecionados.remove(position);
            } else {
                if (selecionados.size() < LIMITE) {
                    selecionados.add(position);
                }
            }

            if (listener != null) {
                listener.onChange(selecionados.size());
            }

            notifyItemChanged(position);
        });

        // 🔥 BOTÃO RECEITA (pode evoluir depois)
        holder.botao.setOnClickListener(v -> {
            // depois você pode abrir outra tela aqui
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }
}