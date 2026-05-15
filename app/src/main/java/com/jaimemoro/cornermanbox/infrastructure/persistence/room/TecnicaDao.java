package com.jaimemoro.cornermanbox.infrastructure.persistence.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.jaimemoro.cornermanbox.data.entities.Tecnica;
import java.util.List;

@Dao
public interface TecnicaDao {

    @Query("SELECT * FROM tecnicas")
    List<Tecnica> obtenerTodas();

    @Query("SELECT * FROM tecnicas WHERE categoria = :cat")
    List<Tecnica> obtenerPorCategoria(String cat);

    @Insert
    void insertarTecnica(Tecnica tecnica);

    @Insert
    void insertarVarias(List<Tecnica> tecnicas);
}