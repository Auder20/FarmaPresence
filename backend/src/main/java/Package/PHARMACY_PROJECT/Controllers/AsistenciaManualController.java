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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalTime;


@RestController
@RequestMapping("/asistencia/manual")
public class AsistenciaManualController {

    private static final Logger logger = LoggerFactory.getLogger(AsistenciaManualController.class);

    @Autowired
    private Asistencia_Services asistenciaServices;

    @Autowired
    private Empleado_Services empleadoServices;

   @PostMapping("/registrarIngreso")
public ResponseEntity<Response<Asistencia_Model>> registrarAsistencia(@RequestBody Asistencia_Model asistencia) {
    try {
        Long empleadoId = asistencia.getEmpleado().getId();
        Optional<Empleado_Model> empleadoOpt = empleadoServices.getEmpleadoById(empleadoId);

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
        asistencia.setDiferenciaTiempoEntrada(diferencia);

        Asistencia_Model asistenciaGuardada = asistenciaServices.save(asistencia);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new Response<>("201", "Asistencia registrada con éxito", asistenciaGuardada, "ASISTENCIA_REGISTRADA"));

    } catch (Exception e) {
        logger.error("Error al registrar asistencia", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new Response<>("500", "Error interno al registrar asistencia", null, "ERROR_INTERNO"));
    }
}


    @GetMapping("/test")
public ResponseEntity<String> test() {
    return ResponseEntity.ok("Controlador manual funciona");
}

}
