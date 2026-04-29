//package com.example.projeto.feture.MudarCardapio;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.projeto.R;
//
//import java.util.List;
//
//public class RefeicaoAdapter extends RecyclerView.Adapter<RefeicaoAdapter.ViewHolder> {
//
//    public interface OnRefeicaoSelecionada {
//        void onSelecionada(Refeicao refeicao);
//    }
//
//    private final List<Refeicao> lista;
//    private final OnRefeicaoSelecionada listener;
//
//    public RefeicaoAdapter(List<Refeicao> lista, OnRefeicaoSelecionada listener) {
//        this.lista    = lista;
//        this.listener = listener;
//    }
//
//    public static class ViewHolder extends RecyclerView.ViewHolder {
//        TextView tvTipo, tvNome, tvTempo, tvCalorias;
//        Button btnVerReceita;
//
//        public ViewHolder(View view) {
//            super(view);
//            tvTipo        = view.findViewById(R.id.tvTipoRefeicao);
//            tvNome        = view.findViewById(R.id.tvNomeRefeicao);
//            tvTempo       = view.findViewById(R.id.tvTempo);
//            tvCalorias    = view.findViewById(R.id.tvCalorias);
//            btnVerReceita = view.findViewById(R.id.btnVerReceita);
//        }
//    }
//
//    @NonNull
//    @Override
//    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.activity_item_refeicao, parent, false);
//        return new ViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        Refeicao refeicao = lista.get(position);
//
//        holder.tvTipo.setText(refeicao.getTipo());
//        holder.tvNome.setText(refeicao.getNome());
//        holder.tvTempo.setText(refeicao.getTempo());
//        holder.tvCalorias.setText(refeicao.getCalorias());
//
//        holder.btnVerReceita.setOnClickListener(v ->
//                listener.onSelecionada(refeicao)
//        );
//    }
//
//    @Override
//    public int getItemCount() {
//        return lista.size();
//    }
//}