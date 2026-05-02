package com.jaimemoro.cornermanbox.data.entities;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "usuario")
public class Usuario {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public int totalPoints;
    public int dailyStreak;
    public long lastTrainingDate;

    // Personalización del Usuario y del tiempo de los asaltos y descansos
    public String nombre = "Boxeador";        // Nombre por defecto
    public int roundDurationSeconds = 180;    // 3 minutos por defecto
    public int restDurationSeconds = 60;      // 1 minuto por defecto

    public Usuario() {
    }
@Ignore
    public Usuario(int id, int totalPoints, int dailyStreak, long lastTrainingDate,
                   String nombre, int roundDurationSeconds, int restDurationSeconds) {
        this.id = id;
        this.totalPoints = totalPoints;
        this.dailyStreak = dailyStreak;
        this.lastTrainingDate = lastTrainingDate;
        this.nombre = nombre;
        this.roundDurationSeconds = roundDurationSeconds;
        this.restDurationSeconds = restDurationSeconds;
    }
}