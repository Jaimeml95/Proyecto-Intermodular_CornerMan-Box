package com.jaimemoro.cornermanbox.infrastructure.persistence.repositories;

import android.app.Application;
import com.jaimemoro.cornermanbox.core.domain.model.Tecnica;
import com.jaimemoro.cornermanbox.core.domain.repository.ITecnicaRepository;
import com.jaimemoro.cornermanbox.infrastructure.persistence.room.AppDatabase;
import com.jaimemoro.cornermanbox.infrastructure.persistence.mappers.TecnicaMapper;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RoomTecnicaRepository implements ITecnicaRepository {

    private final AppDatabase database;
    private final ExecutorService executor;
    private final TecnicaMapper mapper;

    public RoomTecnicaRepository(Application application) {
        this.database = AppDatabase.getInstance(application);
        this.executor = Executors.newFixedThreadPool(4);
        this.mapper = new TecnicaMapper();
    }

    @Override
    public void getAllTecnicas(ITecnicaRepository.RepositoryCallback<List<Tecnica>> callback) {
        executor.execute(() -> {
            try {
                List<com.jaimemoro.cornermanbox.data.entities.Tecnica> entities =
                    database.tecnicaDao().obtenerTodas();
                List<Tecnica> domains = mapper.toDomainList(entities);
                callback.onSuccess(domains);
            } catch (Exception e) {
                callback.onError(e);
            }
        });
    }

    @Override
    public void getTecnicasByCategoria(String categoria,
            ITecnicaRepository.RepositoryCallback<List<Tecnica>> callback) {
        executor.execute(() -> {
            try {
                List<com.jaimemoro.cornermanbox.data.entities.Tecnica> entities =
                    database.tecnicaDao().obtenerPorCategoria(categoria);
                List<Tecnica> domains = mapper.toDomainList(entities);
                callback.onSuccess(domains);
            } catch (Exception e) {
                callback.onError(e);
            }
        });
    }

    @Override
    public void insertTecnicas(List<Tecnica> tecnicas) {
        executor.execute(() -> {
            database.tecnicaDao().insertarVarias(mapper.toEntityList(tecnicas));
        });
    }
}