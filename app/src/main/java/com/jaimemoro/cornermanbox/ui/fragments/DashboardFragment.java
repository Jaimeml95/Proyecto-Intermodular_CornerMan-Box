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
import com.jaimemoro.cornermanbox.core.domain.model.Usuario;
import com.jaimemoro.cornermanbox.ui.viewmodel.StatsViewModel;

public class DashboardFragment extends Fragment {

    private TextView tvStreakCount, tvTotalPoints, tvNextSessionText, tvGreeting;
    private ExtendedFloatingActionButton fabStart;
    private StatsViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        tvGreeting = root.findViewById(R.id.tv_greeting);
        tvStreakCount = root.findViewById(R.id.tv_streak_count);
        tvTotalPoints = root.findViewById(R.id.tv_total_points);
        tvNextSessionText = root.findViewById(R.id.tv_next_session_text);
        fabStart = root.findViewById(R.id.fab_start_training);

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

        viewModel = new ViewModelProvider(this).get(StatsViewModel.class);

        viewModel.getUsuario().observe(getViewLifecycleOwner(), usuario -> {
            if (usuario != null) {
                actualizarInterfaz(usuario);
            } else {
                configurarEstadoNuevoUsuario();
            }
        });

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
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.cargarUsuario();
    }

    private void actualizarInterfaz(@NonNull Usuario user) {
        String nombreAMostrar = (user.getNombre() != null && !user.getNombre().trim().isEmpty())
                ? user.getNombre()
                : "Boxeador";

        if (tvGreeting != null) {
            tvGreeting.setText("¡Hola, " + nombreAMostrar + "!");
        }

        String textoRacha = (user.getDailyStreak() == 1)
                ? "1 Día entrenado"
                : user.getDailyStreak() + " Días seguidos";
        tvStreakCount.setText(textoRacha);

        tvTotalPoints.setText(String.format("%,d pts", user.getTotalPoints()));

        if (user.getDailyStreak() == 0) {
            tvNextSessionText.setText("Has perdido la racha. ¡A por ello de nuevo, " + nombreAMostrar + "!");
        } else {
            tvNextSessionText.setText("¡Mantén el ritmo, " + nombreAMostrar + "!");
        }
    }

    private void configurarEstadoNuevoUsuario() {
        if (tvGreeting != null) tvGreeting.setText("¡Hola, Boxeador!");
        tvStreakCount.setText("0 Días seguidos");
        tvTotalPoints.setText("0 pts");
        tvNextSessionText.setText("¡Bienvenido! Empieza tu primer entreno.");
    }
}