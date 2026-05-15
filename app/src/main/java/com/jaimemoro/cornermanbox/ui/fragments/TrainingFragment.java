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
import android.widget.LinearLayout;
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
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.jaimemoro.cornermanbox.R;
import com.jaimemoro.cornermanbox.service.TimerService;
import com.jaimemoro.cornermanbox.viewmodel.TimerViewModel;

public class TrainingFragment extends Fragment {

    private FrameLayout timerContainer;
    private TextView tvCronometro, tvRoundCount, tvSongTitle;
    private MaterialCardView musicControlCard;
    private LinearLayout voiceIndicator;

    private TimerViewModel viewModel;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    viewModel.iniciarTimer();
                } else {
                    Toast.makeText(getContext(), "Se necesita el micrófono para el control por voz", Toast.LENGTH_LONG).show();
                    viewModel.iniciarTimer();
                }
            });

    private final BroadcastReceiver timerReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getExtras() != null) {
                String tiempo = intent.getStringExtra("tiempo");
                String info = intent.getStringExtra("infoAsalto");
                boolean esDescanso = intent.getBooleanExtra("esDescanso", false);
                boolean running = intent.getBooleanExtra("isRunning", false);
                boolean listening = intent.getBooleanExtra("isListening", false);
                boolean ready = intent.getBooleanExtra("isReady", false);

                viewModel.actualizarDesdeService(tiempo, info, esDescanso, running, listening, ready);
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
        musicControlCard = root.findViewById(R.id.music_control_card);
        tvSongTitle = root.findViewById(R.id.tv_song_title);
        voiceIndicator = root.findViewById(R.id.voice_indicator);

        viewModel = new ViewModelProvider(this).get(TimerViewModel.class);

        viewModel.inicializarSpotify();

        timerContainer.setOnClickListener(v -> {
            if (!viewModel.isHasStarted()) {
                verificarPermisoYEmpezar();
            } else {
                Boolean running = viewModel.getIsRunning().getValue();
                if (Boolean.TRUE.equals(running)) {
                    viewModel.pausarTimer();
                } else {
                    viewModel.reanudarTimer();
                }
            }
        });

        timerContainer.setOnLongClickListener(v -> {
            viewModel.resetearTimer();
            viewModel.resetearEstadoLocal();
            return true;
        });

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
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

        observarViewModel();
    }

    private void observarViewModel() {
        viewModel.getTiempoRestante().observe(getViewLifecycleOwner(), tiempo -> {
            if (tiempo != null) tvCronometro.setText(tiempo);
        });

        viewModel.getInfoAsalto().observe(getViewLifecycleOwner(), info -> {
            if (info != null) tvRoundCount.setText(info);
        });

        viewModel.getIsListening().observe(getViewLifecycleOwner(), listening -> {
            voiceIndicator.setVisibility(Boolean.TRUE.equals(listening) ? View.VISIBLE : View.INVISIBLE);
        });

        viewModel.getIsRunning().observe(getViewLifecycleOwner(), running -> {
            Boolean ready = viewModel.getIsReady().getValue();
            Boolean descanso = viewModel.getEsDescanso().getValue();

            if (!Boolean.TRUE.equals(running) && !Boolean.TRUE.equals(ready)) {
                tvCronometro.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
            } else {
                int color = Boolean.TRUE.equals(descanso) ? R.color.red_boxing : R.color.green_boxing;
                tvCronometro.setTextColor(ContextCompat.getColor(requireContext(), color));
            }
        });

        viewModel.getSpotifyConectado().observe(getViewLifecycleOwner(), conectado -> {
            if (Boolean.TRUE.equals(conectado)) {
                musicControlCard.setVisibility(View.VISIBLE);
                tvSongTitle.setText("Spotify: Conectado");
            } else {
                musicControlCard.setVisibility(View.GONE);
            }
        });

        viewModel.getCancionActual().observe(getViewLifecycleOwner(), cancion -> {
            if (cancion != null && !cancion.isEmpty()) {
                tvSongTitle.setText(cancion);
            }
        });
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

        viewModel.getStatus();
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
        viewModel.desconectarSpotify();
    }

    private void mostrarDialogoFinalizar() {
        viewModel.pausarTimer();

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("¿Finalizar entrenamiento?")
                .setMessage("Se guardará tu progreso actual y volverás al inicio.")
                .setCancelable(false)
                .setPositiveButton("SÍ", (dialog, which) -> {
                    enviarAccionServicio(TimerService.ACTION_STOP);
                    Navigation.findNavController(requireView())
                            .navigate(R.id.action_training_to_dashboard);
                })
                .setNegativeButton("NO", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }

    private void verificarPermisoYEmpezar() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO);
        } else {
            viewModel.iniciarTimer();
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
}