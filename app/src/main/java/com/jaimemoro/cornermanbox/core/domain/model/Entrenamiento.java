package com.jaimemoro.cornermanbox.core.domain.model;

public class Entrenamiento {
    private int id;
    private long fecha;
    private int puntosGanados;
    private int roundsCompletados;
    private int duracionTotalSegundos;

    public Entrenamiento() {}

    public Entrenamiento(int id, long fecha, int puntosGanados,
                         int roundsCompletados, int duracionTotalSegundos) {
        this.id = id;
        this.fecha = fecha;
        this.puntosGanados = puntosGanados;
        this.roundsCompletados = roundsCompletados;
        this.duracionTotalSegundos = duracionTotalSegundos;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public long getFecha() { return fecha; }
    public void setFecha(long fecha) { this.fecha = fecha; }
    public int getPuntosGanados() { return puntosGanados; }
    public void setPuntosGanados(int puntosGanados) { this.puntosGanados = puntosGanados; }
    public int getRoundsCompletados() { return roundsCompletados; }
    public void setRoundsCompletados(int roundsCompletados) { this.roundsCompletados = roundsCompletados; }
    public int getDuracionTotalSegundos() { return duracionTotalSegundos; }
    public void setDuracionTotalSegundos(int duracionTotalSegundos) { this.duracionTotalSegundos = duracionTotalSegundos; }
}