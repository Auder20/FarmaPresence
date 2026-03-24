package Package.PHARMACY_PROJECT.Models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Table(name = "empleados")
@Getter
@Setter
public class Empleado_Model {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // ID autoincremental

    @Column(name = "nombre", length = 100, nullable = true)  // Asegúrate de que sea null
    private String nombre;  // Nombre completo del empleado

    @Column(name = "identificacion", length = 50, unique = true, nullable = true)  // Asegúrate de que sea null
    private String identificacion;  // Identificación del empleado

    @Column(name = "fecha_contratacion", nullable = true)  // Asegúrate de que sea null
    private LocalDate fechaContratacion;  // Fecha de contratación

    @Column(name = "activo", nullable = true)  // Asegúrate de que sea null
    private Boolean activo;  // Estado de si el empleado sigue trabajando

    @Column(name = "Rol", nullable = true)  // Ya permitido como null
    private String rol;

    @Column(name = "huella_dactilar", nullable = true)  // Ya permitido como null
    private String huellaDactilar;  // Huella dactilar del empleado

    @ManyToOne
    @JoinColumn(name = "horario_id", nullable = true)
    private Horario_Model horario;  // Horario asignado al turno

    @ManyToOne
    @JoinColumn(name = "turnoProgramado_id", nullable = true)
    private TurnoProgramado_Model turnoProgramado;  // Horario asignado al turno

    @Column(name = "telefono", length = 20, nullable = true)
    private String telefono;  // Teléfono del empleado

    // Constructor vacío
    public Empleado_Model() {
    }

    // Constructor con todos los atributos excepto turnos


    public Empleado_Model(Boolean activo, LocalDate fechaContratacion, Horario_Model horario, String huellaDactilar, String identificacion, String nombre, String rol, TurnoProgramado_Model turnoProgramado, String telefono) {
        this.activo = activo;
        this.fechaContratacion = fechaContratacion;
        this.horario = horario;
        this.huellaDactilar = huellaDactilar;
        this.identificacion = identificacion;
        this.nombre = nombre;
        this.rol = rol;
        this.turnoProgramado = turnoProgramado;
        this.telefono = telefono;
    }

    // Método para obtener el estado de "activo"
    public boolean isActivo() {
        return activo;
    }

    // Getters y setters manuales para asegurar compatibilidad
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getIdentificacion() {
        return identificacion;
    }

    public void setIdentificacion(String identificacion) {
        this.identificacion = identificacion;
    }

    public LocalDate getFechaContratacion() {
        return fechaContratacion;
    }

    public void setFechaContratacion(LocalDate fechaContratacion) {
        this.fechaContratacion = fechaContratacion;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getHuellaDactilar() {
        return huellaDactilar;
    }

    public void setHuellaDactilar(String huellaDactilar) {
        this.huellaDactilar = huellaDactilar;
    }

    public Horario_Model getHorario() {
        return horario;
    }

    public void setHorario(Horario_Model horario) {
        this.horario = horario;
    }

    public TurnoProgramado_Model getTurnoProgramado() {
        return turnoProgramado;
    }

    public void setTurnoProgramado(TurnoProgramado_Model turnoProgramado) {
        this.turnoProgramado = turnoProgramado;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

}
