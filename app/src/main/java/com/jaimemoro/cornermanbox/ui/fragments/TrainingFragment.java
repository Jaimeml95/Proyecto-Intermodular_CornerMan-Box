package com.jaimemoro.cornermanbox.ui.fragments;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.navigation.Navigation;

import com.google.android.material.card.MaterialCardView; // Nuevo
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.jaimemoro.cornermanbox.R;
import com.jaimemoro.cornermanbox.infrastructure.external.spotify.SpotifyManager;
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

        // Inicializar vistas
        timerContainer = root.findViewById(R.id.timer_container);
        tvCronometro = root.findViewById(R.id.tv_timer_display);
        tvRoundCount = root.findViewById(R.id.tv_round_count);
        musicControlCard = root.findViewById(R.id.music_control_card);
        tvSongTitle = root.findViewById(R.id.tv_song_title);
        voiceIndicator = root.findViewById(R.id.voice_indicator);

        // Configurar Spotify
        setupSpotify();

        // Listener de Clicks
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // IMPLEMENTACIÓN MODERNA DEL MENÚ
        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                // Inflamos el XML que creaste
                menuInflater.inflate(R.menu.training_menu, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.action_finalizar) {
                    mostrarDialogoFinalizar();
                    return true;
                }
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }

    private void mostrarDialogoFinalizar() {
        // Pausamos el crono inmediatamente para que no pierda tiempo mientras decide
        enviarAccionServicio(TimerService.ACTION_PAUSE);

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("¿Finalizar entrenamiento?")
                .setMessage("Se guardará tu progreso actual y volverás al inicio.")
                .setCancelable(false) // No permite cerrar tocando fuera
                .setPositiveButton("SÍ", (dialog, which) -> {
                    // Detenemos el servicio y sumamos puntos
                    enviarAccionServicio(TimerService.ACTION_STOP);

                    // Volvemos al Dashboard
                    Navigation.findNavController(requireView())
                            .navigate(R.id.action_training_to_dashboard);
                })
                .setNegativeButton("NO", (dialog, which) -> {
                    // Cerramos el diálogo y el crono se queda pausado (blanco)
                    dialog.dismiss();
                })
                .show();
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