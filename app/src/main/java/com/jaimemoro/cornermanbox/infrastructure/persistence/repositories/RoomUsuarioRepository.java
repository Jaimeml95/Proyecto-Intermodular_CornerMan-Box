package com.jaimemoro.cornermanbox.infrastructure.persistence.repositories;

import android.app.Application;
import com.jaimemoro.cornermanbox.core.domain.model.Usuario;
import com.jaimemoro.cornermanbox.core.domain.repository.IUsuarioRepository;
import com.jaimemoro.cornermanbox.core.domain.repository.RepositoryCallback;
import com.jaimemoro.cornermanbox.infrastructure.persistence.room.AppDatabase;
import com.jaimemoro.cornermanbox.infrastructure.persistence.mappers.UsuarioMapper;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RoomUsuarioRepository implements IUsuarioRepository {

    private final AppDatabase database;
    private final ExecutorService executor;
    private final UsuarioMapper mapper;

    public RoomUsuarioRepository(Application application) {
        this.database = AppDatabase.getInstance(application);
        this.executor = Executors.newFixedThreadPool(4);
        this.mapper = new UsuarioMapper();
    }

    @Override
    public void getUsuario(RepositoryCallback<Usuario> callback) {
        executor.execute(() -> {
            try {
                com.jaimemoro.cornermanbox.data.entities.Usuario entity =
                    database.usuarioDao().getUsuario();
                Usuario domain = mapper.toDomain(entity);
                callback.onSuccess(domain);
            } catch (Exception e) {
                callback.onError(e);
            }
        });
    }

    @Override
    public void updateUsuario(Usuario usuario) {
        executor.execute(() -> {
            com.jaimemoro.cornermanbox.data.entities.Usuario entity =
                mapper.toEntity(usuario);
            database.usuarioDao().updateUsuario(entity);
        });
    }
}