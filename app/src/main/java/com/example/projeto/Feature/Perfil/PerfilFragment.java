package com.example.projeto.Feature.Perfil;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.projeto.R;
import com.example.projeto.Feature.Pagamento.Pagamento;

public class PerfilFragment extends Fragment {


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_perfil, container, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // sua lógica aqui quando tiver

        view.findViewById(R.id.btnAssinatura).setOnClickListener(v -> {
            startActivity(new Intent(requireActivity(), Pagamento.class));
        });

        view.findViewById(R.id.buttonConta).setOnClickListener(v -> {
            startActivity(new Intent(requireActivity(), com.example.projeto.Feature.AtulizarPerfil.AtualizarPerfil.class));
        });

    }
}