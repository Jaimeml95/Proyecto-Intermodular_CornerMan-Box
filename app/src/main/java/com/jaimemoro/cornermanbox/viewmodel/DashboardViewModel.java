package com.jaimemoro.cornermanbox.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jaimemoro.cornermanbox.data.entities.Usuario;
import com.jaimemoro.cornermanbox.repository.CornerManRepository;

public class DashboardViewModel extends AndroidViewModel {

    private final CornerManRepository repository;
    private final MutableLiveData<Usuario> usuario = new MutableLiveData<>();

    public DashboardViewModel(@NonNull Application application) {
        super(application);
        repository = new CornerManRepository(application);
    }

    public LiveData<Usuario> getUsuario() {
        return usuario;
    }

    public void cargarUsuario() {
        repository.getUsuario(user -> {
            usuario.postValue(user);
        });
    }

    public String getNombreMostrar() {
        Usuario user = usuario.getValue();
        if (user != null && user.nombre != null && !user.nombre.trim().isEmpty()) {
            return user.nombre;
        }
        return "Boxeador";
    }

    public String getTextoRacha() {
        Usuario user = usuario.getValue();
        if (user == null) return "0 Días seguidos";

        if (user.dailyStreak == 1) {
            return "1 Día entrenado";
        }
        return user.dailyStreak + " Días seguidos";
    }

    public String getTextoPuntos() {
        Usuario user = usuario.getValue();
        if (user == null) return "0 pts";
        return String.format("%,d pts", user.totalPoints);
    }

    public String getTextoApoyo() {
        Usuario user = usuario.getValue();
        String nombre = getNombreMostrar();

        if (user == null || user.dailyStreak == 0) {
            return "Has perdido la racha. ¡A por ello de nuevo, " + nombre + "!";
        }
        return "¡Mantén el ritmo, " + nombre + "!";
    }
}