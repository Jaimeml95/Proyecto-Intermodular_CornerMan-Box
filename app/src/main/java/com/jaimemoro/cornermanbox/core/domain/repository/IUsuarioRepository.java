package com.jaimemoro.cornermanbox.core.domain.repository;

import com.jaimemoro.cornermanbox.core.domain.model.Usuario;

public interface IUsuarioRepository {
    void getUsuario(RepositoryCallback<Usuario> callback);
    void updateUsuario(Usuario usuario);

    interface RepositoryCallback<T> {
        void onSuccess(T result);
        void onError(Exception e);
    }
}