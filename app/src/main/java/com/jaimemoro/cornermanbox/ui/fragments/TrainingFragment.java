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
import android.widget.LinearLayout; // Nuevo
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.card.MaterialCardView; // Nuevo
import com.jaimemoro.cornermanbox.R;
import com.jaimemoro.cornermanbox.data.spotify.SpotifyManager; // Tu nueva clase
import com.jaimemoro.cornermanbox.service.TimerService;

public class TrainingFragment extends Fragment {

    private FrameLayout timerContainer;
    private TextView tvCronometro, tvRoundCount, tvSongTitle;
    private MaterialCardView musicControlCard;
    private LinearLayout voiceIndicator;
    private SpotifyManager spotifyManager;

    private boolean isRunning = false;
    private boolean hasStarted = false;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    enviarAccionServicio(TimerService.ACTION_START);
                } else {
                    Toast.makeText(getContext(), "Se necesita el micrófono para el control por voz", Toast.LENGTH_LONG).show();
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
                boolean isListening = intent.getBooleanExtra("isListening", false);
                boolean isReady = intent.getBooleanExtra("isReady", false);

                tvCronometro.setText(tiempo);
                tvRoundCount.setText(info);
                voiceIndicator.setVisibility(isListening ? View.VISIBLE : View.INVISIBLE);

                // Lógica de "hasStarted" (Control de botones Play/Pause)
                // Solo es "false" si no está corriendo Y está en el estado inicial (isReady)
                // Si has pausado a mitad (!isRunning y !isReady), hasStarted sigue siendo true
                hasStarted = isRunning || !isReady;

                // Lógica de Colores
                if (!isRunning && !isReady) {
                    // ESTADO: Pausado a mitad de un round -> Blanco
                    tvCronometro.setTextColor(ContextCompat.getColor(context, R.color.white));
                } else {
                    // ESTADO: Corriendo o Listo para empezar -> Color Neón (Verde/Rojo)
                    int color = esDescanso ? R.color.red_boxing : R.color.green_boxing;
                    tvCronometro.setTextColor(ContextCompat.getColor(context, color));
                }
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_training, container, false);

        // Inicializar vistas existentes
        timerContainer = root.findViewById(R.id.timer_container);
        tvCronometro = root.findViewById(R.id.tv_timer_display);
        tvRoundCount = root.findViewById(R.id.tv_round_count);

        // Inicializar nuevas vistas
        musicControlCard = root.findViewById(R.id.music_control_card);
        tvSongTitle = root.findViewById(R.id.tv_song_title);
        voiceIndicator = root.findViewById(R.id.voice_indicator);

        // Configurar Spotify
        setupSpotify();

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
            tvCronometro.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
            tvRoundCount.setText("ASALTO 1 / 12");
            return true;
        });

        return root;
    }

    private void setupSpotify() {
        spotifyManager = new SpotifyManager(requireActivity());
        spotifyManager.conectar(new SpotifyManager.SpotifyConnectionListener() {
            @Override
            public void onConnected() {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        // Hacemos visible la tarjeta
                        musicControlCard.setVisibility(View.VISIBLE);
                        tvSongTitle.setText("Spotify: Conectado");

                        // Nos suscribimos a los cambios de canción usando el Manager
                        spotifyManager.suscribirseACancion((titulo, artista) -> {
                            // Spotify avisa en un hilo secundario, volvemos al principal para la UI
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(() -> {
                                    tvSongTitle.setText(titulo + " - " + artista);
                                });
                            }
                        });
                    });
                }
            }

            @Override
            public void onFailure(Throwable error) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        musicControlCard.setVisibility(View.GONE);
                    });
                }
            }
        });
    }

    private void verificarPermisoYEmpezar() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
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
        ContextCompat.registerReceiver(
                requireContext(),
                timerReceiver,
                new IntentFilter(TimerService.TIMER_UPDATE),
                ContextCompat.RECEIVER_NOT_EXPORTED
        );
        enviarAccionServicio(TimerService.ACTION_GET_STATUS);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getContext() != null) {
            requireContext().unregisterReceiver(timerReceiver);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (spotifyManager != null) {
            spotifyManager.desconectar();
        }
    }
}