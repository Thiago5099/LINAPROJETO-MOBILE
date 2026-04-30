package com.example.projeto.feture.Compras.models;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.projeto.R;

public class ComprasPlaceholderFragment extends Fragment {

    private String texto;

    public ComprasPlaceholderFragment(String texto) {
        super(R.layout.fragment_placeholder_compras);
        this.texto = texto;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        TextView txt = view.findViewById(R.id.txtTitulo);
        txt.setText(texto);
    }
}