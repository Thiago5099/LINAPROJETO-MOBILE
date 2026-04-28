package com.example.projeto.feture.cardapio;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.projeto.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class CardapioMainActivity extends AppCompatActivity {

    BottomNavigationView nav;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_cardapio_main);

        nav = findViewById(R.id.bottomNav);

        // abre cardápio por padrão
        trocarFragment(new CardapioFragment());

        nav.setOnItemSelectedListener(item -> {

            if (item.getItemId() == R.id.nav_cardapio) {
                trocarFragment(new CardapioFragment());
                return true;

            } else {
                Toast.makeText(this, "Tela em desenvolvimento", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    private void trocarFragment(Fragment f) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame, f)
                .commit();
    }
}