package com.jaimemoro.cornermanbox.core.application.usecases;

import com.jaimemoro.cornermanbox.core.domain.model.Usuario;
import com.jaimemoro.cornermanbox.core.domain.repository.IUsuarioRepository;

public class UpdateUsuarioUseCase {

    private final IUsuarioRepository usuarioRepository;

    public UpdateUsuarioUseCase(IUsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public void ejecutar(Usuario usuario, IUsuarioRepository.RepositoryCallback<Void> callback) {
        usuarioRepository.updateUsuario(usuario);
        callback.onSuccess(null);
    }
}