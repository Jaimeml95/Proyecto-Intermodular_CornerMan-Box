package com.jaimemoro.cornermanbox.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jaimemoro.cornermanbox.data.entities.Usuario;
import com.jaimemoro.cornermanbox.repository.CornerManRepository;

public class SettingsViewModel extends AndroidViewModel {

    private final CornerManRepository repository;
    private final MutableLiveData<Usuario> usuario = new MutableLiveData<>();
    private final MutableLiveData<Boolean> guardadoExitoso = new MutableLiveData<>();
    private final MutableLiveData<String> errorMensaje = new MutableLiveData<>();

    public SettingsViewModel(@NonNull Application application) {
        super(application);
        repository = new CornerManRepository(application);
        cargarUsuario();
    }

    public LiveData<Usuario> getUsuario() {
        return usuario;
    }

    public LiveData<Boolean> getGuardadoExitoso() {
        return guardadoExitoso;
    }

    public LiveData<String> getErrorMensaje() {
        return errorMensaje;
    }

    private void cargarUsuario() {
        repository.getUsuario(user -> {
            usuario.postValue(user);
        });
    }

    public void guardarUsuario(String nombre, String roundTime, String restTime) {
        if (nombre.isEmpty() || roundTime.isEmpty() || restTime.isEmpty()) {
            errorMensaje.postValue("Por favor, rellena todos los campos");
            return;
        }

        int roundDuration;
        int restDuration;

        try {
            roundDuration = Integer.parseInt(roundTime);
            restDuration = Integer.parseInt(restTime);
        } catch (NumberFormatException e) {
            errorMensaje.postValue("Los valores de tiempo deben ser números");
            return;
        }

        Usuario usuarioActual = usuario.getValue();

        if (usuarioActual == null) {
            usuarioActual = new Usuario();
        }

        usuarioActual.nombre = nombre;
        usuarioActual.roundDurationSeconds = roundDuration;
        usuarioActual.restDurationSeconds = restDuration;

        final Usuario usuarioFinal = usuarioActual;

        if (usuarioFinal.id == 0) {
            repository.insertUsuario(usuarioFinal, success -> {
                guardadoExitoso.postValue(true);
                usuario.postValue(usuarioFinal);
            });
        } else {
            repository.updateUsuario(usuarioFinal);
            guardadoExitoso.postValue(true);
            usuario.postValue(usuarioFinal);
        }
    }
}