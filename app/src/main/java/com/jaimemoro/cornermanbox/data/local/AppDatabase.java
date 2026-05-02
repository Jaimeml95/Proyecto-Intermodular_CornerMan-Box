package com.jaimemoro.cornermanbox.data.local;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

import com.jaimemoro.cornermanbox.data.entities.Entrenamiento;
import com.jaimemoro.cornermanbox.data.entities.Tecnica;
import com.jaimemoro.cornermanbox.data.entities.Usuario;

// Incrementamos a versión 3 para que Room detecte el cambio de esquema
@Database(entities = {Usuario.class, Entrenamiento.class, Tecnica.class}, version = 3, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract UsuarioDao usuarioDao();
    public abstract EntrenamientoDao entrenamientoDao();
    // public abstract TecnicaDao tecnicaDao(); // Descomentar cuando se cree TecnicaDao

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "cornerman-db")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}