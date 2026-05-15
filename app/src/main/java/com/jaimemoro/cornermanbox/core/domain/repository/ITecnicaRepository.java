package com.jaimemoro.cornermanbox.core.domain.repository;

import com.jaimemoro.cornermanbox.core.domain.model.Tecnica;
import java.util.List;

public interface ITecnicaRepository {
    void getAllTecnicas(RepositoryCallback<List<Tecnica>> callback);
    void getTecnicasByCategoria(String categoria, RepositoryCallback<List<Tecnica>> callback);
    void insertarVarias(List<Tecnica> tecnicas);
}
