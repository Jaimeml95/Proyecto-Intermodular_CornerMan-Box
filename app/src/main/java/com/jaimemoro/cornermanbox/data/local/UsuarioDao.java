package com.jaimemoro.cornermanbox.data.local;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.jaimemoro.cornermanbox.data.entities.Usuario;

@Dao
public interface UsuarioDao {
    @Query("SELECT * FROM usuario WHERE id = 1")
    Usuario getUsuario();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void updateUsuario(Usuario usuario);
}