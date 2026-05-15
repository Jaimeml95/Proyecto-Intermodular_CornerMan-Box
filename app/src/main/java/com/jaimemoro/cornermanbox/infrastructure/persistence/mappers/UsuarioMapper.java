package com.jaimemoro.cornermanbox.infrastructure.persistence.mappers;

import com.jaimemoro.cornermanbox.core.domain.model.Usuario;

public class UsuarioMapper {

    public Usuario toDomain(com.jaimemoro.cornermanbox.data.entities.Usuario entity) {
        if (entity == null) return null;

        Usuario domain = new Usuario();
        domain.setId(entity.id);
        domain.setTotalPoints(entity.totalPoints);
        domain.setDailyStreak(entity.dailyStreak);
        domain.setLastTrainingDate(entity.lastTrainingDate);
        domain.setNombre(entity.nombre);
        domain.setRoundDurationSeconds(entity.roundDurationSeconds);
        domain.setRestDurationSeconds(entity.restDurationSeconds);
        return domain;
    }

    public com.jaimemoro.cornermanbox.data.entities.Usuario toEntity(Usuario domain) {
        if (domain == null) return null;

        com.jaimemoro.cornermanbox.data.entities.Usuario entity = new com.jaimemoro.cornermanbox.data.entities.Usuario();
        entity.id = domain.getId();
        entity.totalPoints = domain.getTotalPoints();
        entity.dailyStreak = domain.getDailyStreak();
        entity.lastTrainingDate = domain.getLastTrainingDate();
        entity.nombre = domain.getNombre();
        entity.roundDurationSeconds = domain.getRoundDurationSeconds();
        entity.restDurationSeconds = domain.getRestDurationSeconds();
        return entity;
    }
}