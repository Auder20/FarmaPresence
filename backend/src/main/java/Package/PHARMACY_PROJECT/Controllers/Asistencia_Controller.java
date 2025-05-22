package Package.PHARMACY_PROJECT.Controllers;

import Package.PHARMACY_PROJECT.Models.Asistencia_Model;
import Package.PHARMACY_PROJECT.Services.Asistencia_Services;
import Package.PHARMACY_PROJECT.Services.InformeAsistencia_PDF_Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/asistencia")
public class Asistencia_Controller {

    @Autowired
    private Asistencia_Services asistenciaServices;

    @Autowired
    private InformeAsistencia_PDF_Services informeAsistenciaPDFServices;

    // Nuevo endpoint para reporte PDF de cumplimiento horario
   @GetMapping(value = "/reporteCumplimientoHorario", produces = MediaType.APPLICATION_PDF_VALUE)
public ResponseEntity<byte[]> reporteCumplimientoHorarioPDF(
        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fechaInicio,
        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fechaFin) {

    try {
        Date inicio = java.sql.Date.valueOf(fechaInicio);
        Date fin = java.sql.Date.valueOf(fechaFin);

        List<Asistencia_Model> asistencias = asistenciaServices.obtenerCumplimientoHorario(inicio, fin);

        // Usa aquí tu método en InformeAsistencia_PDF_Services que genere el PDF según tus necesidades
        byte[] pdfBytes = informeAsistenciaPDFServices.generateEmployeeAttendancePdf(
            // Necesitarás pasar el empleado o adaptar según tu lógica
            null,  // O pasa un empleado si aplica
            asistencias,
            null   // O mes, si quieres filtrar por mes dentro del PDF
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename("reporte_cumplimiento_horario.pdf")
                .build());

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);

    } catch (Exception e) {
        logger.error("Error generando reporte PDF cumplimiento horario", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}

    private static final Logger logger = LoggerFactory.getLogger(Asistencia_Controller.class);

    public static final int RANGO_TEMPRANO = -10; // 10 minutos antes
    public static final int RANGO_TARDE = 10;     // 10 minutos después
    public static final LocalTime HORA_REFERENCIA_ENTRADA = LocalTime.of(7, 0); // 7 am para entrada
    public static final LocalTime HORA_REFERENCIA_SALIDA = LocalTime.of(19, 0); // 7 pm para salida

    @Autowired
    private Asistencia_Services asistenciaServices;

    @Autowired
    private Empleado_Services empleadoServices;

    @Autowired
    private Horario_Services horarioServices;

    @Autowired
    private InformeAsistencia_PDF_Services informeAsistenciaPDFServices;

    @GetMapping(value = "/reporteCumplimientoGeneral", produces = MediaType.APPLICATION_PDF_VALUE)
public ResponseEntity<byte[]> reporteCumplimientoGeneralPDF(
        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fechaInicio,
        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fechaFin) {

    try {
        // Convertir LocalDate a Date si tu servicio usa java.util.Date
        Date inicio = java.sql.Date.valueOf(fechaInicio);
        Date fin = java.sql.Date.valueOf(fechaFin);

        // Obtener la lista filtrada de asistencias
        List<Asistencia_Model> asistencias = asistenciaServices.obtenerCumplimientoHorario(inicio, fin);

        // Generar PDF usando el nuevo método que creamos
        byte[] pdfBytes = informeAsistenciaPDFServices.generarReporteCumplimientoGeneralPdf(asistencias);

        // Configurar headers para descarga del PDF
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename("reporte_cumplimiento_general.pdf")
                .build());

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);

    } catch (Exception e) {
        logger.error("Error generando reporte general PDF de cumplimiento horario", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}


    // Obtener todas las asistencias
    @GetMapping("/todas")
    public ResponseEntity<Response<List<Asistencia_Model>>> obtenerTodasLasAsistencias() {
        try {
            List<Asistencia_Model> asistencias = asistenciaServices.findAll();
            Response<List<Asistencia_Model>> response = new Response<>("200", "Asistencias obtenidas satisfactoriamente", asistencias, "ASISTENCIAS_OBTENIDAS");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error al obtener todas las asistencias", e);
            Response<List<Asistencia_Model>> response = new Response<>("500", "Error al obtener las asistencias", null, "ERROR_DB");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Eliminar asistencia por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Response<Void>> deleteAsistencia(@PathVariable Long id) {
        try {
            asistenciaServices.deleteById(id);
            Response<Void> response = new Response<>("200", "Asistencia eliminada satisfactoriamente", null, "ASISTENCIA_DELETED");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error al eliminar la asistencia", e);
            Response<Void> response = new Response<>("500", "Error al eliminar la asistencia", null, "INTERNAL_SERVER_ERROR");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Eliminar todas las asistencias
    @DeleteMapping("/eliminar/todos")
    public ResponseEntity<Response<Void>> deleteAllAsistencias() {
        try {
            asistenciaServices.deleteAll();
            Response<Void> response = new Response<>("200", "Todas las asistencias eliminadas satisfactoriamente", null, "ALL_ASISTENCIAS_DELETED");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error al eliminar todas las asistencias: ", e);
            Response<Void> response = new Response<>("500", "Error al eliminar todas las asistencias", null, "INTERNAL_SERVER_ERROR");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Obtener asistencias por empleado
    @GetMapping("/empleado/{empleadoId}")
    public ResponseEntity<Response<List<Asistencia_Model>>> obtenerAsistenciasPorEmpleadoId(@PathVariable long empleadoId) {
        try {
            List<Asistencia_Model> asistencias = asistenciaServices.findByEmpleadoId(empleadoId);

            if (asistencias == null || asistencias.isEmpty()) {
                logger.warn("No se encontraron asistencias para el empleado con ID: " + empleadoId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new Response<>("404", "No se encontraron asistencias para el empleado", null, "ASISTENCIAS_NO_ENCONTRADAS"));
            }

            LocalTime horaReferenciaEntrada1 = LocalTime.of(8, 0);
            LocalTime horaReferenciaEntrada2 = LocalTime.of(9, 0);

            for (Asistencia_Model asistencia : asistencias) {
                try {
                    String diferenciaEntrada = asistencia.calcularDiferenciaTiempoEntrada(horaReferenciaEntrada1, horaReferenciaEntrada2);
                    asistencia.setDiferenciaTiempoEntrada(diferenciaEntrada);
                } catch (Exception ex) {
                    logger.error("Error al calcular diferencia para asistencia ID: " + asistencia.getId(), ex);
                    asistencia.setDiferenciaTiempoEntrada("Error");
                }
            }

            Response<List<Asistencia_Model>> response = new Response<>("200", "Asistencias obtenidas satisfactoriamente", asistencias, "ASISTENCIAS_OBTENIDAS");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error al obtener asistencias del empleado con ID: " + empleadoId, e);
            Response<List<Asistencia_Model>> response = new Response<>("500", "Error al obtener las asistencias", null, "ERROR_DB");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Obtener asistencias por empleado y mes
    @GetMapping("/empleado-mes/{empleadoId}/{mes}")
    public ResponseEntity<Response<List<Asistencia_Model>>> obtenerAsistenciasPorEmpleadoIdYMes(@PathVariable long empleadoId, @PathVariable int mes) {
        try {
            List<Asistencia_Model> asistenciasFiltradas = asistenciaServices.obtenerAsistenciasPorEmpleadoIdYMes(empleadoId, mes);

            if (asistenciasFiltradas.isEmpty()) {
                logger.warn("No se encontraron asistencias para el empleado con ID: " + empleadoId + " en el mes: " + mes);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new Response<>("404", "No se encontraron asistencias para el empleado en el mes especificado", null, "ASISTENCIAS_NO_ENCONTRADAS"));
            }

            Response<List<Asistencia_Model>> response = new Response<>("200", "Asistencias filtradas por mes obtenidas satisfactoriamente", asistenciasFiltradas, "ASISTENCIAS_FILTRADAS");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error al obtener asistencias del empleado con ID: " + empleadoId + " para el mes: " + mes, e);
            Response<List<Asistencia_Model>> response = new Response<>("500", "Error al obtener las asistencias", null, "ERROR_DB");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Reporte mensual general
    @GetMapping("/reporteMensual")
    public ResponseEntity<ReporteMensual_DTO> obtenerReporteMensual(@RequestParam Integer mes, @RequestParam Integer ano) {
        try {
            ReporteMensual_DTO reporte = asistenciaServices.obtenerReporteGeneralMensual(mes, ano);
            return ResponseEntity.ok(reporte);
        } catch (Exception e) {
            logger.error("Error al obtener reporte mensual", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Reporte mensual por empleado
    @GetMapping("/reporteEmpleadoMensual")
    public ResponseEntity<Response<ReporteEmpleado_DTO>> obtenerReporteEmpleadoMensual(
            @RequestParam Long empleadoId,
            @RequestParam Integer mes,
            @RequestParam Integer anio) {
        try {
            ReporteEmpleado_DTO reporteEmpleado = asistenciaServices.obtenerReporteEmpleadoMensual(empleadoId, mes, anio);
            Response<ReporteEmpleado_DTO> response = Response.success(reporteEmpleado);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            Response<ReporteEmpleado_DTO> response = Response.error("Parámetros inválidos: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

        } catch (NoSuchElementException e) {
            Response<ReporteEmpleado_DTO> response = Response.notFound("No se encontró el reporte solicitado.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);

        } catch (Exception e) {
            Response<ReporteEmpleado_DTO> response = Response.internalServerError("Error interno: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Reporte por empleado y fecha
    @GetMapping("/reporteEmpleadoFecha")
    public ResponseEntity<ReporteEmpleado_DTO> obtenerReporteEmpleadoFecha(
            @RequestParam Long empleadoId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fecha) {
        try {
            ReporteEmpleado_DTO reporteEmpleado = asistenciaServices.obtenerReporteEmpleadoFecha(empleadoId, fecha);
            return ResponseEntity.ok(reporteEmpleado);
        } catch (Exception e) {
            logger.error("Error al obtener reporte por fecha", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Datos para gráfica comparativa
    @GetMapping("/reporteComparativo/grafica")
    public ResponseEntity<Map<String, Object>> obtenerDatosGrafica(
            @RequestParam Integer mes,
            @RequestParam Integer anio) {
        try {
            Map<String, Object> datosGrafica = asistenciaServices.obtenerComparativaAsistencia(mes, anio);
            return ResponseEntity.ok(datosGrafica);
        } catch (Exception e) {
            logger.error("Error al generar gráfica comparativa", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al generar la gráfica", "details", e.getMessage()));
        }
    }
}
