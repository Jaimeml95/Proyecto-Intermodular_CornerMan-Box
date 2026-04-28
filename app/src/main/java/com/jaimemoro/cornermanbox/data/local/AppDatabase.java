package com.jaimemoro.cornermanbox.data.local;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;
import com.jaimemoro.cornermanbox.data.entities.Usuario;

// Incrementamos la versión a 2 ya que el esquema cambió
@Database(entities = {Usuario.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {

    public abstract UsuarioDao usuarioDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "cornerman-db")
                            .fallbackToDestructiveMigration() // Para que si no sabe que hacer borre la tabla y la cree de nuevo en vez de crashear.
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}