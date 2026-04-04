package com.example.projeto.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.projeto.R;
import com.example.projeto.models.Ingrediente;
import com.example.projeto.models.ItemLista;

import java.util.List;

public class ComprasAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ItemLista> lista;
    private Runnable onCheckChanged;

    public ComprasAdapter(List<ItemLista> lista, Runnable callback) {
        this.lista = lista;
        this.onCheckChanged = callback;
    }

    @Override
    public int getItemViewType(int position) {
        return lista.get(position).getTipo();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == ItemLista.TIPO_HEADER) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_header, parent, false);
            return new HeaderViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_compra, parent, false);
            return new ItemViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        ItemLista item = lista.get(position);

        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).bind(item);
        } else {
            ((ItemViewHolder) holder).bind(item.getIngrediente());
        }
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    // HEADER
    static class HeaderViewHolder extends RecyclerView.ViewHolder {

        TextView txtCategoria;
        ImageView icon;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            txtCategoria = itemView.findViewById(R.id.txtCategoria);
            icon = itemView.findViewById(R.id.iconCategoria);
        }

        void bind(ItemLista item) {
            txtCategoria.setText(item.getCategoria());

            // ícones simples
            switch (item.getCategoria()) {
                case "Frutas":
                    icon.setImageResource(R.drawable.ic_frutas);
                    break;
                case "Proteínas":
                    icon.setImageResource(R.drawable.ic_proteina);
                    break;
                default:
                    icon.setImageResource(R.drawable.ic_default);
            }
        }
    }

    // ITEM
    class ItemViewHolder extends RecyclerView.ViewHolder {

        CheckBox check;
        TextView nome, qtd;

        public ItemViewHolder(View v) {
            super(v);
            check = v.findViewById(R.id.checkItem);
            nome = v.findViewById(R.id.txtNome);
            qtd = v.findViewById(R.id.txtQuantidade);
        }

        void bind(Ingrediente i) {
            nome.setText(i.getNome());
            qtd.setText("(x" + i.getQuantidade() + ")");
            check.setChecked(i.isComprado());

            check.setOnCheckedChangeListener((b, checked) -> {
                i.setComprado(checked);
                onCheckChanged.run();
            });
        }
    }
}