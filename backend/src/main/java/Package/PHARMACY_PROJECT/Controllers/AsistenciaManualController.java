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

import java.util.Optional;
import java.util.Map;
import java.util.HashMap;
import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalTime;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

@RestController
@RequestMapping("/asistencia/manual")
public class AsistenciaManualController {

    private static final Logger logger = LoggerFactory.getLogger(AsistenciaManualController.class);

    @Autowired
    private Asistencia_Services asistenciaServices;

    @Autowired
    private Empleado_Services empleadoServices;

    @GetMapping("/evaluar-hora-entrada/{empleadoId}")
    public ResponseEntity<Map<String, String>> evaluarHoraEntrada(@PathVariable Long empleadoId) {
        try {
            Optional<Empleado_Model> empleadoOpt = empleadoServices.findById(empleadoId);

            if (!empleadoOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            Empleado_Model empleado = empleadoOpt.get();
            LocalTime entrada1 = empleado.getHorario().getHoraInicio1();
            LocalTime entrada2 = empleado.getHorario().getHoraInicio2();
            LocalTime ahora = LocalTime.now();

            long minutos1 = Duration.between(entrada1, ahora).toMinutes();
            long minutos2 = Duration.between(entrada2, ahora).toMinutes();

            long minutosDiferencia;
            String franjaUsada;
            if (minutos1 >= 0 && (minutos1 <= minutos2 || minutos2 < 0)) {
                minutosDiferencia = minutos1;
                franjaUsada = "Mañana";
            } else if (minutos2 >= 0) {
                minutosDiferencia = minutos2;
                franjaUsada = "Tarde";
            } else {
                minutosDiferencia = Math.min(Math.abs(minutos1), Math.abs(minutos2));
                franjaUsada = "Aún no es hora de entrada";
            }

            String estado;
            String diferencia;

            if (minutosDiferencia <= 0) {
                estado = "Presente";
                diferencia = "Temprano para la franja de " + franjaUsada;
            } else if (minutosDiferencia <= 5) {
                estado = "Presente";
                diferencia = "Tarde por " + minutosDiferencia + " minutos (tolerancia) en la franja de " + franjaUsada;
            } else {
                estado = "Tarde";
                diferencia = "Tarde por " + minutosDiferencia + " minutos en la franja de " + franjaUsada;
            }

            Map<String, String> resultado = new HashMap<>();
            resultado.put("estado", estado);
            resultado.put("diferencia", diferencia);
            return ResponseEntity.ok(resultado);

        } catch (Exception e) {
            e.printStackTrace(); // útil para depurar
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/registrarIngreso")
    public ResponseEntity<Response<Asistencia_Model>> registrarAsistencia(@RequestBody Asistencia_Model asistencia) {
        try {
            Long empleadoId = asistencia.getEmpleado().getId();
            Optional<Empleado_Model> empleadoOpt = empleadoServices.findById(empleadoId);

            if (!empleadoOpt.isPresent()) {
                logger.error("Empleado no encontrado con ID: " + empleadoId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new Response<>("404", "Empleado no encontrado", null, "EMPLEADO_NO_ENCONTRADO"));
            }

            Empleado_Model empleado = empleadoOpt.get();
            if (!empleado.isActivo()) {
                logger.error("Empleado no activo: " + empleado.getNombre());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new Response<>("400", "Empleado no activo", null, "EMPLEADO_INACTIVO"));
            }

            asistencia.setEmpleado(empleado);

            // Calcular la diferencia de tiempo antes de guardar
            LocalTime horaReferenciaEntrada1 = empleado.getHorario().getHoraInicio1();
            LocalTime horaReferenciaEntrada2 = empleado.getHorario().getHoraInicio2();

            String diferencia = asistencia.calcularDiferenciaTiempoEntrada(horaReferenciaEntrada1, horaReferenciaEntrada2);

            // Si el estado es "Ausente", no se calcula nada más
            if ("Ausente".equalsIgnoreCase(asistencia.getEstado())) {
                asistencia.setDiferenciaTiempoEntrada(null);
                asistencia.setEstado("Ausente");
            } else {
                asistencia.setDiferenciaTiempoEntrada(diferencia);

                if (diferencia != null && diferencia.toLowerCase().contains("tarde")) {
                    int minutosTarde = extraerMinutosDesdeTexto(diferencia.toLowerCase());
                    if (minutosTarde > 5) {
                        asistencia.setEstado("Tarde");
                    } else {
                        asistencia.setEstado("Presente");
                    }
                } else {
                    // Si no llegó tarde, se marca como presente
                    asistencia.setEstado("Presente");
                }
            }

            Asistencia_Model asistenciaGuardada = asistenciaServices.save(asistencia);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new Response<>("201", "Asistencia registrada con éxito", asistenciaGuardada, "ASISTENCIA_REGISTRADA"));

        } catch (Exception e) {
            logger.error("Error al registrar asistencia", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response<>("500", "Error interno al registrar asistencia", null, "ERROR_INTERNO"));
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
        return ResponseEntity.ok("Controlador manual funciona");
    }
}
