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
        TextView tvTipoRefeicao, tvNomeRefeicao, tvTempo, tvCalorias;
        Button btnVerReceita;

        public ViewHolder(View v) {
            super(v);
            tvTipoRefeicao = v.findViewById(R.id.tvTipoRefeicao);
            tvNomeRefeicao = v.findViewById(R.id.tvNomeRefeicao);
            tvTempo        = v.findViewById(R.id.tvTempo);
            tvCalorias     = v.findViewById(R.id.tvCalorias);
            btnVerReceita  = v.findViewById(R.id.btnVerReceita);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup p, int v) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_refeicao_adapter, p, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder h, int i) {
        Refeicao r = lista.get(i);

        h.tvTipoRefeicao.setText(r.tipo);
        h.tvNomeRefeicao.setText(r.prato.nome);
        h.tvTempo.setText(r.prato.tempo + " min");
        h.tvCalorias.setText(r.prato.calorias + " kcal");

        // VER RECEITA
        h.btnVerReceita.setOnClickListener(v -> {
            Intent it = new Intent(context, ReceitaActivity.class);
            it.putExtra("nome", r.prato.nome);
            it.putExtra("ingredientes", r.prato.ingredientes);
            it.putExtra("preparo", r.prato.preparo);
            context.startActivity(it);
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }
}