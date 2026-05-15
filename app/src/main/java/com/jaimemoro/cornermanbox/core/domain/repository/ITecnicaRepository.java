package com.jaimemoro.cornermanbox.core.domain.repository;

import com.jaimemoro.cornermanbox.core.domain.model.Tecnica;
import java.util.List;

public interface ITecnicaRepository {
    void getAllTecnicas(RepositoryCallback<List<Tecnica>> callback);
    void getTecnicasByCategoria(String categoria, RepositoryCallback<List<Tecnica>> callback);
    void insertTecnicas(List<Tecnica> tecnicas);

    interface RepositoryCallback<T> {
        void onSuccess(T result);
        void onError(Exception e);
    }
}