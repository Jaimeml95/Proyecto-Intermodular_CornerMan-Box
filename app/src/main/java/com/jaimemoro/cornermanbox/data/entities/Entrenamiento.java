package com.jaimemoro.cornermanbox.data.entities;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "entrenamientos")
public class Entrenamiento {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public long fecha; // Guardaremos el timestamp
    public int puntosGanados;
    public int roundsCompletados;
    public int duracionTotalSegundos;

    public Entrenamiento() {
    }
@Ignore
    public Entrenamiento(int duracionTotalSegundos, long fecha, int id, int puntosGanados, int roundsCompletados) {
        this.duracionTotalSegundos = duracionTotalSegundos;
        this.fecha = fecha;
        this.id = id;
        this.puntosGanados = puntosGanados;
        this.roundsCompletados = roundsCompletados;
    }
}