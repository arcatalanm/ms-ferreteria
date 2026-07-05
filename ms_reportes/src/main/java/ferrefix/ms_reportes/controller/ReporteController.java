package ferrefix.ms_reportes.controller;

import ferrefix.ms_reportes.service.ReporteService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDate;

@Slf4j
@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final ReporteService reporteService;

    /**
     * Genera un reporte PDF con el historial de compras de un cliente específico por su RUN.
     */
    @GetMapping("/run/{run}")
    public void generarReporteVentasPorRun(@PathVariable String run, HttpServletResponse response) throws IOException {
        log.info("REST request para generar PDF de ventas para el RUN: {}", run);

        // 1. Configurar las cabeceras del protocolo HTTP
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=reporte_ventas_" + run + ".pdf");

        // 2. Delegar la lógica pesada a la capa Service
        try {
            reporteService.exportarVentasPorRun(run, response.getOutputStream());
        } catch (IllegalArgumentException e) {
            log.warn("No se pudo procesar la solicitud: {}", e.getMessage());
            response.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            log.error("Error crítico al generar reporte: ", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error interno al generar el documento.");
        }
    }

    /**
     * Genera un reporte PDF con el historial de ventas filtrado por rango de fechas y RUN de cliente opcional.
     */
    @GetMapping("/buscar")
    public void generarReporteVentasPorRango(
            @RequestParam(required = false) String runCliente,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            HttpServletResponse response) throws IOException {

        log.info("REST request para generar PDF de ventas por rango - RUN: {}, Inicio: {}, Fin: {}", runCliente, fechaInicio, fechaFin);

        // 1. Configurar las cabeceras del protocolo HTTP
        response.setContentType("application/pdf");
        String filename = "reporte_ventas_rango_" + (runCliente != null && !runCliente.isBlank() ? runCliente + "_" : "") + fechaInicio + "_a_" + fechaFin + ".pdf";
        response.setHeader("Content-Disposition", "attachment; filename=" + filename);

        // 2. Delegar la lógica pesada a la capa Service
        try {
            reporteService.exportarVentasPorRangoFechas(runCliente, fechaInicio, fechaFin, response.getOutputStream());
        } catch (IllegalArgumentException e) {
            log.warn("No se pudo procesar la solicitud: {}", e.getMessage());
            response.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            log.error("Error crítico al generar reporte por rango: ", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error interno al generar el documento.");
        }
    }
}
