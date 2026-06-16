package ferrefix.ms_reportes.controller;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import ferrefix.ms_reportes.client.VentaClient;
import ferrefix.ms_reportes.dto.VentaDTO;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.awt.Color;
import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final VentaClient ventaClient;

    @GetMapping("/run/{run}")
    public void generarReporteVentasPorRun(@PathVariable String run, HttpServletResponse response) throws IOException {
        log.info("Iniciando generación de PDF de ventas para el RUN: {}", run);

        // 1. Rescatar datos de ms-ventas vía Feign (usando CollectionModel por HATEOAS)
        var collectionModel = ventaClient.obtenerVentasPorRun(run);
        List<VentaDTO> ventas = collectionModel != null ? collectionModel.getContent().stream().toList() : List.of();

        if (ventas == null || ventas.isEmpty()) {
            log.warn("No se encontraron ventas para el RUN: {}", run);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("No se encontraron ventas para el RUN proporcionado.");
            return;
        }

        // 2. Configurar cabeceras de respuesta
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=reporte_ventas_" + run + ".pdf");

        // 3. Crear documento PDF
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();

        // 4. Agregar Logo (Esquina superior izquierda)
        try {
            // Intentar cargar la imagen desde resources
            var resource = new org.springframework.core.io.ClassPathResource("static/images/logo-ferrefix.png");
            if (resource.exists()) {
                com.lowagie.text.Image logo = com.lowagie.text.Image.getInstance(resource.getURL());
                logo.scaleToFit(80, 80); // Escalar a un tamaño razonable
                logo.setAbsolutePosition(40, 750); // Posición fija arriba a la izquierda
                document.add(logo);
            } else {
                log.warn("Archivo de logo no encontrado en: static/images/logo-ferrefix.png. Se generará el PDF sin logo.");
            }
        } catch (Exception e) {
            log.error("Error al cargar el logo: {}", e.getMessage());
        }

        // Estilos de fuente
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.DARK_GRAY);
        Font subTitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.GRAY);
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE);
        Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.BLACK);

        // Título
        Paragraph title = new Paragraph("Reporte de Ventas por Cliente", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(10);
        document.add(title);

        Paragraph subTitle = new Paragraph("RUN Cliente: " + run, subTitleFont);
        subTitle.setAlignment(Element.ALIGN_CENTER);
        subTitle.setSpacingAfter(20);
        document.add(subTitle);

        // Tabla de Ventas
        for (VentaDTO venta : ventas) {
            Paragraph ventaHeader = new Paragraph("Venta ID: " + venta.getIdVenta() + " | Fecha: " + venta.getFechaVenta() + " | Pago: " + venta.getNombreTipoPago(), subTitleFont);
            ventaHeader.setSpacingBefore(10);
            ventaHeader.setSpacingAfter(5);
            document.add(ventaHeader);

            PdfPTable table = new PdfPTable(5); // 5 columnas
            table.setWidthPercentage(100);
            table.setSpacingBefore(5);
            table.setWidths(new float[]{15f, 40f, 15f, 15f, 15f});

            // Cabeceras de tabla
            String[] headers = {"ID Prod", "Producto", "Cant", "P. Unit", "Subtotal"};
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                cell.setBackgroundColor(Color.GRAY);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(5);
                table.addCell(cell);
            }

            // Detalles de la venta
            if (venta.getDetalles() != null) {
                for (var detalle : venta.getDetalles()) {
                    table.addCell(new PdfPCell(new Phrase(detalle.getIdProducto().toString(), bodyFont)));
                    table.addCell(new PdfPCell(new Phrase(detalle.getNombreProducto(), bodyFont)));
                    table.addCell(new PdfPCell(new Phrase(detalle.getCantidad().toString(), bodyFont)));
                    table.addCell(new PdfPCell(new Phrase("$" + detalle.getPrecioUnitario(), bodyFont)));
                    table.addCell(new PdfPCell(new Phrase("$" + detalle.getSubtotal(), bodyFont)));
                }
            }

            document.add(table);

            Paragraph total = new Paragraph("TOTAL VENTA: $" + venta.getTotalVenta(), subTitleFont);
            total.setAlignment(Element.ALIGN_RIGHT);
            total.setSpacingAfter(10);
            document.add(total);
            
            document.add(new Paragraph("----------------------------------------------------------------------------------------------------------------------------------"));
        }

        document.close();
        log.info("PDF de ventas para el RUN {} generado correctamente.", run);
    }
}
