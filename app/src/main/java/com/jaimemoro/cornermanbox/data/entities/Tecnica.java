package com.jaimemoro.cornermanbox.data.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tecnicas")
public class Tecnica {

    @PrimaryKey(autoGenerate = true) //Llave primaria
    public int id;

    public String nombre;
    public String descripcion;
    public String categoria; // "Golpes", "Defensa", "Pasos" ...
}