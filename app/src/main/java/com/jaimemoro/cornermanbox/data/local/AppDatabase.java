package com.jaimemoro.cornermanbox.data.local;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import com.jaimemoro.cornermanbox.data.local.UsuarioDao;
import com.jaimemoro.cornermanbox.data.entities.Usuario;

@Database(entities = {Usuario.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UsuarioDao usuarioDao();
}