package com.jaimemoro.cornermanbox.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.jaimemoro.cornermanbox.core.application.usecases.GetUsuarioUseCase;
import com.jaimemoro.cornermanbox.core.application.usecases.UpdateUsuarioUseCase;
import com.jaimemoro.cornermanbox.core.domain.model.Usuario;
import com.jaimemoro.cornermanbox.core.domain.repository.RepositoryCallback;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class SettingsViewModel extends ViewModel {

    private final GetUsuarioUseCase getUsuarioUseCase;
    private final UpdateUsuarioUseCase updateUsuarioUseCase;

    private final MutableLiveData<Usuario> usuario = new MutableLiveData<>();
    private final MutableLiveData<Boolean> saveSuccess = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();

    @Inject
    public SettingsViewModel(GetUsuarioUseCase getUsuarioUseCase, UpdateUsuarioUseCase updateUsuarioUseCase) {
        this.getUsuarioUseCase = getUsuarioUseCase;
        this.updateUsuarioUseCase = updateUsuarioUseCase;
    }

    public LiveData<Usuario> getUsuario() {
        return usuario;
    }

    public LiveData<Boolean> getSaveSuccess() {
        return saveSuccess;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void cargarUsuario() {
        getUsuarioUseCase.ejecutar(new RepositoryCallback<Usuario>() {
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

    public void guardarUsuario(Usuario user) {
        updateUsuarioUseCase.ejecutar(user, new RepositoryCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                saveSuccess.postValue(true);
            }

            @Override
            public void onError(Exception e) {
                error.postValue(e.getMessage());
            }
        });
    }
}
