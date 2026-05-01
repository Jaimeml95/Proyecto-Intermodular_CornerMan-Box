package com.jaimemoro.cornermanbox.ui.fragments;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.jaimemoro.cornermanbox.R;
import com.jaimemoro.cornermanbox.service.TimerService;

public class TrainingFragment extends Fragment {

    private FrameLayout timerContainer;
    private TextView tvCronometro, tvRoundCount;
    private boolean isRunning = false;
    private boolean hasStarted = false;

    // Nueva forma de registrar la petición de permisos
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // Permiso concedido, arrancamos
                    enviarAccionServicio(TimerService.ACTION_START);
                } else {
                    // Permiso denegado, avisamos al usuario
                    Toast.makeText(getContext(), "Se necesita el micrófono para el control por voz", Toast.LENGTH_LONG).show();
                    // Arrancamos de todos modos, aunque el control por voz no funcionará
                    enviarAccionServicio(TimerService.ACTION_START);
                }
            });

    private final BroadcastReceiver timerReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getExtras() != null) {
                String tiempo = intent.getStringExtra("tiempo");
                String info = intent.getStringExtra("infoAsalto");
                boolean esDescanso = intent.getBooleanExtra("esDescanso", false);
                isRunning = intent.getBooleanExtra("isRunning", false);

                if (isRunning || (!tiempo.equals("03:00") && !tiempo.equals("01:00"))) {
                    hasStarted = true;
                } else {
                    hasStarted = false;
                }

                tvCronometro.setText(tiempo);
                tvRoundCount.setText(info);

                if (!isRunning && !tiempo.equals("03:00") && !tiempo.equals("01:00")) {
                    tvCronometro.setTextColor(ContextCompat.getColor(context, R.color.white));
                } else {
                    if (esDescanso) {
                        tvCronometro.setTextColor(ContextCompat.getColor(context, R.color.red_boxing));
                    } else {
                        tvCronometro.setTextColor(ContextCompat.getColor(context, R.color.green_boxing));
                    }
                }
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_training, container, false);

        timerContainer = root.findViewById(R.id.timer_container);
        tvCronometro = root.findViewById(R.id.tv_timer_display);
        tvRoundCount = root.findViewById(R.id.tv_round_count);

        timerContainer.setOnClickListener(v -> {
            if (!hasStarted) {
                verificarPermisoYEmpezar();
            } else if (isRunning) {
                enviarAccionServicio(TimerService.ACTION_PAUSE);
            } else {
                enviarAccionServicio(TimerService.ACTION_RESUME);
            }
        });

        timerContainer.setOnLongClickListener(v -> {
            enviarAccionServicio(TimerService.ACTION_RESET);
            isRunning = false;
            hasStarted = false;
            tvCronometro.setText("03:00");
            tvCronometro.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
            tvRoundCount.setText("ASALTO 1 / 12");
            return true;
        });

        return root;
    }

    private void verificarPermisoYEmpezar() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            // Usamos el nuevo lanzador en lugar de ActivityCompat.requestPermissions
            requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO);
        } else {
            enviarAccionServicio(TimerService.ACTION_START);
        }
    }

    private void enviarAccionServicio(String accion) {
        Context context = getContext();
        if (context != null) {
            Intent intent = new Intent(context, TimerService.class);
            intent.setAction(accion);
            context.startService(intent);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        androidx.core.content.ContextCompat.registerReceiver(
                getContext(),
                timerReceiver,
                new IntentFilter(TimerService.TIMER_UPDATE),
                androidx.core.content.ContextCompat.RECEIVER_NOT_EXPORTED
        );
        enviarAccionServicio(TimerService.ACTION_GET_STATUS);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getContext() != null) {
            getContext().unregisterReceiver(timerReceiver);
        }
    }
}