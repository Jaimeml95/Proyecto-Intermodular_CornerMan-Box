package com.jaimemoro.cornermanbox.utils;

import android.content.Context;
import com.jaimemoro.cornermanbox.data.entities.Usuario;
import com.jaimemoro.cornermanbox.data.local.AppDatabase;
import java.util.Calendar;

public class StatsManager {

    /**
     * Procesa el fin de un entrenamiento: suma puntos y actualiza rachas.
     * Se debe llamar siempre en un hilo secundario.
     */
    public static void registrarEntrenamientoCompletado(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        Usuario user = db.usuarioDao().getUsuario();

        if (user == null) {
            crearNuevoUsuario(db);
            return;
        }

        // 1. Sumar puntos
        user.totalPoints += 100;

        // 2. Calcular racha
        actualizarRacha(user);

        // 3. Actualizar fecha y guardar
        user.lastTrainingDate = System.currentTimeMillis();
        db.usuarioDao().updateUsuario(user);
    }

    private static void actualizarRacha(Usuario user) {
        Calendar calUltimo = Calendar.getInstance();
        calUltimo.setTimeInMillis(user.lastTrainingDate);
        normalizarFecha(calUltimo);

        Calendar calHoy = Calendar.getInstance();
        normalizarFecha(calHoy);

        long diffMillis = calHoy.getTimeInMillis() - calUltimo.getTimeInMillis();
        long diasDiferencia = diffMillis / (24 * 60 * 60 * 1000);

        if (diasDiferencia == 1) {
            user.dailyStreak++;
        } else if (diasDiferencia > 1) {
            user.dailyStreak = 1;
        }
        // Si es 0, ya entrenó hoy, no tocamos la racha
    }

    private static void normalizarFecha(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
    }

    private static void crearNuevoUsuario(AppDatabase db) {
        Usuario newUser = new Usuario();
        newUser.totalPoints = 100;
        newUser.dailyStreak = 1;
        newUser.lastTrainingDate = System.currentTimeMillis();
        db.usuarioDao().insertUsuario(newUser);
    }
}