package com.jaimemoro.cornermanbox.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;
import androidx.room.Room;

import com.jaimemoro.cornermanbox.R;
import com.jaimemoro.cornermanbox.data.local.AppDatabase;
import com.jaimemoro.cornermanbox.data.entities.Usuario;

/**
 * Servicio en primer plano (Foreground Service) encargado de gestionar el motor de tiempos.
 * Implementa la lógica de asaltos/descansos y la persistencia de datos mediante Room.
 */
public class TimerService extends Service {

    public static final String ACTION_START = "START";
    public static final String ACTION_STOP = "STOP";
    public static final String TIMER_UPDATE = "com.jaimemoro.cornermanbox.TIMER_UPDATE";

    private static final String CHANNEL_ID = "TimerServiceChannel";
    private boolean isRunning = false;

    // Configuración del entrenamiento
    private int mCurrentRound = 1;
    private int mTotalRounds = 12;
    private boolean mIsResting = false;

    // Tiempos (segundos) - Configuración estándar de boxeo
    private int mTimeLeft;
    private final int ROUND_DURATION = 180; // 3 minutos
    private final int REST_DURATION = 60;   // 1 minuto

    private final Handler mHandler = new Handler();

    /**
     * Hilo de ejecución recurrente para el cronometrado.
     * Actualiza el estado del entrenamiento cada segundo.
     */
    private final Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            if (isRunning) {
                if (mTimeLeft > 0) {
                    mTimeLeft--;
                } else {
                    cambiarDeFase();
                }

                // Emisión de datos a la interfaz de usuario mediante Broadcast
                int minutes = mTimeLeft / 60;
                int seconds = mTimeLeft % 60;
                String tiempoFormateado = String.format("%02d:%02d", minutes, seconds);
                String infoAsalto = mIsResting ? "DESCANSO" : "ASALTO " + mCurrentRound;

                enviarDatos(tiempoFormateado, infoAsalto, mIsResting);
                mHandler.postDelayed(this, 1000);
            }
        }
    };

    private void cambiarDeFase() {
        if (!mIsResting) {
            mIsResting = true;
            mTimeLeft = REST_DURATION;
        } else {
            mIsResting = false;
            mCurrentRound++;

            if (mCurrentRound > mTotalRounds) {
                finalizarEntrenamiento(); // Cierre automático al completar la sesión
                return;
            }
            mTimeLeft = ROUND_DURATION;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            String action = intent.getAction();
            if (ACTION_START.equals(action)) {
                iniciarCronometro();
            } else if (ACTION_STOP.equals(action)) {
                finalizarEntrenamiento(); // Cierre manual por el usuario
            }
        }

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("CornerMan Box")
                .setContentText("Entrenamiento en curso")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(true)
                .build();

        startForeground(1, notification);
        return START_NOT_STICKY;
    }

    private void iniciarCronometro() {
        if (!isRunning) {
            mCurrentRound = 1;
            mIsResting = false;
            mTimeLeft = ROUND_DURATION;
            isRunning = true;
            mHandler.postDelayed(timerRunnable, 0);
        }
    }

    /**
     * Gestiona el cierre del servicio y la persistencia de estadísticas.
     * Realiza operaciones de escritura en base de datos en un hilo secundario para evitar bloqueos en el UI Thread.
     */
    private void finalizarEntrenamiento() {
        isRunning = false;
        mHandler.removeCallbacks(timerRunnable);

        new Thread(() -> {
            AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                    AppDatabase.class, "cornerman-db").build();

            Usuario user = db.usuarioDao().getUsuario();
            if (user != null) {
                // Incremento de puntos y lógica de gamificación
                user.totalPoints += 100;

                // Lógica de cálculo de rachas diarias
                long hoy = System.currentTimeMillis();
                long diferencia = hoy - user.lastTrainingDate;

                if (diferencia > 86400000 && diferencia < 172800000) {
                    user.dailyStreak++; // Racha mantenida (entrenó ayer)
                } else if (diferencia > 172800000) {
                    user.dailyStreak = 1; // Racha perdida (más de 48h sin actividad)
                }

                user.lastTrainingDate = hoy;
                db.usuarioDao().updateUsuario(user);
            }
        }).start();

        stopForeground(true);
        stopSelf();
    }

    private void enviarDatos(String tiempo, String infoAsalto, boolean esDescanso) {
        Intent intent = new Intent(TIMER_UPDATE);
        intent.putExtra("tiempo", tiempo);
        intent.putExtra("infoAsalto", infoAsalto);
        intent.putExtra("esDescanso", esDescanso);
        sendBroadcast(intent);
    }

    @Override public void onCreate() { super.onCreate(); createNotificationChannel(); }
    @Override public IBinder onBind(Intent intent) { return null; }
    @Override public void onDestroy() { isRunning = false; super.onDestroy(); }

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