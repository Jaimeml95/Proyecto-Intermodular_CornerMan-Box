package com.jaimemoro.cornermanbox.ui.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import androidx.navigation.ui.AppBarConfiguration;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jaimemoro.cornermanbox.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Localizar el NavHostFragment (el contenedor de los fragments)
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment != null) {
            // Obtener el NavController desde el NavHostFragment
            NavController navController = navHostFragment.getNavController();

            // Localizar el BottomNavigationView
            BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

            // Configuramos qué destinos son de nivel superior
            // Para que al usar el botón de "Empezar Ya" no haya problemas.
            AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.navigation_dashboard,
                    R.id.navigation_training,
                    R.id.navigation_library)
                    .build();

            // Vincular el menú inferior con el controlador de navegación
            // Esto hace que, al pulsar un botón del menú, se cargue el fragmento con el MISMO ID
            NavigationUI.setupWithNavController(bottomNav, navController);
        }
    }
}