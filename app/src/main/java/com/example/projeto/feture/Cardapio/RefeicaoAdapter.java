package com.example.projeto.feture.Cardapio;

import android.content.*;
import android.view.*;
import android.widget.*;

import androidx.recyclerview.widget.RecyclerView;

import com.example.projeto.R;

import java.util.List;

public class RefeicaoAdapter extends RecyclerView.Adapter<RefeicaoAdapter.ViewHolder> {

    Context context;
    List<Refeicao> lista;

    // INTERFACE PARA CLIQUE
    public interface OnMudarClick {
        void onMudar(int posicao, String tipo);
    }

    OnMudarClick listener;

    public RefeicaoAdapter(Context c, List<Refeicao> l, OnMudarClick listener) {
        context = c;
        lista = l;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tipo, nome, info;
        Button receita, mudar;

        public ViewHolder(View v) {
            super(v);
            tipo = v.findViewById(R.id.tipo);
            nome = v.findViewById(R.id.nome);
            info = v.findViewById(R.id.info);
            receita = v.findViewById(R.id.btnReceita);
            mudar = v.findViewById(R.id.btnMudar);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup p, int v) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_cardapio_item_refeicao, p, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder h, int i) {
        Refeicao r = lista.get(i);

        h.tipo.setText(r.tipo);
        h.nome.setText(r.prato.nome);
        h.info.setText(r.prato.tempo + " min | " + r.prato.calorias + " kcal");

        // VER RECEITA
        h.receita.setOnClickListener(v -> {
            Intent it = new Intent(context, ReceitaActivity.class);
            it.putExtra("nome", r.prato.nome);
            it.putExtra("ingredientes", r.prato.ingredientes);
            it.putExtra("preparo", r.prato.preparo);
            context.startActivity(it);
        });

        // MUDAR
        h.mudar.setOnClickListener(v -> {
            int pos = h.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION && listener != null) {
                listener.onMudar(pos, r.tipo);
            }
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }
}