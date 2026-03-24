package Package.PHARMACY_PROJECT.Models.Reportes;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class ReporteMensual_DTO {
    private int mes;
    private int año;
    private int totalAsistencias;
    private Map<String, Integer> asistenciasPorEstado; // Para contar puntuales y tardes
    private List<EstadisticasEmpleado_DTO> estadisticasPorEmpleado;
    private EstadisticasEmpleado_DTO empleadoConMayorTardanza;

    // Getters y setters manuales para asegurar compatibilidad
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

    public Map<String, Integer> getAsistenciasPorEstado() {
        return asistenciasPorEstado;
    }

    public void setAsistenciasPorEstado(Map<String, Integer> asistenciasPorEstado) {
        this.asistenciasPorEstado = asistenciasPorEstado;
    }

    public List<EstadisticasEmpleado_DTO> getEstadisticasPorEmpleado() {
        return estadisticasPorEmpleado;
    }

    public void setEstadisticasPorEmpleado(List<EstadisticasEmpleado_DTO> estadisticasPorEmpleado) {
        this.estadisticasPorEmpleado = estadisticasPorEmpleado;
    }

    public EstadisticasEmpleado_DTO getEmpleadoConMayorTardanza() {
        return empleadoConMayorTardanza;
    }

    public void setEmpleadoConMayorTardanza(EstadisticasEmpleado_DTO empleadoConMayorTardanza) {
        this.empleadoConMayorTardanza = empleadoConMayorTardanza;
    }
}
