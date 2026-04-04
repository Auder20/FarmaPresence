package com.farmapresence.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "asistencia")
@Getter
@Setter
public class Asistencia_Model {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Empleado_Model empleado;

    private LocalDate fecha;
    private LocalTime horaEntrada;
    private LocalTime horaSalida;

    private String estado; // ← corregido el nombre del campo

    private String tipoRegistro;

    @Transient
    private String diferenciaTiempoEntrada;

    @Transient
    private String diferenciaTiempoSalida;

    public Asistencia_Model() {}

    public Asistencia_Model(Empleado_Model empleado, LocalDate fecha, LocalTime horaEntrada, String estado, String tipoRegistro) {
        this.empleado = empleado;
        this.fecha = fecha;
        this.horaEntrada = horaEntrada;
        this.estado = estado;
        this.tipoRegistro = tipoRegistro;
    }

    // ✅ Estos métodos son NECESARIOS para que compile
    public String calcularDiferenciaTiempoEntrada(LocalTime horaEsperada, LocalTime horaReal) {
        long minutos = ChronoUnit.MINUTES.between(horaEsperada, horaReal);
        long horas = Math.abs(minutos) / 60;
        long minutosRestantes = Math.abs(minutos) % 60;
        String signo = (minutos < 0) ? "-" : "+";
        return signo + String.format("%02d:%02d", horas, minutosRestantes);
    }

    public String calcularDiferenciaTiempoSalida(LocalTime horaEsperada, LocalTime horaReal) {
        long minutos = ChronoUnit.MINUTES.between(horaEsperada, horaReal);
        long horas = Math.abs(minutos) / 60;
        long minutosRestantes = Math.abs(minutos) % 60;
        String signo = (minutos < 0) ? "-" : "+";
        return signo + String.format("%02d:%02d", horas, minutosRestantes);
    }

    // Getters y setters manuales para asegurar compatibilidad
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Empleado_Model getEmpleado() {
        return empleado;
    }

    public void setEmpleado(Empleado_Model empleado) {
        this.empleado = empleado;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public LocalTime getHoraEntrada() {
        return horaEntrada;
    }

    public void setHoraEntrada(LocalTime horaEntrada) {
        this.horaEntrada = horaEntrada;
    }

    public LocalTime getHoraSalida() {
        return horaSalida;
    }

    public void setHoraSalida(LocalTime horaSalida) {
        this.horaSalida = horaSalida;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getTipoRegistro() {
        return tipoRegistro;
    }

    public void setTipoRegistro(String tipoRegistro) {
        this.tipoRegistro = tipoRegistro;
    }

    public String getDiferenciaTiempoEntrada() {
        return diferenciaTiempoEntrada;
    }

    public void setDiferenciaTiempoEntrada(String diferenciaTiempoEntrada) {
        this.diferenciaTiempoEntrada = diferenciaTiempoEntrada;
    }

    public String getDiferenciaTiempoSalida() {
        return diferenciaTiempoSalida;
    }

    public void setDiferenciaTiempoSalida(String diferenciaTiempoSalida) {
        this.diferenciaTiempoSalida = diferenciaTiempoSalida;
    }
}
