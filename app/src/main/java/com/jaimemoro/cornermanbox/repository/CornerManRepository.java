package com.jaimemoro.cornermanbox.repository;

import android.app.Application;
import com.jaimemoro.cornermanbox.data.entities.Entrenamiento;
import com.jaimemoro.cornermanbox.data.entities.Usuario;
import com.jaimemoro.cornermanbox.data.local.AppDatabase;
import com.jaimemoro.cornermanbox.data.local.EntrenamientoDao;
import com.jaimemoro.cornermanbox.data.local.UsuarioDao;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CornerManRepository {

    private final UsuarioDao usuarioDao;
    private final EntrenamientoDao entrenamientoDao;

    // Un solo executor para todas las tareas de base de datos de la app
    private final ExecutorService executorService = Executors.newFixedThreadPool(4);

    public CornerManRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        usuarioDao = db.usuarioDao();
            entrenamientoDao = db.entrenamientoDao();
    }

    // --- OPERACIONES DE USUARIO ---

    public void getUsuario(RepositoryCallback<Usuario> callback) {
        executorService.execute(() -> {
            Usuario usuario = usuarioDao.getUsuario();
            callback.onComplete(usuario);
        });
    }

    public void updateUsuario(Usuario usuario) {
        executorService.execute(() -> usuarioDao.updateUsuario(usuario));
    }

    // --- OPERACIONES DE ENTRENAMIENTO ---

    public void insertEntrenamiento(Entrenamiento entrenamiento) {
        executorService.execute(() -> entrenamientoDao.insertEntrenamiento(entrenamiento));
    }

    public void getHistorialEntrenamientos(RepositoryCallback<List<Entrenamiento>> callback) {
        executorService.execute(() -> {
            List<Entrenamiento> historial = entrenamientoDao.getAllEntrenamientos();
            callback.onComplete(historial);
        });
    }

    // --- INTERFAZ PARA RETORNAR DATOS ---
    public interface RepositoryCallback<T> {
        void onComplete(T result);
    }
}