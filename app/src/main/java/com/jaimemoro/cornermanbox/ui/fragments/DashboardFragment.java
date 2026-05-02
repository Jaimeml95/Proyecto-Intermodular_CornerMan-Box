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
import androidx.navigation.Navigation;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.jaimemoro.cornermanbox.R;
import com.jaimemoro.cornermanbox.data.entities.Usuario;
import com.jaimemoro.cornermanbox.data.local.AppDatabase;
import com.jaimemoro.cornermanbox.utils.StatsManager;

import java.util.Calendar;

public class DashboardFragment extends Fragment {

    private TextView tvStreakCount, tvTotalPoints, tvNextSessionText, tvGreeting;
    private ExtendedFloatingActionButton fabStart;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        // Inicializar vistas
        tvStreakCount = root.findViewById(R.id.tv_streak_count);
        tvTotalPoints = root.findViewById(R.id.tv_total_points);
        tvNextSessionText = root.findViewById(R.id.tv_next_session_text);
        fabStart = root.findViewById(R.id.fab_start_training);
        tvGreeting = root.findViewById(R.id.tv_greeting);

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

        // --- CONFIGURACIÓN DEL MENÚ DE AJUSTES ---
        MenuHost menuHost = requireActivity();
        menuHost.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.dashboard_menu, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                // Manejar el clic en el icono de engranaje
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
        // Refresca los datos cada vez que el fragmento se hace visible
        cargarEstadisticasUsuario();
    }

    private void cargarEstadisticasUsuario() {
        new Thread(() -> {
            AppDatabase db = AppDatabase.getInstance(requireContext());
            Usuario user = db.usuarioDao().getUsuario();

            // Delegamos la lógica de la racha al StatsManager
            if (user != null) {
                StatsManager.validarRachaAlEntrar(user, db);
            }

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    if (user != null) {
                        // Nombre y Saludo
                        String nombreAMostrar = (user.nombre != null && !user.nombre.trim().isEmpty())
                                ? user.nombre
                                : "Boxeador";

                        if (tvGreeting != null) tvGreeting.setText("¡Hola, " + nombreAMostrar + "!");

                        // Racha
                        String textoRacha = (user.dailyStreak == 1) ? "1 Día entrenado" : user.dailyStreak + " Días seguidos";
                        tvStreakCount.setText(textoRacha);

                        // Puntos
                        tvTotalPoints.setText(String.format("%,d pts", user.totalPoints));

                        // Texto de apoyo dinámico
                        if (user.dailyStreak == 0) {
                            tvNextSessionText.setText("Has perdido la racha. ¡A por ello de nuevo, " + nombreAMostrar + "!");
                        } else {
                            tvNextSessionText.setText("¡Mantén el ritmo, " + nombreAMostrar + "!");
                        }

                    } else {
                        // Usuario nuevo
                        if (tvGreeting != null) tvGreeting.setText("¡Hola, Boxeador!");
                        tvStreakCount.setText("0 Días seguidos");
                        tvTotalPoints.setText("0 pts");
                        tvNextSessionText.setText("¡Bienvenido! Empieza tu primer entreno.");
                    }
                });
            }
        }).start();
    }
}