package com.jaimemoro.cornermanbox.infrastructure.persistence.mappers;

import com.jaimemoro.cornermanbox.core.domain.model.Entrenamiento;
import java.util.ArrayList;
import java.util.List;

public class EntrenamientoMapper {

    public Entrenamiento toDomain(com.jaimemoro.cornermanbox.data.entities.Entrenamiento entity) {
        if (entity == null) return null;

        Entrenamiento domain = new Entrenamiento();
        domain.setId(entity.id);
        domain.setFecha(entity.fecha);
        domain.setPuntosGanados(entity.puntosGanados);
        domain.setRoundsCompletados(entity.roundsCompletados);
        domain.setDuracionTotalSegundos(entity.duracionTotalSegundos);
        return domain;
    }

    public com.jaimemoro.cornermanbox.data.entities.Entrenamiento toEntity(Entrenamiento domain) {
        if (domain == null) return null;

        com.jaimemoro.cornermanbox.data.entities.Entrenamiento entity = new com.jaimemoro.cornermanbox.data.entities.Entrenamiento();
        entity.id = domain.getId();
        entity.fecha = domain.getFecha();
        entity.puntosGanados = domain.getPuntosGanados();
        entity.roundsCompletados = domain.getRoundsCompletados();
        entity.duracionTotalSegundos = domain.getDuracionTotalSegundos();
        return entity;
    }

    public List<Entrenamiento> toDomainList(List<com.jaimemoro.cornermanbox.data.entities.Entrenamiento> entities) {
        if (entities == null) return null;
        List<Entrenamiento> result = new ArrayList<>();
        for (com.jaimemoro.cornermanbox.data.entities.Entrenamiento entity : entities) {
            result.add(toDomain(entity));
        }
        return result;
    }

    public List<com.jaimemoro.cornermanbox.data.entities.Entrenamiento> toEntityList(List<Entrenamiento> domains) {
        if (domains == null) return null;
        List<com.jaimemoro.cornermanbox.data.entities.Entrenamiento> result = new ArrayList<>();
        for (Entrenamiento domain : domains) {
            result.add(toEntity(domain));
        }
        return result;
    }
}