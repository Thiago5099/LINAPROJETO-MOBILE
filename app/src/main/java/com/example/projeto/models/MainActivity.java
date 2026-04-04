package com.example.projeto.models;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.projeto.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNav = findViewById(R.id.bottomNav);

        // Tela inicial
        loadFragment(new ComprasFragment());
        // loadFragment(new LoginFragment()); *?*

        bottomNav.setOnItemSelectedListener(item -> {

            Fragment selected = null;

            if (item.getItemId() == R.id.nav_cardapio) {
                selected = new PlaceholderFragment("Cardápio (em desenvolvimento)");
                // if (item.getItemId() == R.id.nav_cardapio) {
                //   selected = new CardapioFragment();

            } else if (item.getItemId() == R.id.nav_compras) {
                selected = new ComprasFragment();

            } else if (item.getItemId() == R.id.nav_nutri) {
                selected = new PlaceholderFragment("Nutri (em desenvolvimento)");
                // } else if (item.getItemId() == R.id.nav_nutri) {
                //     selected = new NutriFragment();

            } else if (item.getItemId() == R.id.nav_perfil) {
                selected = new PlaceholderFragment("Perfil (em desenvolvimento)");
                // } else if (item.getItemId() == R.id.nav_perfil) {
                //     selected = new PerfilFragment();
            }

            return loadFragment(selected);
        });
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame, fragment)
                    .commit();
            return true;
        }
        return false;
    }
}