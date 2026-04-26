package com.jaimemoro.cornermanbox.data.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "usuario")
public class Usuario {

    @PrimaryKey
    public int id;

    public int totalPoints;
    public int dailyStreak;
    public long lastTrainingDate;

    public Usuario(int id, int totalPoints, int dailyStreak, long lastTrainingDate) {
        this.id = id;
        this.totalPoints = totalPoints;
        this.dailyStreak = dailyStreak;
        this.lastTrainingDate = lastTrainingDate;
    }
}