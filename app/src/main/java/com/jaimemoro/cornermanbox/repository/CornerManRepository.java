package com.jaimemoro.cornermanbox.repository;

import android.app.Application;
import com.jaimemoro.cornermanbox.data.entities.Entrenamiento;
import com.jaimemoro.cornermanbox.data.entities.Tecnica; // Nueva entidad
import com.jaimemoro.cornermanbox.data.entities.Usuario;
import com.jaimemoro.cornermanbox.data.local.AppDatabase;
import com.jaimemoro.cornermanbox.data.local.EntrenamientoDao;
import com.jaimemoro.cornermanbox.data.local.UsuarioDao;
import com.jaimemoro.cornermanbox.data.local.TecnicaDao; // Nuevo DAO

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CornerManRepository {

    private final UsuarioDao usuarioDao;
    private final EntrenamientoDao entrenamientoDao;
    private final TecnicaDao tecnicaDao; // Añadido

    private final ExecutorService executorService = Executors.newFixedThreadPool(4);

    public CornerManRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        usuarioDao = db.usuarioDao();
        entrenamientoDao = db.entrenamientoDao();
        tecnicaDao = db.tecnicaDao(); // Inicializado
    }

    // --- OPERACIONES DE USUARIO ---

    public void getUsuario(RepositoryCallback<Usuario> callback) {
        executorService.execute(() -> {
            try {
                Usuario usuario = usuarioDao.getUsuario();
                callback.onComplete(usuario);
            } catch (Exception e) {
                callback.onError(e);
            }
        });
    }

    public void updateUsuario(Usuario usuario) {
        executorService.execute(() -> {
            try {
                usuarioDao.updateUsuario(usuario);
            } catch (Exception e) {
                // Silent fail para operaciones de actualización
            }
        });
    }

    public void insertUsuario(Usuario usuario, RepositoryCallback<Boolean> callback) {
        executorService.execute(() -> {
            try {
                usuarioDao.insertUsuario(usuario);
                callback.onComplete(true);
            } catch (Exception e) {
                callback.onError(e);
            }
        });
    }

    // --- OPERACIONES DE ENTRENAMIENTO ---

    public void insertEntrenamiento(Entrenamiento entrenamiento) {
        executorService.execute(() -> {
            try {
                entrenamientoDao.insertEntrenamiento(entrenamiento);
            } catch (Exception e) {
                // Silent fail
            }
        });
    }

    public void getHistorialEntrenamientos(RepositoryCallback<List<Entrenamiento>> callback) {
        executorService.execute(() -> {
            try {
                List<Entrenamiento> historial = entrenamientoDao.getAllEntrenamientos();
                callback.onComplete(historial);
            } catch (Exception e) {
                callback.onError(e);
            }
        });
    }

    // --- OPERACIONES DE TÉCNICA (NUEVO) ---

    public void getAllTecnicas(RepositoryCallback<List<Tecnica>> callback) {
        executorService.execute(() -> {
            try {
                List<Tecnica> tecnicas = tecnicaDao.obtenerTodas();
                callback.onComplete(tecnicas);
            } catch (Exception e) {
                callback.onError(e);
            }
        });
    }

    public void getTecnicasByCategoria(String categoria, RepositoryCallback<List<Tecnica>> callback) {
        executorService.execute(() -> {
            try {
                List<Tecnica> filtradas = tecnicaDao.obtenerPorCategoria(categoria);
                callback.onComplete(filtradas);
            } catch (Exception e) {
                callback.onError(e);
            }
        });
    }

    public void insertTecnicas(List<Tecnica> listaTecnicas) {
        executorService.execute(() -> tecnicaDao.insertarVarias(listaTecnicas));
    }

    // --- INTERFAZ PARA RETORNAR DATOS ---
    public interface RepositoryCallback<T> {
        void onComplete(T result);
        default void onError(Exception e) {
            // Default vacío para mantener compatibilidad hacia atrás
        }
    }
}