package com.jaimemoro.cornermanbox.core.application.usecases;

import com.jaimemoro.cornermanbox.core.domain.model.Usuario;
import com.jaimemoro.cornermanbox.core.domain.repository.IUsuarioRepository;
import com.jaimemoro.cornermanbox.core.domain.repository.RepositoryCallback;

public class GetUsuarioUseCase {

    private final IUsuarioRepository usuarioRepository;
    private final CalcularRachaUseCase calcularRachaUseCase;

    public GetUsuarioUseCase(IUsuarioRepository usuarioRepository, CalcularRachaUseCase calcularRachaUseCase) {
        this.usuarioRepository = usuarioRepository;
        this.calcularRachaUseCase = calcularRachaUseCase;
    }

    public void ejecutar(RepositoryCallback<Usuario> callback) {
        usuarioRepository.getUsuario(new RepositoryCallback<Usuario>() {
            @Override
            public void onSuccess(Usuario result) {
                if (!calcularRachaUseCase.validarRacha(result)) {
                    result.setDailyStreak(0);
                    usuarioRepository.updateUsuario(result);
                }
                callback.onSuccess(result);
            }

            @Override
            public void onError(Exception e) {
                callback.onError(e);
            }
        });
    }
}