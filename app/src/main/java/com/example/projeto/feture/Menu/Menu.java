package com.example.projeto.feture.Menu;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.projeto.R;
import com.example.projeto.feture.Cardapio.CardapioFragment;
import com.example.projeto.feture.Compras.models.ComprasFragment;
import com.example.projeto.feture.Nutricionistas.NutricionistasFragment;
import com.example.projeto.feture.Perfil.PerfilFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Menu extends AppCompatActivity {

    BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        bottomNav = findViewById(R.id.bottomNav);

        // Tela inicial
        loadFragment(new CardapioFragment());

        bottomNav.setOnItemSelectedListener(item -> {

            Fragment selected = null;

            if (item.getItemId() == R.id.nav_cardapio) {
                selected = new CardapioFragment();

            } else if (item.getItemId() == R.id.nav_compras) {
                selected = new ComprasFragment();

            } else if (item.getItemId() == R.id.nav_nutri) {
                selected = new NutricionistasFragment();


            } else if (item.getItemId() == R.id.nav_perfil) {
                selected = new PerfilFragment();


                /*
            } else if (item.getItemId() == R.id.nav_perfil) {
                selected = new PerfilPremiumFragment();
               */

            }

            return loadFragment(selected);
        });
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frame, fragment).commit();
            return true;
        }
        return false;
    }
}