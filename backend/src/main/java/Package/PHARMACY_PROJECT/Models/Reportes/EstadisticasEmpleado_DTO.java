package Package.PHARMACY_PROJECT.Models.Reportes;

import Package.PHARMACY_PROJECT.Models.Empleado_Model;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EstadisticasEmpleado_DTO {
    private String empleadoNombre;
    private String totalTarde; // Total de tardanzas en formato "minutos y segundos"
    private int llegadasTarde; // Número de llegadas tarde
    private String totalPuntual; // Total de llegadas puntuales
    private int llegadasPuntuales; // Número de llegadas puntuales

    // Getters y setters manuales para asegurar compatibilidad
    public String getEmpleadoNombre() {
        return empleadoNombre;
    }

    public void setEmpleadoNombre(String empleadoNombre) {
        this.empleadoNombre = empleadoNombre;
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

    public String getTotalPuntual() {
        return totalPuntual;
    }

    public void setTotalPuntual(String totalPuntual) {
        this.totalPuntual = totalPuntual;
    }

    public int getLlegadasPuntuales() {
        return llegadasPuntuales;
    }

    public void setLlegadasPuntuales(int llegadasPuntuales) {
        this.llegadasPuntuales = llegadasPuntuales;
    }
}
