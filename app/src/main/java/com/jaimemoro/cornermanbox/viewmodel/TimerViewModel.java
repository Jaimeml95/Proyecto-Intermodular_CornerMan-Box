package com.jaimemoro.cornermanbox.viewmodel;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jaimemoro.cornermanbox.data.spotify.SpotifyManager;
import com.jaimemoro.cornermanbox.repository.CornerManRepository;
import com.jaimemoro.cornermanbox.service.TimerService;

public class TimerViewModel extends AndroidViewModel {

    private final MutableLiveData<String> tiempoRestante = new MutableLiveData<>("03:00");
    private final MutableLiveData<String> infoAsalto = new MutableLiveData<>("ASALTO 1 / 12");
    private final MutableLiveData<Boolean> esDescanso = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isRunning = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isReady = new MutableLiveData<>(true);
    private final MutableLiveData<Boolean> isListening = new MutableLiveData<>(false);

    private final MutableLiveData<Boolean> spotifyConectado = new MutableLiveData<>(false);
    private final MutableLiveData<String> cancionActual = new MutableLiveData<>("");

    private boolean hasStarted = false;

    private final CornerManRepository repository;
    private SpotifyManager spotifyManager;
    private final Context context;

    public TimerViewModel(@NonNull Application application) {
        super(application);
        this.context = application.getApplicationContext();
        this.repository = new CornerManRepository(application);
        this.spotifyManager = new SpotifyManager(context);
    }

    public LiveData<String> getTiempoRestante() {
        return tiempoRestante;
    }

    public LiveData<String> getInfoAsalto() {
        return infoAsalto;
    }

    public LiveData<Boolean> getEsDescanso() {
        return esDescanso;
    }

    public LiveData<Boolean> getIsRunning() {
        return isRunning;
    }

    public LiveData<Boolean> getIsReady() {
        return isReady;
    }

    public LiveData<Boolean> getIsListening() {
        return isListening;
    }

    public LiveData<Boolean> getSpotifyConectado() {
        return spotifyConectado;
    }

    public LiveData<String> getCancionActual() {
        return cancionActual;
    }

    public boolean isHasStarted() {
        return hasStarted;
    }

    public void setHasStarted(boolean started) {
        this.hasStarted = started;
    }

    public void iniciarTimer() {
        enviarAccion(TimerService.ACTION_START);
    }

    public void pausarTimer() {
        enviarAccion(TimerService.ACTION_PAUSE);
    }

    public void reanudarTimer() {
        enviarAccion(TimerService.ACTION_RESUME);
    }

    public void resetearTimer() {
        enviarAccion(TimerService.ACTION_RESET);
    }

    public void getStatus() {
        enviarAccion(TimerService.ACTION_GET_STATUS);
    }

    private void enviarAccion(String accion) {
        Intent intent = new Intent(context, TimerService.class);
        intent.setAction(accion);
        context.startService(intent);
    }

    public void actualizarDesdeService(String tiempo, String info, boolean pEsDescanso,
                                       boolean pRunning, boolean pListening, boolean pReady) {
        tiempoRestante.postValue(tiempo);
        infoAsalto.postValue(info);
        esDescanso.postValue(pEsDescanso);
        isRunning.postValue(pRunning);
        isListening.postValue(pListening);
        isReady.postValue(pReady);

        this.hasStarted = pRunning || !pReady;
    }

    public void resetearEstadoLocal() {
        tiempoRestante.postValue("03:00");
        infoAsalto.postValue("ASALTO 1 / 12");
        esDescanso.postValue(false);
        isRunning.postValue(false);
        isListening.postValue(false);
        isReady.postValue(true);
        hasStarted = false;
    }

    public void inicializarSpotify() {
        spotifyManager.conectar(new SpotifyManager.SpotifyConnectionListener() {
            @Override
            public void onConnected() {
                spotifyConectado.postValue(true);
                suscribirseACancion();
            }

            @Override
            public void onFailure(Throwable error) {
                spotifyConectado.postValue(false);
            }
        });
    }

    private void suscribirseACancion() {
        spotifyManager.suscribirseACancion((titulo, artista) -> {
            cancionActual.postValue(titulo + " - " + artista);
        });
    }

    public void desconectarSpotify() {
        if (spotifyManager != null) {
            spotifyManager.desconectar();
        }
    }
}