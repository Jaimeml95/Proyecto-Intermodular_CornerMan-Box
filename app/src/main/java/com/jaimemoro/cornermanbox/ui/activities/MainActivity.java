package com.jaimemoro.cornermanbox.ui.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar; // Importación necesaria para la barra de herramientas
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

        // Localizar la Toolbar y configurarla como la ActionBar de la actividad.
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Localizar el NavHostFragment
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment != null) {
            // Obtener el NavController desde el NavHostFragment
            NavController navController = navHostFragment.getNavController();

            // Localizar el BottomNavigationView
            BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

            // Configuramos qué destinos son de nivel superior
            // Para que al usar el botón de "Empezar Ya" no haya problemas.
            // También define en qué pantallas NO debe aparecer la flecha de "atrás" automáticamente.
            AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.navigation_dashboard,
                    R.id.navigation_training,
                    R.id.navigation_library)
                    .build();

            // Vincular el menú inferior con el controlador de navegación
            // Esto hace que, al pulsar un botón del menú, se cargue el fragmento con el MISMO ID
            NavigationUI.setupWithNavController(bottomNav, navController);

            // Vincular la Toolbar con el controlador de navegación y la configuración de nivel superior.
            // Esto sincroniza el título de la barra con el nombre del fragmento y gestiona el menú superior.
            NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration);
        }
    }
}