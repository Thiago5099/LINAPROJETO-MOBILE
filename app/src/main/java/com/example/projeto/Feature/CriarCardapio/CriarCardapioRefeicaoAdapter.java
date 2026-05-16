package com.example.projeto.Feature.CriarCardapio;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.projeto.R;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class CriarCardapioRefeicaoAdapter extends RecyclerView.Adapter<CriarCardapioRefeicaoAdapter.ViewHolder> {

    private List<CriarCardapioRefeicao> lista;
    private Set<Integer> selecionados;
    /** Um índice selecionado por período (café, almoço, lanche da tarde, jantar) — no máximo 4. */

    public interface OnSelecaoChange {
        void onChange(int total);
    }

    private OnSelecaoChange listener;

    public CriarCardapioRefeicaoAdapter(List<CriarCardapioRefeicao> lista, Set<Integer> selecionados, OnSelecaoChange listener) {
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
                .inflate(R.layout.item_criar_cardapio_refeicao, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        CriarCardapioRefeicao r = lista.get(position);

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

        // Um período = um tipo (Café da manhã, Almoço, Lanche da tarde, Jantar).
        // Ao escolher outra opção do mesmo período, a anterior é desmarcada.
        holder.card.setOnClickListener(v -> {
            if (selecionados.contains(position)) {
                selecionados.remove(position);
                notifyItemChanged(position);
            } else {
                String tipoSel = lista.get(position).tipo;
                List<Integer> desmarcar = new ArrayList<>();
                Iterator<Integer> it = selecionados.iterator();
                while (it.hasNext()) {
                    int idx = it.next();
                    if (mesmoPeriodo(tipoSel, lista.get(idx).tipo)) {
                        it.remove();
                        desmarcar.add(idx);
                    }
                }
                selecionados.add(position);
                for (int idx : desmarcar) {
                    notifyItemChanged(idx);
                }
                notifyItemChanged(position);
            }

            if (listener != null) {
                listener.onChange(selecionados.size());
            }
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

    private static boolean mesmoPeriodo(String tipoA, String tipoB) {
        if (tipoA == null || tipoB == null) return false;
        return tipoA.equals(tipoB);
    }
}