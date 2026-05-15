package com.jaimemoro.cornermanbox.core.domain.model;

public class Tecnica {
    private int id;
    private String nombre;
    private String descripcion;
    private String categoria;
    private int imagenResId;
    private String videoUrl;

    public Tecnica() {}

    public Tecnica(int id, String nombre, String descripcion, String categoria,
                   int imagenResId, String videoUrl) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.imagenResId = imagenResId;
        this.videoUrl = videoUrl;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public int getImagenResId() { return imagenResId; }
    public void setImagenResId(int imagenResId) { this.imagenResId = imagenResId; }
    public String getVideoUrl() { return videoUrl; }
    public void setVideoUrl(String videoUrl) { this.videoUrl = videoUrl; }
}