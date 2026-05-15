package com.jaimemoro.cornermanbox.core.application.usecases;

import com.jaimemoro.cornermanbox.core.domain.model.Usuario;
import com.jaimemoro.cornermanbox.core.domain.repository.IUsuarioRepository;

public class GetUsuarioUseCase {

    private final IUsuarioRepository usuarioRepository;

    public GetUsuarioUseCase(IUsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public void ejecutar(IUsuarioRepository.RepositoryCallback<Usuario> callback) {
        usuarioRepository.getUsuario(new IUsuarioRepository.RepositoryCallback<Usuario>() {
            @Override
            public void onSuccess(Usuario result) {
                CalcularRachaUseCase calcularRachaUseCase = new CalcularRachaUseCase();
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