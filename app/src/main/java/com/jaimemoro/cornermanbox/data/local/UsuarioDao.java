package com.jaimemoro.cornermanbox.data.local;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.jaimemoro.cornermanbox.data.entities.Usuario;

@Dao
public interface UsuarioDao {

    @Query("SELECT * FROM usuario LIMIT 1")
    Usuario getUsuario();

    @Insert
    void insertUsuario(Usuario usuario);

    @Update
    void updateUsuario(Usuario usuario);
}