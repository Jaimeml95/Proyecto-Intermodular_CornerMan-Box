package com.jaimemoro.cornermanbox.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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
    public void onResume() {
        super.onResume();
        // REFRESCAR DATOS: Cada vez que el fragmento se hace visible
        cargarEstadisticasUsuario();
    }

    private void cargarEstadisticasUsuario() {
        new Thread(() -> {
            AppDatabase db = Room.databaseBuilder(requireContext(),
                    AppDatabase.class, "cornerman-db").build();

            Usuario user = db.usuarioDao().getUsuario();

            if (user != null) {
                // --- LÓGICA DE VERIFICACIÓN DE RACHA PARA API 24 (Calendar) ---

                // 1. Fecha del último entrenamiento (Normalizada a medianoche)
                Calendar calUltimo = Calendar.getInstance();
                calUltimo.setTimeInMillis(user.lastTrainingDate);
                calUltimo.set(Calendar.HOUR_OF_DAY, 0);
                calUltimo.set(Calendar.MINUTE, 0);
                calUltimo.set(Calendar.SECOND, 0);
                calUltimo.set(Calendar.MILLISECOND, 0);

                // 2. Fecha de hoy (Normalizada a medianoche)
                Calendar calHoy = Calendar.getInstance();
                calHoy.set(Calendar.HOUR_OF_DAY, 0);
                calHoy.set(Calendar.MINUTE, 0);
                calHoy.set(Calendar.SECOND, 0);
                calHoy.set(Calendar.MILLISECOND, 0);

                // 3. Cálculo de la diferencia en días naturales
                long diffMillis = calHoy.getTimeInMillis() - calUltimo.getTimeInMillis();
                long diasDiferencia = diffMillis / (24 * 60 * 60 * 1000);

                if (diasDiferencia > 1) {
                    // Ha pasado más de un día natural sin entrenar.
                    // Reset de racha a 0 hasta que complete una nueva sesión.
                    user.dailyStreak = 0;
                    db.usuarioDao().updateUsuario(user);
                }
                // --------------------------------------------------------------
            }

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    if (user != null) {
                        tvStreakCount.setText(user.dailyStreak + " Días seguidos");
                        tvTotalPoints.setText(String.format("%,d pts", user.totalPoints));

                        if (user.dailyStreak > 0) {
                            tvNextSessionText.setText("¡Mantén el ritmo, Jaime!");
                        } else {
                            // Mensaje motivador si la racha se ha perdido
                            tvNextSessionText.setText("Has perdido la racha. ¡A por ello de nuevo!");
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