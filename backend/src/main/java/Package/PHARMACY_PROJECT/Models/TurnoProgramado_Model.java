package Package.PHARMACY_PROJECT.Models;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "turnos_programados")
public class TurnoProgramado_Model {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // ID autoincremental

    @Column(name = "fecha", nullable = true)
    private LocalDate fecha;  // Fecha específica del turno

    @Column(name = "hora_inicio", nullable = true)
    private LocalTime horaInicio;  // Hora de inicio específica (puede coincidir con el horario)

    @Column(name = "hora_fin", nullable = true)
    private LocalTime horaFin;  // Hora de fin específica (puede coincidir con el horario)

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "empleado_id", nullable = false) // ajusta nullable según necesidad
    private Empleado_Model empleado;

    // Constructor vacío
    public TurnoProgramado_Model() {
    }

    public TurnoProgramado_Model(LocalTime horaFin, LocalDate fecha, LocalTime horaInicio) {
        this.horaFin = horaFin;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
    }

    // Métodos getter
    public Long getId() {
        return id;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public Empleado_Model getEmpleado() {
        return empleado;
    }

    // Métodos setter manuales para evitar problemas con Lombok
    public void setId(Long id) {
        this.id = id;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public void setHoraFin(LocalTime horaFin) {
        this.horaFin = horaFin;
    }

    public void setEmpleado(Empleado_Model empleado) {
        this.empleado = empleado;
    }
}