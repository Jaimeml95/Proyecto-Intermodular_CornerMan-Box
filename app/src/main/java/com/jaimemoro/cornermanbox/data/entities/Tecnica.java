package com.jaimemoro.cornermanbox.data.entities;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "tecnicas")
public class Tecnica {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String nombre;
    public String descripcion;
    public String categoria; // "GOLPES", "DEFENSA", "PASOS", "COMBOS"

    // Añadimos esto para poder mostrar una ilustración o icono de la técnica
    public int imagenResId;

    public String videoUrl;

    // Constructor vacío requerido por Room
    public Tecnica() {}

    // Constructor para insertar datos fácilmente
    @Ignore
    public Tecnica(String nombre, String descripcion, String categoria, int imagenResId, String videoUrl) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.imagenResId = imagenResId;
        this.videoUrl = videoUrl;
    }
}