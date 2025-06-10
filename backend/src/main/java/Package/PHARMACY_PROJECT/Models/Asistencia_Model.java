package Package.PHARMACY_PROJECT.Models;

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

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String getDiferenciaTiempoEntrada() {
        if (empleado != null && empleado.getHorario() != null && horaEntrada != null) {
            LocalTime horaEsperada = tipoRegistro.equals("ENTRADA_1") ?
                    empleado.getHorario().getHoraInicio1() :
                    empleado.getHorario().getHoraInicio2();
            return calcularDiferenciaTiempoEntrada(horaEsperada, horaEntrada);
        }
        return null;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String getDiferenciaTiempoSalida() {
        if (empleado != null && empleado.getHorario() != null && horaSalida != null) {
            LocalTime horaEsperada = tipoRegistro.equals("SALIDA_1") ?
                    empleado.getHorario().getHoraFin1() :
                    empleado.getHorario().getHoraFin2();
            return calcularDiferenciaTiempoSalida(horaEsperada, horaSalida);
        }
        return null;
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
}
