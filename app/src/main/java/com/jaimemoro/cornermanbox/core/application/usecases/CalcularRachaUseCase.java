package com.jaimemoro.cornermanbox.core.application.usecases;

import com.jaimemoro.cornermanbox.core.domain.model.Usuario;
import java.util.Calendar;

public class CalcularRachaUseCase {

    public static final int PUNTOS_POR_ENTRENAMIENTO = 100;

    public ResultadoRacha calcular(Usuario usuario) {
        if (usuario == null) {
            return new ResultadoRacha(100, 1, true);
        }

        Calendar calUltimo = Calendar.getInstance();
        calUltimo.setTimeInMillis(usuario.getLastTrainingDate());
        normalizarFecha(calUltimo);

        Calendar calHoy = Calendar.getInstance();
        normalizarFecha(calHoy);

        long diffMillis = calHoy.getTimeInMillis() - calUltimo.getTimeInMillis();
        long diasDiferencia = diffMillis / (24 * 60 * 60 * 1000);

        int nuevaRacha;
        int nuevosPuntos;

        if (diasDiferencia == 0) {
            nuevaRacha = usuario.getDailyStreak();
            nuevosPuntos = 0;
        } else if (diasDiferencia == 1) {
            nuevaRacha = usuario.getDailyStreak() + 1;
            nuevosPuntos = PUNTOS_POR_ENTRENAMIENTO;
        } else {
            nuevaRacha = 1;
            nuevosPuntos = PUNTOS_POR_ENTRENAMIENTO;
        }

        return new ResultadoRacha(
            usuario.getTotalPoints() + nuevosPuntos,
            nuevaRacha,
            diasDiferencia == 0
        );
    }

    public boolean validarRacha(Usuario usuario) {
        if (usuario == null) return false;

        Calendar calUltimo = Calendar.getInstance();
        calUltimo.setTimeInMillis(usuario.getLastTrainingDate());
        normalizarFecha(calUltimo);

        Calendar calHoy = Calendar.getInstance();
        normalizarFecha(calHoy);

        long diffMillis = calHoy.getTimeInMillis() - calUltimo.getTimeInMillis();
        long diasDiferencia = diffMillis / (24 * 60 * 60 * 1000);

        return diasDiferencia <= 1;
    }

    private void normalizarFecha(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
    }

    public static class ResultadoRacha {
        public final int nuevoTotalPuntos;
        public final int nuevaRacha;
        public final boolean entrenoHoy;

        public ResultadoRacha(int nuevoTotalPuntos, int nuevaRacha, boolean entrenoHoy) {
            this.nuevoTotalPuntos = nuevoTotalPuntos;
            this.nuevaRacha = nuevaRacha;
            this.entrenoHoy = entrenoHoy;
        }
    }
}