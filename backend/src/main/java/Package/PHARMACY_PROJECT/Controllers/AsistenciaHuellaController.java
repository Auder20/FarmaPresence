package Package.PHARMACY_PROJECT.Controllers;

import Package.PHARMACY_PROJECT.Models.Asistencia_Model;
import Package.PHARMACY_PROJECT.Models.Empleado_Model;
import Package.PHARMACY_PROJECT.Response;
import Package.PHARMACY_PROJECT.Services.Asistencia_Services;
import Package.PHARMACY_PROJECT.Services.Empleado_Services;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/asistencia/huella")
public class AsistenciaHuellaController {

    private static final Logger logger = LoggerFactory.getLogger(AsistenciaHuellaController.class);

    @Autowired
    private Asistencia_Services asistenciaServices;

    @Autowired
    private Empleado_Services empleadoServices;

    // Método para registrar entrada usando huella
    @PostMapping("/entrada/{huella}")
    public ResponseEntity<Response<Asistencia_Model>> registrarEntrada(@PathVariable String huella) {
        Optional<Empleado_Model> empleadoOptional = empleadoServices.findByHuellaDactilar(huella);

        if (!empleadoOptional.isPresent()) {
            logger.error("Empleado no encontrado para la huella: " + huella);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response<>("404", "Empleado no encontrado", null, "EMPLEADO_NO_ENCONTRADO"));
        }

        Empleado_Model empleado = empleadoOptional.get();

        if (!empleado.isActivo()) {
            logger.error("Empleado no activo: " + empleado.getNombre());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new Response<>("400", "Empleado no activo", null, "EMPLEADO_INACTIVO"));
        }

        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("America/Bogota"));
        LocalDate fechaActual = now.toLocalDate();
        LocalTime horaEntradaActual = now.toLocalTime();

        logger.info(String.valueOf(horaEntradaActual));

        Optional<Asistencia_Model> asistencia1Optional = asistenciaServices.findByEmpleadoAndFechaAndTipoRegistro(empleado, fechaActual, "ENTRADA_1");
        if (!asistencia1Optional.isPresent()) {
            String estadoEntrada1 = calcularEstadoEntrada(empleado, horaEntradaActual, 1);
            Asistencia_Model asistencia1 = new Asistencia_Model(empleado, fechaActual, horaEntradaActual, estadoEntrada1, "ENTRADA_1");

            String diferenciaEntrada1 = asistencia1.calcularDiferenciaTiempoEntrada(empleado.getHorario().getHoraInicio1(), horaEntradaActual);
            asistencia1.setDiferenciaTiempoEntrada(diferenciaEntrada1);

            asistenciaServices.save(asistencia1);
            logger.info("Entrada registrada para el primer bloque de horario del empleado: " + empleado.getNombre());
        } else {
            logger.warn("La entrada para el primer bloque ya fue registrada.");
        }

        if (empleado.getHorario().getHoraInicio2() != null) {
            Optional<Asistencia_Model> asistencia2Optional = asistenciaServices.findByEmpleadoAndFechaAndTipoRegistro(empleado, fechaActual, "ENTRADA_2");

            if (!asistencia2Optional.isPresent()) {
                Optional<Asistencia_Model> ultimaAsistencia = asistenciaServices.findUltimaAsistenciaRegistrada(empleado);
                if (ultimaAsistencia.isPresent()) {
                    LocalTime ultimaHoraEntrada = ultimaAsistencia.get().getHoraEntrada();
                    if (ultimaHoraEntrada != null && ChronoUnit.MINUTES.between(ultimaHoraEntrada, horaEntradaActual) < 10) {
                        logger.error("Las entradas no pueden registrarse con menos de 10 minutos de diferencia.");
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(new Response<>("400", "Intento de doble registro en un intervalo corto", null, "REGISTRO_DUPLICADO"));
                    }
                }

                String estadoEntrada2 = calcularEstadoEntrada(empleado, horaEntradaActual, 2);
                Asistencia_Model asistencia2 = new Asistencia_Model(empleado, fechaActual, horaEntradaActual, estadoEntrada2, "ENTRADA_2");

                String diferenciaEntrada2 = asistencia2.calcularDiferenciaTiempoEntrada(empleado.getHorario().getHoraInicio2(), horaEntradaActual);
                asistencia2.setDiferenciaTiempoEntrada(diferenciaEntrada2);

                asistenciaServices.save(asistencia2);
                logger.info("Entrada registrada para el segundo bloque de horario del empleado: " + empleado.getNombre());
            } else {
                logger.warn("La entrada para el segundo bloque ya fue registrada.");
            }
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(new Response<>("200", "Entrada registrada", null, "ENTRADA_REGISTRADA"));
    }

    public String calcularEstadoEntrada(Empleado_Model empleado, LocalTime horaEntrada, int bloque) {
        LocalTime horaBloque = (bloque == 1) ? empleado.getHorario().getHoraInicio1() : empleado.getHorario().getHoraInicio2();
        if (horaBloque == null) return "INVALIDO";

        long diferenciaMinutos = ChronoUnit.MINUTES.between(horaBloque, horaEntrada);

        if (bloque == 1 && horaBloque.equals(LocalTime.of(7, 0))) {
            if (diferenciaMinutos <= 10) return "PUNTUAL";
            return "TARDE";
        }

        return (diferenciaMinutos <= 0) ? "PUNTUAL" : "TARDE";
    }
}
