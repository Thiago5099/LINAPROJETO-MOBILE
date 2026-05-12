package com.example.projeto.Feature.Menu;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.projeto.R;
import com.example.projeto.Feature.Cardapio.CardapioFragment;
import com.example.projeto.Feature.Compras.models.ComprasFragment;
import com.example.projeto.Feature.Nutricionistas.NutricionistasFragment;
import com.example.projeto.Feature.Perfil.PerfilFragment;
import com.example.projeto.Feature.Perfil.PerfilPremiumFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.activity.EdgeToEdge;

public class Menu extends AppCompatActivity {

    BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menu);

        // Adiciona isso:
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        bottomNav = findViewById(R.id.bottomNav);

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
                boolean isPremium = getSharedPreferences("prefs", MODE_PRIVATE)
                        .getBoolean("isPremium", false);


                selected = isPremium ? new PerfilPremiumFragment() : new PerfilFragment();
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