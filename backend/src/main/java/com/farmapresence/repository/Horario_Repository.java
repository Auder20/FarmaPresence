package com.farmapresence.repository;

import com.farmapresence.models.Horario_Model;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface Horario_Repository extends JpaRepository<Horario_Model, Long> {
    // Método para obtener un horario por su nombre
    List<Horario_Model> findByHoraInicio1OrHoraFin1(LocalTime horaInicio1, LocalTime horaFin1);
    
    // Método para obtener un horario por descripción
    Optional<Horario_Model> findByDescripcion(String descripcion);
}
