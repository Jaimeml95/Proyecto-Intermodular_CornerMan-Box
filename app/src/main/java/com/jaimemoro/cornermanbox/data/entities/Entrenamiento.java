package com.jaimemoro.cornermanbox.data.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "entrenamientos")
public class Entrenamiento {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public long fecha; // Guardaremos el timestamp
    public int puntosGanados;
    public int roundsCompletados;
    public int duracionTotalSegundos;
}