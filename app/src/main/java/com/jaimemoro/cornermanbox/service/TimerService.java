package com.jaimemoro.cornermanbox.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.room.Room;

import java.util.ArrayList;

import com.jaimemoro.cornermanbox.R;
import com.jaimemoro.cornermanbox.data.local.AppDatabase;
import com.jaimemoro.cornermanbox.data.entities.Usuario;
import com.jaimemoro.cornermanbox.utils.StatsManager;

public class TimerService extends Service {

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

    // Variables de Voz
    private SpeechRecognizer speechRecognizer;
    private Intent speechRecognizerIntent;
    private boolean isListening = false;

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
                    actualizarInterfaz();
                    break;
                case ACTION_RESET:
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
            new Thread(() -> {
                AppDatabase db = AppDatabase.getInstance(getApplicationContext());
                Usuario user = db.usuarioDao().getUsuario();

                if (user != null) {
                    mRoundDuration = user.roundDurationSeconds;
                    mRestDuration = user.restDurationSeconds;
                }
                db.close();

                mHandler.post(() -> {
                    mCurrentRound = 1;
                    mIsResting = false;
                    mTimeLeft = mRoundDuration;
                    mFirstRoundCompleted = false;
                    isRunning = true;

                    reproducirCampana();
                    iniciarReconocimientoVoz(); // Activamos el oído al empezar
                    mHandler.postDelayed(timerRunnable, 0);
                });
            }).start();
        }
    }

    // --- LÓGICA DE CONTROL POR VOZ ---

    private void iniciarReconocimientoVoz() {
        if (speechRecognizer != null) return;

        mHandler.post(() -> {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
            speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-ES");
            speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, false);

            speechRecognizer.setRecognitionListener(new RecognitionListener() {
                @Override public void onReadyForSpeech(Bundle params) { Log.d("VOICE", "Listo para oír"); }
                @Override public void onBeginningOfSpeech() {}
                @Override public void onRmsChanged(float rmsdB) {}
                @Override public void onBufferReceived(byte[] buffer) {}
                @Override public void onEndOfSpeech() {}

                @Override
                public void onError(int error) {
                    if (isListening) reiniciarEscucha();
                }

                @Override
                public void onResults(Bundle results) {
                    ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                    if (matches != null && !matches.isEmpty()) {
                        procesarComandoVoz(matches.get(0).toLowerCase());
                    }
                    if (isListening) reiniciarEscucha();
                }

                @Override public void onPartialResults(Bundle partialResults) {}
                @Override public void onEvent(int eventType, Bundle params) {}
            });

            isListening = true;
            speechRecognizer.startListening(speechRecognizerIntent);
        });
    }

    private void reiniciarEscucha() {
        if (speechRecognizer != null && isListening) {
            speechRecognizer.cancel();
            speechRecognizer.startListening(speechRecognizerIntent);
        }
    }

    private void procesarComandoVoz(String comando) {
        Log.d("VOICE", "Comando detectado: " + comando);
        if (comando.contains("box")) {
            if (!isRunning) {
                reanudarCronometro();
                actualizarInterfaz();
            }
        } else if (comando.contains("tiempo")) {
            if (isRunning) {
                pausarCronometro();
                actualizarInterfaz();
            }
        }
    }

    // --- FIN LÓGICA DE VOZ ---

    private void pausarCronometro() {
        isRunning = false;
        mHandler.removeCallbacks(timerRunnable);
        actualizarInterfaz();
    }

    private void reanudarCronometro() {
        if (!isRunning) {
            isRunning = true;
            mHandler.postDelayed(timerRunnable, 0);
        }
    }

    private void finalizarEntrenamiento() {
        isRunning = false;
        isListening = false;
        mHandler.removeCallbacks(timerRunnable);

        if (speechRecognizer != null) {
            speechRecognizer.stopListening();
            speechRecognizer.cancel();
        }

        if (mFirstRoundCompleted) {
            new Thread(() -> {
                // Toda la magia ocurre aquí dentro de forma aislada
                StatsManager.registrarEntrenamientoCompletado(getApplicationContext());

                // Una vez terminado el trabajo sucio, cerramos el servicio
                mHandler.post(() -> {
                    mFirstRoundCompleted = false;
                    stopForeground(true);
                    stopSelf();
                });
            }).start();
        } else {
            mFirstRoundCompleted = false;
            stopForeground(true);
            stopSelf();
        }
    }

    private void cambiarDeFase() {
        reproducirCampana();
        if (!mIsResting) {
            mIsResting = true;
            mTimeLeft = mRestDuration;
            mFirstRoundCompleted = true;
        } else {
            mIsResting = false;
            mCurrentRound++;
            if (mCurrentRound > mTotalRounds) {
                finalizarEntrenamiento();
                return;
            }
            mTimeLeft = mRoundDuration;
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

    @Override public IBinder onBind(Intent intent) { return null; }

    @Override
    public void onDestroy() {
        isRunning = false;
        isListening = false;
        if (mpCampana != null) {
            mpCampana.release();
            mpCampana = null;
        }
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
            speechRecognizer = null;
        }
        super.onDestroy();
    }
}