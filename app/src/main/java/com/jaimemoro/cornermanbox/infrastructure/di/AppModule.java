package com.jaimemoro.cornermanbox.infrastructure.di;

import android.app.Application;

import com.jaimemoro.cornermanbox.core.domain.repository.IUsuarioRepository;
import com.jaimemoro.cornermanbox.core.domain.repository.ITecnicaRepository;
import com.jaimemoro.cornermanbox.core.domain.repository.IEntrenamientoRepository;
import com.jaimemoro.cornermanbox.infrastructure.persistence.repositories.RoomUsuarioRepository;
import com.jaimemoro.cornermanbox.infrastructure.persistence.repositories.RoomTecnicaRepository;
import com.jaimemoro.cornermanbox.infrastructure.persistence.repositories.RoomEntrenamientoRepository;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class AppModule {

    @Provides
    @Singleton
    public IUsuarioRepository provideUsuarioRepository(Application app) {
        return new RoomUsuarioRepository(app);
    }

    @Provides
    @Singleton
    public ITecnicaRepository provideTecnicaRepository(Application app) {
        return new RoomTecnicaRepository(app);
    }

    @Provides
    @Singleton
    public IEntrenamientoRepository provideEntrenamientoRepository(Application app) {
        return new RoomEntrenamientoRepository(app);
    }
}