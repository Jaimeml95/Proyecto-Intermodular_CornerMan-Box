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
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.jaimemoro.cornermanbox.R;
import com.jaimemoro.cornermanbox.repository.CornerManRepository;
import com.jaimemoro.cornermanbox.utils.StatsManager;
import com.jaimemoro.cornermanbox.utils.VoiceCommandHelper;
import com.jaimemoro.cornermanbox.data.spotify.SpotifyManager;

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
    private CornerManRepository repository;
    private SpotifyManager spotifyManager;
    private boolean isListening = false; // Para el indicador de voz en el Fragment
    private final Handler mHandler = new Handler();

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

        // Inicializamos los colaboradores
        repository = new CornerManRepository(getApplication());
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
        repository.getUsuario(user -> {
            if (user != null) {
                mRoundDuration = user.roundDurationSeconds;
                mRestDuration = user.restDurationSeconds;

                // Solo sincronizamos mTimeLeft si:
                // No está corriendo.
                // Es el primer asalto y no estamos descansando.
                // El tiempo está intacto (es igual al total, o es el valor inicial 180, o está en 0).
                if (!isRunning && mCurrentRound == 1 && !mIsResting && !mFirstRoundCompleted) {
                    if (mTimeLeft == 0 || mTimeLeft == 180 || mTimeLeft == mRoundDuration) {
                        mTimeLeft = mRoundDuration;
                    }
                }
            } else {
                // Valores de emergencia por si la DB falla
                mRoundDuration = 180;
                mRestDuration = 60;
                if (!isRunning && mCurrentRound == 1) mTimeLeft = 180;
            }

            actualizarInterfaz();
            if (postConfig != null) postConfig.run();
        });
    }

    @Override
    public void onCommandDetected(String comando) {
        // Forzamos a que todo lo que altere el entrenamiento ocurra en el hilo principal para optimizar los tiempos.
        mHandler.post(() -> {
            Log.d("VOSK_SERVICE", "Comando procesado en Main Thread: " + comando);

            if (comando.contains("pausa") || comando.contains("tiempo") || comando.contains("stop")) {
                pausarCronometro();
            } else if (comando.contains("empezar") || comando.contains("continuar")) {
                reanudarCronometro();
            }
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
        });
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
            startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE);
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
                StatsManager.registrarEntrenamientoCompletado(getApplicationContext());
                // Importante: una vez sumados los puntos, reseteamos el flag
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