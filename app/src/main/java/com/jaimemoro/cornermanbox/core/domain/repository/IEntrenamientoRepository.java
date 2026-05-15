package com.jaimemoro.cornermanbox.core.domain.repository;

import com.jaimemoro.cornermanbox.core.domain.model.Entrenamiento;
import java.util.List;

public interface IEntrenamientoRepository {
    void insertEntrenamiento(Entrenamiento entrenamiento);
    void getHistorialEntrenamientos(RepositoryCallback<List<Entrenamiento>> callback);
}
