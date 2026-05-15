package com.jaimemoro.cornermanbox.core.domain.model;

public class Usuario {
    private int id;
    private int totalPoints;
    private int dailyStreak;
    private long lastTrainingDate;
    private String nombre;
    private int roundDurationSeconds;
    private int restDurationSeconds;

    public Usuario() {}

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

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getTotalPoints() { return totalPoints; }
    public void setTotalPoints(int totalPoints) { this.totalPoints = totalPoints; }
    public int getDailyStreak() { return dailyStreak; }
    public void setDailyStreak(int dailyStreak) { this.dailyStreak = dailyStreak; }
    public long getLastTrainingDate() { return lastTrainingDate; }
    public void setLastTrainingDate(long lastTrainingDate) { this.lastTrainingDate = lastTrainingDate; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public int getRoundDurationSeconds() { return roundDurationSeconds; }
    public void setRoundDurationSeconds(int roundDurationSeconds) { this.roundDurationSeconds = roundDurationSeconds; }
    public int getRestDurationSeconds() { return restDurationSeconds; }
    public void setRestDurationSeconds(int restDurationSeconds) { this.restDurationSeconds = restDurationSeconds; }
}