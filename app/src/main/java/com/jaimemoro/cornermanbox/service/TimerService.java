package com.jaimemoro.cornermanbox.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.jaimemoro.cornermanbox.R;
import com.jaimemoro.cornermanbox.core.application.usecases.GetUsuarioUseCase;
import com.jaimemoro.cornermanbox.core.application.usecases.RegistrarEntrenamientoUseCase;
import com.jaimemoro.cornermanbox.core.domain.model.Usuario;
import com.jaimemoro.cornermanbox.core.domain.repository.IUsuarioRepository;
import com.jaimemoro.cornermanbox.core.domain.repository.RepositoryCallback;
import com.jaimemoro.cornermanbox.infrastructure.external.voice.VoiceCommandHelper;
import com.jaimemoro.cornermanbox.infrastructure.external.spotify.SpotifyManager;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class TimerService extends Service implements VoiceCommandHelper.VoiceCommandListener {

    public static final String ACTION_START = "START";
    public static final String ACTION_PAUSE = "PAUSE";
    public static final String ACTION_RESUME = "RESUME";
    public static final String ACTION_RESET = "RESET";
    public static final String ACTION_STOP = "STOP";
    public static final String ACTION_GET_STATUS = "GET_STATUS";
    public static final String TIMER_UPDATE = "com.jaimemoro.cornermanbox.TIMER_UPDATE";
    private static final String CHANNEL_ID = "TimerServiceChannel";

    private static final int NOTIFICATION_ID = 1;
    private boolean isRunning = false;
    private int mCurrentRound = 1;
    private int mTotalRounds = 12;
    private boolean mIsResting = false;
    private int mRoundDuration = 180;
    private int mRestDuration = 60;
    private int mTimeLeft = mRoundDuration;
    private boolean mFirstRoundCompleted = false;
    private MediaPlayer mpCampana;
    private VoiceCommandHelper voiceHelper;
    private SpotifyManager spotifyManager;

    @Inject
    public GetUsuarioUseCase getUsuarioUseCase;

    @Inject
    public RegistrarEntrenamientoUseCase registrarEntrenamientoUseCase;
    private boolean isListening = false; // Para el indicador de voz en el Fragment
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    private final Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            if (isRunning) {
                if (mTimeLeft > 0) {
                    mTimeLeft--;
                } else {
                    cambiarDeFase();
                }
                actualizarInterfaz();
                mHandler.postDelayed(this, 1000);
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        mpCampana = MediaPlayer.create(this, R.raw.campana);

        // Al crear el helper aquí, Vosk empieza a cargar el modelo inmediatamente
        voiceHelper = new VoiceCommandHelper(this, this);
        spotifyManager = new SpotifyManager(this);
        spotifyManager.conectar(null);
        cargarConfiguracionBase(null);
    }

    /**
     * Carga los tiempos de la DB y refresca la interfaz sin empezar el crono.
     */
    private void cargarConfiguracionBase(Runnable postConfig) {
        getUsuarioUseCase.ejecutar(new RepositoryCallback<Usuario>() {
            @Override
            public void onSuccess(Usuario user) {
                if (user != null) {
                    mRoundDuration = user.getRoundDurationSeconds();
                    mRestDuration = user.getRestDurationSeconds();

                    if (!isRunning && mCurrentRound == 1 && !mIsResting && !mFirstRoundCompleted) {
                        if (mTimeLeft == 0 || mTimeLeft == 180 || mTimeLeft == mRoundDuration) {
                            mTimeLeft = mRoundDuration;
                        }
                    }
                } else {
                    mRoundDuration = 180;
                    mRestDuration = 60;
                    if (!isRunning && mCurrentRound == 1) mTimeLeft = 180;
                }

                actualizarInterfaz();
                if (postConfig != null) postConfig.run();
            }

            @Override
            public void onError(Exception e) {
                mRoundDuration = 180;
                mRestDuration = 60;
                if (!isRunning && mCurrentRound == 1) mTimeLeft = 180;
                actualizarInterfaz();
                if (postConfig != null) postConfig.run();
            }
        });
    }

    // Callback que viene del Helper
    @Override
    public void onCommandDetected(String comando) {
        Log.d("VOSK_SERVICE", "Comando recibido: " + comando);
        // Como Vosk es muy preciso, podemos buscar palabras clave exactas
        if (comando.contains("pausa") || comando.contains("tiempo") || comando.contains("stop")) {
            pausarCronometro();
        } else if (comando.contains("empezar") || comando.contains("continuar")) {
            reanudarCronometro();
        }
        // Comandos de Música
        else if (comando.contains("siguiente") || comando.contains("pasar") || comando.contains("próxima")) {
            saltarCancionSpotify();
        }
        else if (comando.contains("anterior") || comando.contains("atrás") || comando.contains("regresar")) {
            spotifyManager.retrocederCancionInteligente();
            Log.d("VOSK_MUSIC", "Ejecutando retroceso inteligente");
        }
        else if (comando.contains("silencio") || comando.contains("para")) {
            if (spotifyManager != null) {
                spotifyManager.pausarMusica();
                Log.d("VOSK_MUSIC", "Orden recibida: Silencio");
            }
        }
        else if (comando.contains("musica") || comando.contains("música")) {
            if (spotifyManager != null) {
                spotifyManager.reanudarMusica();
                Log.d("VOSK_MUSIC", "Orden recibida: Música");
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            String action = intent.getAction();
            switch (action) {
                case ACTION_START:
                    iniciarCronometro();
                    break;
                case ACTION_PAUSE:
                    pausarCronometro();
                    break;
                case ACTION_RESUME:
                    reanudarCronometro();
                    break;
                case ACTION_GET_STATUS:
                    // Aseguramos que al pedir estatus, refresque de la DB
                    cargarConfiguracionBase(null);
                    break;
                case ACTION_RESET:
                    // El Reset ya no mata el servicio, solo lo reinicia visualmente
                    resetearCronometro();
                    break;
                case ACTION_STOP:
                    finalizarEntrenamiento();
                    return START_NOT_STICKY;
            }
        }

        mostrarNotificacion();
        return START_NOT_STICKY;
    }

    private void mostrarNotificacion() {
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("CornerMan Box")
                .setContentText("Entrenamiento en curso")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(true)
                .build();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE);
        } else {
            startForeground(NOTIFICATION_ID, notification);
        }
    }

    private void iniciarCronometro() {
        if (!isRunning) {
            // Recargamos por si cambió algo en Settings justo antes de dar Start
            cargarConfiguracionBase(() -> {
                mHandler.post(() -> {
                    isRunning = true;
                    reproducirCampana();
                    voiceHelper.startListening();
                    isListening = true;
                    mHandler.postDelayed(timerRunnable, 0);
                });
            });
        }
    }

    private void resetearCronometro() {
        // Sumar puntos si ha completado al menos un asalto
        registrarProgreso();
        isRunning = false;
        mHandler.removeCallbacks(timerRunnable);
        mCurrentRound = 1;
        mIsResting = false;
        mFirstRoundCompleted = false;

        // Apagamos el micrófono al reiniciar
        if (voiceHelper != null) {
            voiceHelper.stopListening();
            isListening = false; // Esto hará que el icono del micro desaparezca en el Fragment
        }

        // Forzamos el valor a 0 para que cargarConfiguracionBase sincronice con la DB
        mTimeLeft = 0;

        cargarConfiguracionBase(null);
    }

    private void pausarCronometro() {
        isRunning = false;
        mHandler.removeCallbacks(timerRunnable);
        actualizarInterfaz();
    }

    private void reanudarCronometro() {
        if (!isRunning) {
            isRunning = true;
            mHandler.postDelayed(timerRunnable, 0);
            actualizarInterfaz();
        }
    }

    private void registrarProgreso() {
        if (mFirstRoundCompleted) {
            new Thread(() -> {
                Log.d("TIMER", "Registrando entrenamiento completado...");
                RegistrarEntrenamientoUseCase.ParametrosEntrenamiento params =
                    new RegistrarEntrenamientoUseCase.ParametrosEntrenamiento(1, mRoundDuration);
                registrarEntrenamientoUseCase.ejecutar(params, new RegistrarEntrenamientoUseCase.Callback() {
                    @Override
                    public void onSuccess() {
                        Log.d("TIMER", "Entrenamiento registrado correctamente");
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e("TIMER", "Error al registrar entrenamiento", e);
                    }
                });
                mFirstRoundCompleted = false;
            }).start();
        }
    }

    private void finalizarEntrenamiento() {
        isRunning = false;
        mHandler.removeCallbacks(timerRunnable);

        if (voiceHelper != null) {
            voiceHelper.stopListening();
            isListening = false;
        }

        // Sumar puntos
        registrarProgreso();

        // Cerrar el servicio definitivamente
        actualizarInterfaz();
        stopForeground(true);
        stopSelf();
    }

    private void cambiarDeFase() {
        reproducirCampana();

        if (!mIsResting) {
            // --- ENTRANDO EN DESCANSO ---
            mIsResting = true;
            mTimeLeft = mRestDuration;
            mFirstRoundCompleted = true;
            mCurrentRound++;

            // Bajamos el volumen para dar sensación de descanso.
            if (spotifyManager != null) {
                spotifyManager.ajustarVolumen(0.5f);
            }

        } else {
            // --- EMPEZANDO NUEVO ASALTO ---
            mIsResting = false;

            if (mCurrentRound > mTotalRounds) {
                finalizarEntrenamiento();
                return;
            }

            mTimeLeft = mRoundDuration;

            // Subimos el volumen de nuevo
            if (spotifyManager != null) {
                spotifyManager.ajustarVolumen(1.0f);
            }
        }
    }

    private void reproducirCampana() {
        if (mpCampana != null) {
            if (mpCampana.isPlaying()) {
                mpCampana.pause();
                mpCampana.seekTo(0);
            }
            mpCampana.start();
        }
    }

    private void saltarCancionSpotify() {
        if (spotifyManager != null) {
            spotifyManager.saltarSiguiente();
            Log.d("VOSK_MUSIC", "Saltando a la siguiente canción");
        }
    }

    private void retrocederCancionSpotify() {
        if (spotifyManager != null) {
            spotifyManager.saltarAnterior();
            Log.d("VOSK_MUSIC", "Volviendo a la canción anterior");
        }
    }

    private void actualizarInterfaz() {
        int minutes = mTimeLeft / 60;
        int seconds = mTimeLeft % 60;
        String tiempoFormateado = String.format("%02d:%02d", minutes, seconds);
        String infoAsalto = mIsResting ? "DESCANSO" : "ASALTO " + mCurrentRound;
        enviarDatos(tiempoFormateado, infoAsalto, mIsResting);
    }

    private void enviarDatos(String tiempo, String infoAsalto, boolean esDescanso) {
        Intent intent = new Intent(TIMER_UPDATE);
        intent.putExtra("tiempo", tiempo);
        intent.putExtra("infoAsalto", infoAsalto);
        intent.putExtra("esDescanso", esDescanso);
        intent.putExtra("isRunning", isRunning);
        intent.putExtra("isListening", isListening);

        // Solo estamos "Ready" (color neón) si:
        // No corre Y es el asalto 1 Y NO se ha consumido ni un segundo (mTimeLeft == mRoundDuration)
        boolean isReady = !isRunning && mCurrentRound == 1 && !mIsResting && !mFirstRoundCompleted && mTimeLeft == mRoundDuration;

        intent.putExtra("isReady", isReady);

        sendBroadcast(intent);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID, "Canal de Temporizador", NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) manager.createNotificationChannel(serviceChannel);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        isRunning = false;

        // Desconexión de Spotify
        if (spotifyManager != null) {
            spotifyManager.desconectar();
        }

        if (mpCampana != null) {
            mpCampana.release();
            mpCampana = null;
        }

        if (voiceHelper != null) {
            voiceHelper.destroy();
        }

        super.onDestroy();
    }
}