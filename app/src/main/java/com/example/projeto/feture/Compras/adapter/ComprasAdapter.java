package com.example.projeto.feture.Compras.adapter;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.projeto.R;
import com.example.projeto.feture.Compras.models.ComprasIngrediente;
import com.example.projeto.feture.Compras.models.ComprasItemLista;

import java.util.List;

public class ComprasAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ComprasItemLista> lista;
    private Runnable onCheckChanged;

    public ComprasAdapter(List<ComprasItemLista> lista, Runnable callback) {
        this.lista = lista;
        this.onCheckChanged = callback;
    }

    @Override
    public int getItemViewType(int position) {
        return lista.get(position).getTipo();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == ComprasItemLista.TIPO_HEADER) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_header_compras, parent, false);
            return new HeaderViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_compras, parent, false);
            return new ItemViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        ComprasItemLista item = lista.get(position);

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

        void bind(ComprasItemLista item) {
            txtCategoria.setText(item.getCategoria());

            // ícones simples
            switch (item.getCategoria()) {
                case "Frutas e Vegetais":
                    icon.setImageResource(R.drawable.ic_compras_frutas);
                    break;
                case "Grãos e Cereais":
                    icon.setImageResource(R.drawable.ic_compras_graos);
                    break;
                case "Laticínios":
                    icon.setImageResource(R.drawable.ic_compras_laticinios);
                    break;
                case "Proteínas":
                    icon.setImageResource(R.drawable.ic_compras_proteinas);
                    break;
                default:
                    icon.setImageResource(R.drawable.ic_compras_proteinas);
            }

            int widthInDp = 21;
            int heightInDp = 21;

            int widthInPx = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, widthInDp, icon.getResources().getDisplayMetrics()
            );
            int heightInPx = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, heightInDp, icon.getResources().getDisplayMetrics()
            );

            ViewGroup.LayoutParams params = icon.getLayoutParams();
            params.width = widthInPx;
            params.height = heightInPx;
            icon.setLayoutParams(params);
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

        void bind(ComprasIngrediente i) {
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