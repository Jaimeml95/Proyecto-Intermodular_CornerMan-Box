package com.jaimemoro.cornermanbox.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;


import com.jaimemoro.cornermanbox.data.entities.Tecnica;
import com.jaimemoro.cornermanbox.repository.CornerManRepository;

import java.util.List;
import java.util.stream.Collectors;

public class TechViewModel extends AndroidViewModel {

    private final CornerManRepository repository;
    private final MutableLiveData<List<Tecnica>> listaTecnicas = new MutableLiveData<>();

    public TechViewModel(@NonNull Application application) {
        super(application);
        repository = new CornerManRepository(application);
        // Carga inicial: Cargamos las técnicas relativas a GOLPES ya que es el foco por defecto.
        filtrarPorCategoria("GOLPES");
    }

    public LiveData<List<Tecnica>> getListaTecnicas() {
        return listaTecnicas;
    }

    public void cargarTodas() {
        repository.getAllTecnicas(listaTecnicas::postValue);
    }

    public void filtrarPorCategoria(String categoria) {
        repository.getTecnicasByCategoria(categoria, listaTecnicas::postValue);
    }

    // Para el buscador del XML
    public void buscarTecnica(String query) {
        repository.getAllTecnicas(todas -> {
            // Usamos .collect(Collectors.toList()) que es compatible con API 24+
            List<Tecnica> filtradas = todas.stream()
                    .filter(t -> t.nombre.toLowerCase().contains(query.toLowerCase()))
                    .collect(Collectors.toList());

            listaTecnicas.postValue(filtradas);
        });
    }
}