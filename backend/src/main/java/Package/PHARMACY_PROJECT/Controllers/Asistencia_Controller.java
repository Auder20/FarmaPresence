package Package.PHARMACY_PROJECT.Controllers;

import Package.PHARMACY_PROJECT.Models.Asistencia_Model;
import Package.PHARMACY_PROJECT.Models.Reportes.ReporteEmpleado_DTO;
import Package.PHARMACY_PROJECT.Models.Reportes.ReporteMensual_DTO;
import Package.PHARMACY_PROJECT.Response;
import Package.PHARMACY_PROJECT.Services.Asistencia_Services;
import Package.PHARMACY_PROJECT.Services.Empleado_Services;
import Package.PHARMACY_PROJECT.Services.Horario_Services;
import Package.PHARMACY_PROJECT.Services.InformeAsistencia_PDF_Services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/asistencia")
public class Asistencia_Controller {

    private static final Logger logger = LoggerFactory.getLogger(Asistencia_Controller.class);

    // Parámetros de ejemplo para cálculo de diferencias (ajústalos si quieres)
    private static final LocalTime HORA_REFERENCIA_ENTRADA = LocalTime.of(8, 0);
    private static final LocalTime HORA_REFERENCIA_SALIDA   = LocalTime.of(18, 0);

    @Autowired
    private Asistencia_Services asistenciaServices;

    @Autowired
    private Empleado_Services empleadoServices;

    @Autowired
    private Horario_Services horarioServices;

    @Autowired
    private InformeAsistencia_PDF_Services informeAsistenciaPDFServices;

    /**  
     * Endpoint para descargar reporte general de cumplimiento horario en PDF  
     */
    @GetMapping(value = "/reporteCumplimientoGeneral", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> reporteCumplimientoGeneralPDF(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fechaFin) {
        try {
            Date inicio = java.sql.Date.valueOf(fechaInicio);
            Date fin    = java.sql.Date.valueOf(fechaFin);

            List<Asistencia_Model> asistencias = asistenciaServices.obtenerCumplimientoHorario(inicio, fin);

            byte[] pdfBytes = informeAsistenciaPDFServices.generarReporteCumplimientoGeneralPdf(asistencias);

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

    /**  
     * Devuelve todas las asistencias como JSON  
     */
    @GetMapping("/todas")
    public ResponseEntity<Response<List<Asistencia_Model>>> obtenerTodasLasAsistencias() {
        try {
            List<Asistencia_Model> asistencias = asistenciaServices.findAll();
            return ResponseEntity.ok(
                new Response<>("200", "Asistencias obtenidas satisfactoriamente", asistencias, "ASISTENCIAS_OBTENIDAS")
            );
        } catch (Exception e) {
            logger.error("Error al obtener todas las asistencias", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new Response<>("500", "Error al obtener las asistencias", null, "ERROR_DB"));
        }
    }

    /**  
     * Elimina una asistencia por su ID  
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Response<Void>> deleteAsistencia(@PathVariable Long id) {
        try {
            asistenciaServices.deleteById(id);
            return ResponseEntity.ok(new Response<>("200", "Asistencia eliminada satisfactoriamente", null, "ASISTENCIA_DELETED"));
        } catch (Exception e) {
            logger.error("Error al eliminar la asistencia", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new Response<>("500", "Error al eliminar la asistencia", null, "INTERNAL_SERVER_ERROR"));
        }
    }

    /**  
     * Obtiene asistencias de un empleado y calcula diferencias de entrada  
     */
    @GetMapping("/empleado/{empleadoId}")
    public ResponseEntity<Response<List<Asistencia_Model>>> obtenerAsistenciasPorEmpleado(
            @PathVariable long empleadoId) {
        try {
            List<Asistencia_Model> asistencias = asistenciaServices.findByEmpleadoId(empleadoId);
            if (asistencias.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response<>("404", "No se encontraron asistencias para el empleado", null, "ASISTENCIAS_NO_ENCONTRADAS"));
            }
            for (Asistencia_Model a : asistencias) {
                String diff = a.calcularDiferenciaTiempoEntrada(HORA_REFERENCIA_ENTRADA, HORA_REFERENCIA_SALIDA);
                a.setDiferenciaTiempoEntrada(diff);
            }
            return ResponseEntity.ok(new Response<>("200", "Asistencias obtenidas satisfactoriamente", asistencias, "ASISTENCIAS_OBTENIDAS"));
        } catch (Exception e) {
            logger.error("Error al obtener asistencias del empleado con ID: " + empleadoId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new Response<>("500", "Error al obtener las asistencias", null, "ERROR_DB"));
        }
    }

    /**  
     * Reporte mensual general como DTO JSON  
     */
    @GetMapping("/reporteMensual")
    public ResponseEntity<ReporteMensual_DTO> obtenerReporteMensual(
            @RequestParam Integer mes, @RequestParam Integer ano) {
        try {
            return ResponseEntity.ok(asistenciaServices.obtenerReporteGeneralMensual(mes, ano));
        } catch (Exception e) {
            logger.error("Error al obtener reporte mensual", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**  
     * Reporte mensual por empleado como DTO envuelto en Response  
     */
    @GetMapping("/reporteEmpleadoMensual")
    public ResponseEntity<Response<ReporteEmpleado_DTO>> obtenerReporteEmpleadoMensual(
            @RequestParam Long empleadoId,
            @RequestParam Integer mes,
            @RequestParam Integer anio) {
        try {
            ReporteEmpleado_DTO dto = asistenciaServices.obtenerReporteEmpleadoMensual(empleadoId, mes, anio);
            return ResponseEntity.ok(Response.success(dto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Response.error("Parámetros inválidos: " + e.getMessage()));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Response.notFound("No se encontró el reporte solicitado."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Response.internalServerError("Error interno: " + e.getMessage()));
        }
    }

    /**  
     * Datos para la gráfica comparativa  
     */
    @GetMapping("/reporteComparativo/grafica")
    public ResponseEntity<Map<String, Object>> obtenerDatosGrafica(
            @RequestParam Integer mes, @RequestParam Integer anio) {
        try {
            return ResponseEntity.ok(asistenciaServices.obtenerComparativaAsistencia(mes, anio));
        } catch (Exception e) {
            logger.error("Error al generar gráfica comparativa", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al generar la gráfica", "details", e.getMessage()));
        }
    }
}
