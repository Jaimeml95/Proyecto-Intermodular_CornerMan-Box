package com.jaimemoro.cornermanbox.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.jaimemoro.cornermanbox.R;

public class DashboardFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflamos la vista y la guardamos en una variable 'root'
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        // Localizamos el FAB por su ID
        ExtendedFloatingActionButton fabStart = root.findViewById(R.id.fab_start_training);
        // Le asignamos la funcionalidad de clic
        if (fabStart != null) {
            fabStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 1. Buscamos el BottomNavigationView que reside en la MainActivity
                    com.google.android.material.bottomnavigation.BottomNavigationView bottomNav =
                            getActivity().findViewById(R.id.bottom_navigation);

                    // 2. Le decimos que seleccione la pestaña de Entreno
                    // IMPORTANTE: El ID debe ser el mismo que en tu bottom_nav_menu.xml
                    if (bottomNav != null) {
                        bottomNav.setSelectedItemId(R.id.navigation_training);
                    }
                }
            });
        }

        return root;
    }
}