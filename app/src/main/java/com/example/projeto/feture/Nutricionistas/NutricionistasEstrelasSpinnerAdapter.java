package com.example.projeto.feture.Nutricionistas;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.projeto.R;

public class NutricionistasEstrelasSpinnerAdapter extends BaseAdapter {

    private Context context;

    public NutricionistasEstrelasSpinnerAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() { return 6; }

    @Override
    public Object getItem(int position) { return position; }

    @Override
    public long getItemId(int position) { return position; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return criarView(position, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return criarView(position, parent);
    }

    private View criarView(int position, ViewGroup parent) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_spinner_estrelas_nutricionistas, parent, false);

        LinearLayout layoutEstrelas = view.findViewById(R.id.layoutEstrelas);
        layoutEstrelas.removeAllViews();

        if (position == 0) {
            TextView txt = new TextView(context);
            txt.setText("Avaliação");
            txt.setTextSize(16f);
            txt.setTextColor(0xFF000000);
            layoutEstrelas.addView(txt);
        } else {
            for (int i = 0; i < 5; i++) {
                ImageView img = new ImageView(context);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(48, 48);
                params.setMargins(2, 0, 2, 0);
                img.setLayoutParams(params);
                img.setImageResource(i < position ? R.drawable.ic_nutricionistasa_estrela_cheia : R.drawable.ic_nutricionistas_estrela_vazia);
                layoutEstrelas.addView(img);
            }
        }

        return view;
    }
}