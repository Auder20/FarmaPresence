package com.farmapresence.repository;

import com.farmapresence.models.Asistencia_Model;
import com.farmapresence.models.Empleado_Model;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface Asistencia_Repository extends JpaRepository<Asistencia_Model, Long> {

    /** Busca todas las asistencias entre dos fechas */
    List<Asistencia_Model> findByFechaBetween(Date fechaInicio, Date fechaFin);

    /** Reinicia el AUTO_INCREMENT de la tabla */
    @Modifying
    @Transactional
    @Query(value = "ALTER TABLE asistencias AUTO_INCREMENT = 1", nativeQuery = true)
    void resetAutoIncrement();

    /** Encuentra todas las asistencias de un empleado */
    List<Asistencia_Model> findByEmpleadoId(Long empleadoId);

    /** Encuentra asistencias de un empleado en una fecha concreta */
    Optional<Asistencia_Model> findByEmpleadoAndFecha(Empleado_Model empleado, LocalDate fecha);

    /** Encuentra asistencias de un empleado en una fecha concreta y tipo de registro */
    Optional<Asistencia_Model> findByEmpleadoAndFechaAndTipoRegistro(Empleado_Model empleado, LocalDate fecha, String tipoRegistro);

    /** Variante para buscar por empleado y fecha */
    List<Asistencia_Model> findByEmpleadoIdAndFecha(Long empleadoId, LocalDate fecha);

    /** Devuelve la última asistencia registrada de un empleado */
    Optional<Asistencia_Model> findTopByEmpleadoOrderByHoraEntradaDesc(Empleado_Model empleado);

    /** Busca todas las asistencias cuyo mes de la fecha sea el indicado */
    @Query("SELECT a FROM Asistencia_Model a WHERE MONTH(a.fecha) = :mes")
    List<Asistencia_Model> findByMes(@Param("mes") Integer mes);

}
