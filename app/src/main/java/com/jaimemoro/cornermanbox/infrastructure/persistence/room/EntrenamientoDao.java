package com.jaimemoro.cornermanbox.infrastructure.persistence.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.jaimemoro.cornermanbox.data.entities.Entrenamiento;
import java.util.List;

@Dao
public interface EntrenamientoDao {
    @Insert
    void insertEntrenamiento(Entrenamiento entrenamiento);

    @Query("SELECT * FROM entrenamientos ORDER BY fecha DESC")
    List<Entrenamiento> getAllEntrenamientos();

    @Query("SELECT SUM(puntosGanados) FROM entrenamientos")
    int getTotalPoints();
}