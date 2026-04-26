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
import com.jaimemoro.cornermanbox.R;

public class TimerService extends Service {

    // Acciones para controlar el servicio mediante Intents
    public static final String ACTION_START = "START";
    public static final String ACTION_STOP = "STOP";

    // Identificador para el receptor de actualizaciones (Broadcast)
    public static final String TIMER_UPDATE = "com.jaimemoro.cornermanbox.TIMER_UPDATE";

    private static final String CHANNEL_ID = "TimerServiceChannel";
    private boolean isRunning = false;
    private long mStartTime = 0L;

    // Handler para ejecutar el conteo cada segundo
    private final Handler mHandler = new Handler();

    private final Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            if (isRunning) {
                // Cálculo del tiempo transcurrido en milisegundos
                long millis = System.currentTimeMillis() - mStartTime;
                int seconds = (int) (millis / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;

                // Formateo del tiempo (MM:SS) y envío a la interfaz
                enviarTiempo(String.format("%02d:%02d", minutes, seconds));

                // Programar la siguiente ejecución en 1000ms
                mHandler.postDelayed(this, 1000);
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            String action = intent.getAction();
            if (ACTION_START.equals(action)) {
                iniciarCronometro();
            } else if (ACTION_STOP.equals(action)) {
                detenerCronometro();
            }
        }

        // Creación de la notificación persistente necesaria para Foreground Service
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("CornerMan Box")
                .setContentText("Entrenamiento en curso")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(true)
                .build();

        // Inicia el servicio en primer plano para evitar que Android lo cierre
        startForeground(1, notification);

        return START_NOT_STICKY;
    }

    private void iniciarCronometro() {
        if (!isRunning) {
            mStartTime = System.currentTimeMillis();
            isRunning = true;
            mHandler.postDelayed(timerRunnable, 0);
        }
    }

    private void detenerCronometro() {
        isRunning = false;
        mHandler.removeCallbacks(timerRunnable);
        stopForeground(true);
        stopSelf();
    }

    private void enviarTiempo(String tiempo) {
        // Envía el tiempo actual a cualquier Activity o Fragment suscrito
        Intent intent = new Intent(TIMER_UPDATE);
        intent.putExtra("tiempo", tiempo);
        sendBroadcast(intent);
    }

    private void createNotificationChannel() {
        // Canal de notificación requerido a partir de Android 8.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Canal de Temporizador",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(serviceChannel);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        detenerCronometro();
        super.onDestroy();
    }
}