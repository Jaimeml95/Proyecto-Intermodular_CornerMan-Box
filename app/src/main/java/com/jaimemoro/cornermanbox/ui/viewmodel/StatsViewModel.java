package com.jaimemoro.cornermanbox.ui.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jaimemoro.cornermanbox.core.application.usecases.GetUsuarioUseCase;
import com.jaimemoro.cornermanbox.core.application.usecases.RegistrarEntrenamientoUseCase;
import com.jaimemoro.cornermanbox.core.domain.model.Usuario;
import com.jaimemoro.cornermanbox.core.domain.repository.IUsuarioRepository;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class StatsViewModel extends AndroidViewModel {

    private final GetUsuarioUseCase getUsuarioUseCase;
    private final RegistrarEntrenamientoUseCase registrarEntrenamientoUseCase;
    private final MutableLiveData<Usuario> usuario = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();

    @Inject
    public StatsViewModel(
            Application application,
            GetUsuarioUseCase getUsuarioUseCase,
            RegistrarEntrenamientoUseCase registrarEntrenamientoUseCase) {
        super(application);
        this.getUsuarioUseCase = getUsuarioUseCase;
        this.registrarEntrenamientoUseCase = registrarEntrenamientoUseCase;
    }

    public LiveData<Usuario> getUsuario() {
        return usuario;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void cargarUsuario() {
        getUsuarioUseCase.ejecutar(new IUsuarioRepository.RepositoryCallback<Usuario>() {
            @Override
            public void onSuccess(Usuario result) {
                usuario.postValue(result);
            }

            @Override
            public void onError(Exception e) {
                error.postValue(e.getMessage());
            }
        });
    }

    public void registrarEntrenamiento(int roundsCompletados, int duracionTotalSegundos) {
        RegistrarEntrenamientoUseCase.ParametrosEntrenamiento params =
            new RegistrarEntrenamientoUseCase.ParametrosEntrenamiento(roundsCompletados, duracionTotalSegundos);

        registrarEntrenamientoUseCase.ejecutar(params, new RegistrarEntrenamientoUseCase.Callback() {
            @Override
            public void onSuccess() {
                cargarUsuario();
            }

            @Override
            public void onError(Exception e) {
                error.postValue(e.getMessage());
            }
        });
    }
}