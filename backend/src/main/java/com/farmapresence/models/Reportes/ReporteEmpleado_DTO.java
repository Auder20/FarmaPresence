package com.farmapresence.models.Reportes;

import com.farmapresence.models.Asistencia_Model;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class ReporteEmpleado_DTO {
    private Long empleadoId;
    private String empleadoNombre;
    private int mes;
    private int año;
    private int totalAsistencias;
    private String totalTarde;
    private int llegadasTarde;
    private int llegadasPuntuales;
    private List<Asistencia_Model> asistencias; // Lista de detalles de las asistencias
    private LocalDate fecha;

    // Getters y setters manuales para asegurar compatibilidad
    public Long getEmpleadoId() {
        return empleadoId;
    }

    public void setEmpleadoId(Long empleadoId) {
        this.empleadoId = empleadoId;
    }

    public String getEmpleadoNombre() {
        return empleadoNombre;
    }

    public void setEmpleadoNombre(String empleadoNombre) {
        this.empleadoNombre = empleadoNombre;
    }

    public int getMes() {
        return mes;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }

    public int getAño() {
        return año;
    }

    public void setAño(int año) {
        this.año = año;
    }

    public int getTotalAsistencias() {
        return totalAsistencias;
    }

    public void setTotalAsistencias(int totalAsistencias) {
        this.totalAsistencias = totalAsistencias;
    }

    public String getTotalTarde() {
        return totalTarde;
    }

    public void setTotalTarde(String totalTarde) {
        this.totalTarde = totalTarde;
    }

    public int getLlegadasTarde() {
        return llegadasTarde;
    }

    public void setLlegadasTarde(int llegadasTarde) {
        this.llegadasTarde = llegadasTarde;
    }

    public int getLlegadasPuntuales() {
        return llegadasPuntuales;
    }

    public void setLlegadasPuntuales(int llegadasPuntuales) {
        this.llegadasPuntuales = llegadasPuntuales;
    }

    public List<Asistencia_Model> getAsistencias() {
        return asistencias;
    }

    public void setAsistencias(List<Asistencia_Model> asistencias) {
        this.asistencias = asistencias;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }
}





