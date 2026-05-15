package com.jaimemoro.cornermanbox.infrastructure.di;

import com.jaimemoro.cornermanbox.core.application.usecases.CalcularRachaUseCase;
import com.jaimemoro.cornermanbox.core.application.usecases.GetUsuarioUseCase;
import com.jaimemoro.cornermanbox.core.application.usecases.GetTecnicasUseCase;
import com.jaimemoro.cornermanbox.core.application.usecases.RegistrarEntrenamientoUseCase;
import com.jaimemoro.cornermanbox.core.application.usecases.UpdateUsuarioUseCase;
import com.jaimemoro.cornermanbox.core.domain.repository.IUsuarioRepository;
import com.jaimemoro.cornermanbox.core.domain.repository.ITecnicaRepository;
import com.jaimemoro.cornermanbox.core.domain.repository.IEntrenamientoRepository;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class UseCaseModule {

    @Provides
    @Singleton
    public CalcularRachaUseCase provideCalcularRachaUseCase() {
        return new CalcularRachaUseCase();
    }

    @Provides
    @Singleton
    public GetUsuarioUseCase provideGetUsuarioUseCase(IUsuarioRepository repository, CalcularRachaUseCase calcularRachaUseCase) {
        return new GetUsuarioUseCase(repository, calcularRachaUseCase);
    }

    @Provides
    @Singleton
    public UpdateUsuarioUseCase provideUpdateUsuarioUseCase(IUsuarioRepository repository) {
        return new UpdateUsuarioUseCase(repository);
    }

    @Provides
    @Singleton
    public GetTecnicasUseCase provideGetTecnicasUseCase(ITecnicaRepository repository) {
        return new GetTecnicasUseCase(repository);
    }

    @Provides
    @Singleton
    public RegistrarEntrenamientoUseCase provideRegistrarEntrenamientoUseCase(
            CalcularRachaUseCase calcularRachaUseCase,
            IUsuarioRepository usuarioRepository,
            IEntrenamientoRepository entrenamientoRepository) {
        return new RegistrarEntrenamientoUseCase(
            calcularRachaUseCase,
            usuarioRepository,
            entrenamientoRepository
        );
    }
}