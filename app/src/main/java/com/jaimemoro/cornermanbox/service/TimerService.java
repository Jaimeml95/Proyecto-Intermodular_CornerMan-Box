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
import androidx.core.app.NotificationCompat;
import androidx.room.Room;
import java.util.Calendar;

import com.jaimemoro.cornermanbox.R;
import com.jaimemoro.cornermanbox.data.local.AppDatabase;
import com.jaimemoro.cornermanbox.data.entities.Usuario;

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

    private MediaPlayer mpCampana; // Variable para el sonido
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

    private void actualizarInterfaz() {
        int minutes = mTimeLeft / 60;
        int seconds = mTimeLeft % 60;
        String tiempoFormateado = String.format("%02d:%02d", minutes, seconds);
        String infoAsalto = mIsResting ? "DESCANSO" : "ASALTO " + mCurrentRound;
        enviarDatos(tiempoFormateado, infoAsalto, mIsResting);
    }

    private void cambiarDeFase() {
        reproducirCampana(); // Suena al terminar asalto o descanso

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

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            String action = intent.getAction();
            switch (action) {
                case ACTION_START: iniciarCronometro(); break;
                case ACTION_PAUSE: pausarCronometro(); break;
                case ACTION_RESUME: reanudarCronometro(); break;
                case ACTION_GET_STATUS: actualizarInterfaz(); break;
                case ACTION_RESET:
                case ACTION_STOP:
                    finalizarEntrenamiento();
                    return START_NOT_STICKY;
            }
        }

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
        return START_NOT_STICKY;
    }

    private void iniciarCronometro() {
        if (!isRunning) {
            // Abrimos un hilo para leer la configuración de la DB
            new Thread(() -> {
                AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "cornerman-db").build();
                Usuario user = db.usuarioDao().getUsuario();

                if (user != null) {
                    // Actualizamos las duraciones con lo que el usuario configuró
                    mRoundDuration = user.roundDurationSeconds;
                    mRestDuration = user.restDurationSeconds;
                }
                db.close();

                // Una vez tenemos los datos, volvemos al hilo del Handler para empezar
                mHandler.post(() -> {
                    mCurrentRound = 1;
                    mIsResting = false;
                    mTimeLeft = mRoundDuration; // Empezamos con el tiempo personalizado
                    mFirstRoundCompleted = false;
                    isRunning = true;

                    reproducirCampana(); // Campanazo de inicio

                    mHandler.postDelayed(timerRunnable, 0);
                });
            }).start();
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
        mHandler.removeCallbacks(timerRunnable);

        if (mFirstRoundCompleted) {
            new Thread(() -> {
                AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "cornerman-db").build();
                Usuario user = db.usuarioDao().getUsuario();

                if (user != null) {
                    user.totalPoints += 100;
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

                    if (diasDiferencia == 1) {
                        user.dailyStreak++;
                    } else if (diasDiferencia > 1) {
                        user.dailyStreak = 1;
                    }

                    user.lastTrainingDate = System.currentTimeMillis();
                    db.usuarioDao().updateUsuario(user);
                } else {
                    Usuario newUser = new Usuario();
                    newUser.totalPoints = 100;
                    newUser.dailyStreak = 1;
                    newUser.lastTrainingDate = System.currentTimeMillis();
                    db.usuarioDao().insertUsuario(newUser);
                }
                db.close();
            }).start();
        }

        mFirstRoundCompleted = false;
        stopForeground(true);
        stopSelf();
    }

    private void enviarDatos(String tiempo, String infoAsalto, boolean esDescanso) {
        Intent intent = new Intent(TIMER_UPDATE);
        intent.putExtra("tiempo", tiempo);
        intent.putExtra("infoAsalto", infoAsalto);
        intent.putExtra("esDescanso", esDescanso);
        intent.putExtra("isRunning", isRunning);
        sendBroadcast(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        // Inicializamos el MediaPlayer con el archivo campana.mp3 de res/raw
        mpCampana = MediaPlayer.create(this, R.raw.campana);
    }

    @Override public IBinder onBind(Intent intent) { return null; }

    @Override
    public void onDestroy() {
        isRunning = false;
        // Liberar el MediaPlayer para evitar fugas de memoria
        if (mpCampana != null) {
            mpCampana.release();
            mpCampana = null;
        }
        super.onDestroy();
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
}