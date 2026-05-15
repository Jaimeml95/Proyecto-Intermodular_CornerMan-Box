package com.jaimemoro.cornermanbox.infrastructure.persistence.repositories;

import android.app.Application;
import com.jaimemoro.cornermanbox.core.domain.model.Entrenamiento;
import com.jaimemoro.cornermanbox.core.domain.repository.IEntrenamientoRepository;
import com.jaimemoro.cornermanbox.infrastructure.persistence.room.AppDatabase;
import com.jaimemoro.cornermanbox.infrastructure.persistence.mappers.EntrenamientoMapper;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RoomEntrenamientoRepository implements IEntrenamientoRepository {

    private final AppDatabase database;
    private final ExecutorService executor;
    private final EntrenamientoMapper mapper;

    public RoomEntrenamientoRepository(Application application) {
        this.database = AppDatabase.getInstance(application);
        this.executor = Executors.newFixedThreadPool(4);
        this.mapper = new EntrenamientoMapper();
    }

    @Override
    public void insertEntrenamiento(Entrenamiento entrenamiento) {
        executor.execute(() -> {
            database.entrenamientoDao().insertEntrenamiento(
                mapper.toEntity(entrenamiento));
        });
    }

    @Override
    public void getHistorialEntrenamientos(
            IEntrenamientoRepository.RepositoryCallback<List<Entrenamiento>> callback) {
        executor.execute(() -> {
            try {
                List<com.jaimemoro.cornermanbox.data.entities.Entrenamiento> entities =
                    database.entrenamientoDao().getAllEntrenamientos();
                List<Entrenamiento> domains = mapper.toDomainList(entities);
                callback.onSuccess(domains);
            } catch (Exception e) {
                callback.onError(e);
            }
        });
    }
}