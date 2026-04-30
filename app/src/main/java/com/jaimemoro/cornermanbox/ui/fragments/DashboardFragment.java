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
import androidx.room.Room;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.jaimemoro.cornermanbox.R;
import com.jaimemoro.cornermanbox.data.entities.Usuario;
import com.jaimemoro.cornermanbox.data.local.AppDatabase;

import java.util.Calendar;

public class DashboardFragment extends Fragment {

    private TextView tvStreakCount, tvTotalPoints, tvNextSessionText;
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
            AppDatabase db = Room.databaseBuilder(requireContext(),
                    AppDatabase.class, "cornerman-db").build();

            Usuario user = db.usuarioDao().getUsuario();

            if (user != null) {
                // --- LÓGICA DE VERIFICACIÓN DE RACHA PARA API 24 (Calendar) ---
                Calendar calUltimo = Calendar.getInstance();
                calUltimo.setTimeInMillis(user.lastTrainingDate);
                calUltimo.set(Calendar.HOUR_OF_DAY, 0);
                calUltimo.set(Calendar.MINUTE, 0);
                calUltimo.set(Calendar.SECOND, 0);
                calUltimo.set(Calendar.MILLISECOND, 0);

                Calendar calHoy = Calendar.getInstance();
                calHoy.set(Calendar.HOUR_OF_DAY, 0);
                calHoy.set(Calendar.MINUTE, 0);
                calHoy.set(Calendar.SECOND, 0);
                calHoy.set(Calendar.MILLISECOND, 0);

                long diffMillis = calHoy.getTimeInMillis() - calUltimo.getTimeInMillis();
                long diasDiferencia = diffMillis / (24 * 60 * 60 * 1000);

                if (diasDiferencia > 1) {
                    user.dailyStreak = 0;
                    db.usuarioDao().updateUsuario(user);
                }
            }

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    if (user != null) {
                        // Manejo de singular/plural para la racha
                        String textoRacha = (user.dailyStreak == 1)
                                ? "1 Día entrenado"
                                : user.dailyStreak + " Días seguidos";

                        tvStreakCount.setText(textoRacha);
                        tvTotalPoints.setText(String.format("%,d pts", user.totalPoints));

                        // Saludo personalizado con el nombre guardado en Ajustes
                        tvNextSessionText.setText("¡Mantén el ritmo, " + user.nombre + "!");

                        if (user.dailyStreak == 0) {
                            tvNextSessionText.setText("Has perdido la racha. ¡A por ello de nuevo, " + user.nombre + "!");
                        }
                    } else {
                        tvStreakCount.setText("0 Días seguidos");
                        tvTotalPoints.setText("0 pts");
                        tvNextSessionText.setText("¡Bienvenido! Empieza tu primer entreno.");
                    }
                    db.close();
                });
            }
        }).start();
    }
}