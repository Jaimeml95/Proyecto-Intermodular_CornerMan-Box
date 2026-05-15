package com.jaimemoro.cornermanbox.core.application.usecases;

import com.jaimemoro.cornermanbox.core.domain.model.Tecnica;
import com.jaimemoro.cornermanbox.core.domain.repository.ITecnicaRepository;
import java.util.List;

public class GetTecnicasUseCase {

    private final ITecnicaRepository tecnicaRepository;

    public GetTecnicasUseCase(ITecnicaRepository tecnicaRepository) {
        this.tecnicaRepository = tecnicaRepository;
    }

    public void obtenerTodas(ITecnicaRepository.RepositoryCallback<List<Tecnica>> callback) {
        tecnicaRepository.getAllTecnicas(callback);
    }

    public void obtenerPorCategoria(String categoria, ITecnicaRepository.RepositoryCallback<List<Tecnica>> callback) {
        tecnicaRepository.getTecnicasByCategoria(categoria, callback);
    }
}