package Package.PHARMACY_PROJECT.Models;

import Package.PHARMACY_PROJECT.Controllers.Asistencia_Controller;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import static Package.PHARMACY_PROJECT.Controllers.Asistencia_Controller.*;

@Entity
@Table(name = "asistencias")
@Getter
@Setter
public class Asistencia_Model {

    private static final Logger logger = LoggerFactory.getLogger(Asistencia_Model.class);

    public static final int RANGO_TEMPRANO = -10; // 10 minutos antes
    public static final int RANGO_TARDE = 10;     // 10 minutos después

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // ID autoincremental de la asistencia

    @ManyToOne
    @JoinColumn(name = "empleado_id", nullable = false)
    private Empleado_Model empleado; // Relación con la tabla Empleados

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha; // Fecha del registro de asistencia

    @Column(name = "hora_entrada")
    private LocalTime horaEntrada; // Hora de entrada

    @Column(name = "hora_salida")
    private LocalTime horaSalida; // Hora de salida

    @Column(name = "estado", nullable = false, length = 20)
    private String estado; // Estado de la asistencia (temprano, puntual, tarde)

    @Column(name = "tipoRegistro", nullable = false, length = 20)
    private String tipoRegistro; // Tipo de registro (ENTRADA_1, ENTRADA_2, etc.)

    @Column(name = "diferencia_tiempo_entrada", nullable = true)
    private String diferenciaTiempoEntrada; // Diferencia de tiempo, no persistente en la base de datos

    @Column(name = "diferencia_tiempo_salida", nullable = true)
    private String diferenciaTiempoSalida; // Diferencia de tiempo, no persistente en la base de datos

    @Column(name = "motivo", nullable = true, length = 255)
    private String motivo; // Motivo para estados como Tarde o Ausente

    // Constructor vacío
    public Asistencia_Model() {}

    // Constructor para registrar entrada
    public Asistencia_Model(Empleado_Model empleado, LocalDate fecha, LocalTime horaEntrada, String estado, String tipoRegistro) {
        this.empleado = empleado;
        this.fecha = fecha;
        this.horaEntrada = horaEntrada;
        this.estado = estado;
        this.tipoRegistro = tipoRegistro;
    }

    // Constructor para registrar salida
    public Asistencia_Model(Empleado_Model empleado, LocalDate fecha, LocalTime horaEntrada, LocalTime horaSalida, String estado, String tipoRegistro) {
        this.empleado = empleado;
        this.fecha = fecha;
        this.horaEntrada = horaEntrada;
        this.horaSalida = horaSalida;
        this.estado = estado;
        this.tipoRegistro = tipoRegistro;
    }

    // Calcula diferencia de tiempo para entrada con dos referencias (ejemplo: turno 1 y turno 2)
    public String calcularDiferenciaTiempoEntrada(LocalTime horaReferenciaEntrada1, LocalTime horaReferenciaEntrada2) {
        if (horaEntrada != null) {
            // Elegir la referencia más cercana a horaEntrada para comparar
            long diff1 = Math.abs(ChronoUnit.SECONDS.between(horaReferenciaEntrada1, horaEntrada));
            long diff2 = Math.abs(ChronoUnit.SECONDS.between(horaReferenciaEntrada2, horaEntrada));
            if (diff1 <= diff2) {
                return calcularDiferencia(horaReferenciaEntrada1, horaEntrada);
            } else {
                return calcularDiferencia(horaReferenciaEntrada2, horaEntrada);
            }
        }
        return "No disponible";
    }

    // Calcula diferencia de tiempo para salida con dos referencias
    public String calcularDiferenciaTiempoSalida(LocalTime horaReferenciaSalida1, LocalTime horaReferenciaSalida2) {
        if (horaSalida != null) {
            long diff1 = Math.abs(ChronoUnit.SECONDS.between(horaReferenciaSalida1, horaSalida));
            long diff2 = Math.abs(ChronoUnit.SECONDS.between(horaReferenciaSalida2, horaSalida));
            if (diff1 <= diff2) {
                return calcularDiferenciaSalida(horaSalida, horaReferenciaSalida1);
            } else {
                return calcularDiferenciaSalida(horaSalida, horaReferenciaSalida2);
            }
        }
        return "No disponible";
    }

