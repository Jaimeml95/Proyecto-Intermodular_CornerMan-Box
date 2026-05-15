package com.jaimemoro.cornermanbox.infrastructure.persistence.mappers;

import com.jaimemoro.cornermanbox.core.domain.model.Tecnica;
import java.util.ArrayList;
import java.util.List;

public class TecnicaMapper {

    public Tecnica toDomain(com.jaimemoro.cornermanbox.data.entities.Tecnica entity) {
        if (entity == null) return null;

        Tecnica domain = new Tecnica();
        domain.setId(entity.id);
        domain.setNombre(entity.nombre);
        domain.setDescripcion(entity.descripcion);
        domain.setCategoria(entity.categoria);
        domain.setImagenResId(entity.imagenResId);
        domain.setVideoUrl(entity.videoUrl);
        return domain;
    }

    public com.jaimemoro.cornermanbox.data.entities.Tecnica toEntity(Tecnica domain) {
        if (domain == null) return null;

        com.jaimemoro.cornermanbox.data.entities.Tecnica entity = new com.jaimemoro.cornermanbox.data.entities.Tecnica();
        entity.id = domain.getId();
        entity.nombre = domain.getNombre();
        entity.descripcion = domain.getDescripcion();
        entity.categoria = domain.getCategoria();
        entity.imagenResId = domain.getImagenResId();
        entity.videoUrl = domain.getVideoUrl();
        return entity;
    }

    public List<Tecnica> toDomainList(List<com.jaimemoro.cornermanbox.data.entities.Tecnica> entities) {
        if (entities == null) return null;
        List<Tecnica> result = new ArrayList<>();
        for (com.jaimemoro.cornermanbox.data.entities.Tecnica entity : entities) {
            result.add(toDomain(entity));
        }
        return result;
    }

    public List<com.jaimemoro.cornermanbox.data.entities.Tecnica> toEntityList(List<Tecnica> domains) {
        if (domains == null) return null;
        List<com.jaimemoro.cornermanbox.data.entities.Tecnica> result = new ArrayList<>();
        for (Tecnica domain : domains) {
            result.add(toEntity(domain));
        }
        return result;
    }
}