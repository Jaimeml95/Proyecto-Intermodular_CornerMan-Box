package com.jaimemoro.cornermanbox.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.jaimemoro.cornermanbox.core.application.usecases.GetTecnicasUseCase;
import com.jaimemoro.cornermanbox.core.domain.model.Tecnica;
import com.jaimemoro.cornermanbox.core.domain.repository.RepositoryCallback;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class TechViewModel extends ViewModel {

    private final GetTecnicasUseCase getTecnicasUseCase;
    private final MutableLiveData<List<Tecnica>> listaTecnicas = new MutableLiveData<>();

    @Inject
    public TechViewModel(GetTecnicasUseCase getTecnicasUseCase) {
        this.getTecnicasUseCase = getTecnicasUseCase;
        filtrarPorCategoria("GOLPES");
    }

    public LiveData<List<Tecnica>> getListaTecnicas() {
        return listaTecnicas;
    }

    public void cargarTodas() {
        getTecnicasUseCase.obtenerTodas(new RepositoryCallback<List<Tecnica>>() {
            @Override
            public void onSuccess(List<Tecnica> result) {
                listaTecnicas.postValue(result);
            }

            @Override
            public void onError(Exception e) {
            }
        });
    }

    public void filtrarPorCategoria(String categoria) {
        getTecnicasUseCase.obtenerPorCategoria(categoria, new RepositoryCallback<List<Tecnica>>() {
            @Override
            public void onSuccess(List<Tecnica> result) {
                listaTecnicas.postValue(result);
            }

            @Override
            public void onError(Exception e) {
            }
        });
    }

    public void buscarTecnica(String query) {
        getTecnicasUseCase.obtenerTodas(new RepositoryCallback<List<Tecnica>>() {
            @Override
            public void onSuccess(List<Tecnica> todas) {
                List<Tecnica> filtradas = todas.stream()
                        .filter(t -> t.getNombre().toLowerCase().contains(query.toLowerCase()))
                        .collect(Collectors.toList());
                listaTecnicas.postValue(filtradas);
            }

            @Override
            public void onError(Exception e) {
            }
        });
    }
}