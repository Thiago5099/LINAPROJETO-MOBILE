package com.example.projeto.models;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.projeto.R;

public class PlaceholderFragment extends Fragment {

    private String texto;

    public PlaceholderFragment(String texto) {
        super(R.layout.fragment_placeholder);
        this.texto = texto;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        TextView txt = view.findViewById(R.id.txtTitulo);
        txt.setText(texto);
    }
}