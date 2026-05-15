package com.jaimemoro.cornermanbox.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.jaimemoro.cornermanbox.R;
import com.jaimemoro.cornermanbox.viewmodel.DashboardViewModel;

public class DashboardFragment extends Fragment {

    private TextView tvStreakCount, tvTotalPoints, tvNextSessionText, tvGreeting;
    private ExtendedFloatingActionButton fabStart;
    private DashboardViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        tvGreeting = root.findViewById(R.id.tv_greeting);
        tvStreakCount = root.findViewById(R.id.tv_streak_count);
        tvTotalPoints = root.findViewById(R.id.tv_total_points);
        tvNextSessionText = root.findViewById(R.id.tv_next_session_text);
        fabStart = root.findViewById(R.id.fab_start_training);

        viewModel = new ViewModelProvider(this).get(DashboardViewModel.class);

        if (fabStart != null) {
            fabStart.setOnClickListener(v -> {
                com.google.android.material.bottomnavigation.BottomNavigationView bottomNav =
                        getActivity().findViewById(R.id.bottom_navigation);

                if (bottomNav != null) {
                    bottomNav.setSelectedItemId(R.id.navigation_training);
                }
            });
        }

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MenuHost menuHost = requireActivity();
        menuHost.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.dashboard_menu, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.settingsFragment) {
                    Navigation.findNavController(requireView()).navigate(R.id.settingsFragment);
                    return true;
                }
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);

        viewModel.getUsuario().observe(getViewLifecycleOwner(), usuario -> {
            if (usuario != null) {
                actualizarInterfaz();
            } else {
                configurarEstadoNuevoUsuario();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.cargarUsuario();
    }

    private void actualizarInterfaz() {
        String nombreMostrar = viewModel.getNombreMostrar();

        if (tvGreeting != null) {
            tvGreeting.setText("¡Hola, " + nombreMostrar + "!");
        }

        tvStreakCount.setText(viewModel.getTextoRacha());
        tvTotalPoints.setText(viewModel.getTextoPuntos());
        tvNextSessionText.setText(viewModel.getTextoApoyo());
    }

    private void configurarEstadoNuevoUsuario() {
        if (tvGreeting != null) tvGreeting.setText("¡Hola, Boxeador!");
        tvStreakCount.setText("0 Días seguidos");
        tvTotalPoints.setText("0 pts");
        tvNextSessionText.setText("¡Bienvenido! Empieza tu primer entreno.");
    }
}