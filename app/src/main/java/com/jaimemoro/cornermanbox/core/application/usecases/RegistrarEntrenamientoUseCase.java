package com.jaimemoro.cornermanbox.core.application.usecases;

import com.jaimemoro.cornermanbox.core.domain.model.Entrenamiento;
import com.jaimemoro.cornermanbox.core.domain.model.Usuario;
import com.jaimemoro.cornermanbox.core.domain.repository.IUsuarioRepository;
import com.jaimemoro.cornermanbox.core.domain.repository.IEntrenamientoRepository;
import com.jaimemoro.cornermanbox.core.domain.repository.RepositoryCallback;

public class RegistrarEntrenamientoUseCase {

    private final CalcularRachaUseCase calcularRachaUseCase;
    private final IUsuarioRepository usuarioRepository;
    private final IEntrenamientoRepository entrenamientoRepository;

    public RegistrarEntrenamientoUseCase(
            CalcularRachaUseCase calcularRachaUseCase,
            IUsuarioRepository usuarioRepository,
            IEntrenamientoRepository entrenamientoRepository) {
        this.calcularRachaUseCase = calcularRachaUseCase;
        this.usuarioRepository = usuarioRepository;
        this.entrenamientoRepository = entrenamientoRepository;
    }

    public void ejecutar(ParametrosEntrenamiento parametros, Callback callback) {
        usuarioRepository.getUsuario(new RepositoryCallback<Usuario>() {
            @Override
            public void onSuccess(Usuario usuario) {
                CalcularRachaUseCase.ResultadoRacha resultado = calcularRachaUseCase.calcular(usuario);

                usuario.setTotalPoints(resultado.nuevoTotalPuntos);
                usuario.setDailyStreak(resultado.nuevaRacha);
                usuario.setLastTrainingDate(System.currentTimeMillis());

                usuarioRepository.updateUsuario(usuario);

                Entrenamiento entrenamiento = new Entrenamiento();
                entrenamiento.setFecha(System.currentTimeMillis());
                entrenamiento.setPuntosGanados(CalcularRachaUseCase.PUNTOS_POR_ENTRENAMIENTO);
                entrenamiento.setRoundsCompletados(parametros.roundsCompletados);
                entrenamiento.setDuracionTotalSegundos(parametros.duracionTotalSegundos);

                entrenamientoRepository.insertEntrenamiento(entrenamiento);

                callback.onSuccess();
            }

            @Override
            public void onError(Exception e) {
                callback.onError(e);
            }
        });
    }

    public static class ParametrosEntrenamiento {
        public final int roundsCompletados;
        public final int duracionTotalSegundos;

        public ParametrosEntrenamiento(int roundsCompletados, int duracionTotalSegundos) {
            this.roundsCompletados = roundsCompletados;
            this.duracionTotalSegundos = duracionTotalSegundos;
        }
    }

    public interface Callback {
        void onSuccess();
        void onError(Exception e);
    }
}