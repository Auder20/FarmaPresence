package com.farmapresence.repository;

import com.farmapresence.models.TurnoProgramado_Model;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TurnoProgramado_Repository extends JpaRepository<TurnoProgramado_Model, Long> {
    // Método para obtener todos los turnos programados
    List<TurnoProgramado_Model> findAll();

    // Método para obtener turnos por fecha
    List<TurnoProgramado_Model> findByFecha(LocalDate fecha);
    
    // Método para obtener turno por empleado y fecha
    Optional<TurnoProgramado_Model> findByEmpleadoIdAndFecha(Long empleadoId, LocalDate fecha);
}