    // Método privado para calcular la diferencia de tiempo para entrada
    private String calcularDiferencia(LocalTime referencia, LocalTime actual) {
        long diferenciaSegundos = ChronoUnit.SECONDS.between(referencia, actual);

        long horas = Math.abs(diferenciaSegundos) / 3600;
        long minutos = (Math.abs(diferenciaSegundos) % 3600) / 60;
        long segundos = Math.abs(diferenciaSegundos) % 60;

        if (diferenciaSegundos < RANGO_TEMPRANO * 60) { // Temprano
            return horas > 0 ? "Temprano por " + horas + " hora(s), " + minutos + " minuto(s) y " + segundos + " segundo(s)"
                    : minutos > 0 ? "Temprano por " + minutos + " minuto(s) y " + segundos + " segundo(s)"
                    : "Temprano por " + segundos + " segundo(s)";
        } else if (diferenciaSegundos > RANGO_TARDE * 60) { // Tarde
            return horas > 0 ? "Tarde por " + horas + " hora(s), " + minutos + " minuto(s) y " + segundos + " segundo(s)"
                    : minutos > 0 ? "Tarde por " + minutos + " minuto(s) y " + segundos + " segundo(s)"
                    : "Tarde por " + segundos + " segundo(s)";
        } else { // Puntual
            return "Puntual";
        }
    }

    // Método privado para calcular la diferencia de tiempo para salida
    private String calcularDiferenciaSalida(LocalTime actual, LocalTime referencia) {
        long diferenciaSegundos = ChronoUnit.SECONDS.between(referencia, actual);

        long horas = Math.abs(diferenciaSegundos) / 3600;
        long minutos = (Math.abs(diferenciaSegundos) % 3600) / 60;
        long segundos = Math.abs(diferenciaSegundos) % 60;

        if (diferenciaSegundos < RANGO_TEMPRANO * 60) { // Salida temprana
            return horas > 0 ? "Salida temprana por " + horas + " hora(s), " + minutos + " minuto(s) y " + segundos + " segundo(s)"
                    : minutos > 0 ? "Salida temprana por " + minutos + " minuto(s) y " + segundos + " segundo(s)"
                    : "Salida temprana por " + segundos + " segundo(s)";
        } else if (diferenciaSegundos > RANGO_TARDE * 60) { // Salida tarde
            return horas > 0 ? "Salida tarde por " + horas + " hora(s), " + minutos + " minuto(s) y " + segundos + " segundo(s)"
                    : minutos > 0 ? "Salida tarde por " + minutos + " minuto(s) y " + segundos + " segundo(s)"
                    : "Salida tarde por " + segundos + " segundo(s)";
        } else { // Puntual
            return "Salida puntual";
        }
    }
// Calcula la diferencia entre dos horas dadas para entradas
public String calcularDiferenciaTiempoEntrada(LocalTime horaEsperada, LocalTime horaReal) {
    if (horaEsperada == null || horaReal == null) {
        return "Hora no disponible";
    }

    long diferenciaMinutos = ChronoUnit.MINUTES.between(horaEsperada, horaReal);

    if (diferenciaMinutos < 0) {
        return "Temprano por " + Math.abs(diferenciaMinutos) + " minutos";
    } else if (diferenciaMinutos == 0) {
        return "Exacto a la hora";
    } else if (diferenciaMinutos <= 5) {
        return "Tarde (tolerancia) por " + diferenciaMinutos + " minutos";
    } else {
        return "Tarde por " + diferenciaMinutos + " minutos";
    }
}

}
