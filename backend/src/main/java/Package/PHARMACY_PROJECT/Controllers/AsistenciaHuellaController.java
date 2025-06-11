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

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/asistencia/huella")
public class AsistenciaHuellaController {

    private static final Logger logger = LoggerFactory.getLogger(AsistenciaHuellaController.class);

    @Autowired
    private Empleado_Services empleadoServices;

    @Autowired
    private Asistencia_Services asistenciaServices;

    @PostMapping("/entrada/{huella}")
    public ResponseEntity<Response<Asistencia_Model>> registrarPorHuella(@PathVariable String huella) {
        try {
            Optional<Empleado_Model> empleadoOpt = empleadoServices.findByHuellaDactilarWithHorario(huella);

            if (!empleadoOpt.isPresent()) {
                logger.warn("Empleado no encontrado para huella: " + huella);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new Response<>("404", "Empleado no encontrado", null, "EMPLEADO_NO_ENCONTRADO"));
            }

            Empleado_Model empleado = empleadoOpt.get();
            if (!empleado.isActivo()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new Response<>("400", "Empleado inactivo", null, "EMPLEADO_INACTIVO"));
            }

            LocalDate fechaActual = LocalDate.now(ZoneId.of("America/Bogota"));
            LocalTime horaActual = LocalTime.now(ZoneId.of("America/Bogota"));

            LocalTime entrada1 = empleado.getHorario().getHoraInicio1();
            LocalTime entrada2 = empleado.getHorario().getHoraInicio2();

            long minutos1 = Duration.between(entrada1, horaActual).toMinutes();
            long minutos2 = Duration.between(entrada2, horaActual).toMinutes();

            String tipoRegistro;
            LocalTime horaReferencia;

            if (minutos1 >= 0 && (minutos1 <= minutos2 || minutos2 < 0)) {
                tipoRegistro = "ENTRADA_1";
                horaReferencia = entrada1;
            } else if (minutos2 >= 0) {
                tipoRegistro = "ENTRADA_2";
                horaReferencia = entrada2;
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new Response<>("400", "Fuera del horario de entrada", null, "FUERA_DE_HORARIO"));
            }

            // Evitar duplicado
            Optional<Asistencia_Model> asistenciaExistente = asistenciaServices.findByEmpleadoAndFechaAndTipoRegistro(empleado, fechaActual, tipoRegistro);
            if (asistenciaExistente.isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new Response<>("409", "Ya se registró esta entrada", null, "ENTRADA_DUPLICADA"));
            }

            // Evaluar diferencia y estado
            String diferencia = new Asistencia_Model().calcularDiferenciaTiempoEntrada(horaReferencia, horaActual);

            String estado;
            if (diferencia.toLowerCase().contains("tarde")) {
                int minutosTarde = extraerMinutosDesdeTexto(diferencia.toLowerCase());
                estado = (minutosTarde > 5) ? "Tarde" : "Presente";
            } else {
                estado = "Presente";
            }

            Asistencia_Model nuevaAsistencia = new Asistencia_Model(empleado, fechaActual, horaActual, estado, tipoRegistro);
            nuevaAsistencia.setDiferenciaTiempoEntrada(diferencia);

            Asistencia_Model guardada = asistenciaServices.save(nuevaAsistencia);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new Response<>("201", "Asistencia registrada", guardada, "ASISTENCIA_REGISTRADA"));

        } catch (Exception e) {
            logger.error("Error al registrar asistencia por huella", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response<>("500", "Error interno", null, "ERROR_INTERNO"));
        }
    }

    private int extraerMinutosDesdeTexto(String texto) {
        Pattern pattern = Pattern.compile("(\\d+) minuto");
        Matcher matcher = pattern.matcher(texto);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        return 0;
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Controlador de huella activo");
    }
}
