package ferrefix.ms_reportes.service;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.draw.DottedLineSeparator;
import ferrefix.ms_reportes.client.VentaClient;
import ferrefix.ms_reportes.dto.VentaDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReporteService {

    private final VentaClient ventaClient;

    // Paleta de Colores Corporativos Elegantes
    private static final Color COLOR_PRIMARIO = new Color(41, 128, 185);   // Azul Corporativo
    private static final Color COLOR_SECUNDARIO = new Color(52, 73, 94);  // Gris Pizarra Oscuro
    private static final Color COLOR_TEXTO_TEXT = new Color(44, 62, 80);   // Negro Suave
    private static final Color COLOR_FONDO_HEADER = new Color(242, 244, 244); // Gris muy claro para fondos
    private static final Color COLOR_LINEAS = new Color(213, 219, 219);     // Gris tenue para bordes

    /**
     * Exporta el historial de ventas de un cliente específico por su RUN.
     */
    public void exportarVentasPorRun(String run, OutputStream outputStream) throws DocumentException, java.io.IOException {
        log.info("Obteniendo ventas desde ms-ventas para el RUN: {}", run);
        var collectionModel = ventaClient.obtenerVentasPorRun(run);
        List<VentaDTO> ventas = collectionModel != null ? collectionModel.getContent().stream().toList() : List.of();

        if (ventas.isEmpty()) {
            throw new IllegalArgumentException("No se encontraron registros de ventas para el RUN especificado.");
        }

        String subtitulo = "Ferrefix • Reporte de Ventas\nRUN Titular: " + formatearRut(run);
        generarPdf(ventas, subtitulo, outputStream);
    }

    /**
     * Exporta las ventas filtradas por un rango de fechas y un cliente opcional.
     */
    public void exportarVentasPorRangoFechas(String runCliente, LocalDate fechaInicio, LocalDate fechaFin, OutputStream outputStream) throws DocumentException, java.io.IOException {
        if (fechaInicio == null || fechaFin == null) {
            throw new IllegalArgumentException("Las fechas de inicio y fin son obligatorias para generar el reporte.");
        }
        if (fechaInicio.isAfter(fechaFin)) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin.");
        }

        log.info("Obteniendo ventas desde ms-ventas para el rango: {} al {} | RUN cliente: {}", fechaInicio, fechaFin, runCliente);
        var collectionModel = ventaClient.buscarVentas(runCliente, fechaInicio, fechaFin);
        List<VentaDTO> ventas = collectionModel != null ? collectionModel.getContent().stream().toList() : List.of();

        if (ventas.isEmpty()) {
            throw new IllegalArgumentException("No se encontraron registros de ventas para los filtros especificados.");
        }

        StringBuilder sbSubtitulo = new StringBuilder("Ferrefix • Reporte de Ventas\n");
        if (runCliente != null && !runCliente.isBlank()) {
            sbSubtitulo.append("RUN Cliente: ").append(formatearRut(runCliente)).append("  |  ");
        }
        sbSubtitulo.append("Rango: ").append(fechaInicio).append(" al ").append(fechaFin);

        generarPdf(ventas, sbSubtitulo.toString(), outputStream);
    }

    /**
     * Orquesta la generación física del PDF con los estilos unificados de diseño.
     */
    private void generarPdf(List<VentaDTO> ventas, String subtituloText, OutputStream outputStream) throws DocumentException, java.io.IOException {
        // 1. Definición del documento con márgenes profesionales
        Document document = new Document(PageSize.A4, 36, 36, 40, 40);
        PdfWriter.getInstance(document, outputStream);

        document.open();

        // 2. Tipografía unificada (Helvetica) con jerarquías claras
        Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, COLOR_SECUNDARIO);
        Font fontSubtitulo = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.GRAY);
        Font fontSeccion = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, COLOR_PRIMARIO);
        Font fontTableHeader = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, COLOR_SECUNDARIO);
        Font fontTableBody = FontFactory.getFont(FontFactory.HELVETICA, 9, COLOR_TEXTO_TEXT);
        Font fontTotal = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, COLOR_SECUNDARIO);

        // 3. Header del Documento (Uso de una tabla estructural invisible para alinear Logo e Info)
        PdfPTable headerLayout = new PdfPTable(2);
        headerLayout.setWidthPercentage(100);
        headerLayout.setWidths(new float[]{20f, 80f});
        headerLayout.getDefaultCell().setBorder(Rectangle.NO_BORDER);

        // Celda del Logo
        try {
            var resource = new ClassPathResource("static/images/logo-ferrefix.png");
            if (resource.exists()) {
                Image logo = Image.getInstance(resource.getURL());
                logo.scaleToFit(75, 75);
                PdfPCell logoCell = new PdfPCell(logo);
                logoCell.setBorder(Rectangle.NO_BORDER);
                logoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                headerLayout.addCell(logoCell);
            } else {
                headerLayout.addCell(new PdfPCell(new Phrase(""))); // Celda vacía de respaldo
            }
        } catch (Exception e) {
            log.error("Fallo al inyectar el logo en el documento: {}", e.getMessage());
            headerLayout.addCell(new PdfPCell(new Phrase(""))); 
        }

        // Celda de Títulos de la Empresa / Reporte
        PdfPCell textCell = new PdfPCell();
        textCell.setBorder(Rectangle.NO_BORDER);
        textCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        
        Paragraph title = new Paragraph("REPORTE DE VENTAS", fontTitulo);
        title.setSpacingAfter(2);
        textCell.addElement(title);
        
        Paragraph sub = new Paragraph(subtituloText, fontSubtitulo);
        textCell.addElement(sub);
        
        headerLayout.addCell(textCell);
        document.add(headerLayout);

        // Línea divisoria elegante decorativa abajo del Header
        DottedLineSeparator separator = new DottedLineSeparator();
        separator.setLineColor(COLOR_LINEAS);
        separator.setGap(3f);
        
        Paragraph spacing = new Paragraph();
        spacing.add(separator);
        spacing.setSpacingBefore(10);
        spacing.setSpacingAfter(15);
        document.add(spacing);

        // 4. Renderizado del listado transaccional
        for (VentaDTO venta : ventas) {
            
            // Sub-encabezado por cada Boleta/Venta
            Paragraph ventaHeader = new Paragraph("Comprobante N°: 000" + venta.getIdVenta() + "  |  Fecha: " + venta.getFechaVenta() + "  |  Método Pago: " + venta.getNombreTipoPago(), fontSeccion);
            ventaHeader.setSpacingBefore(12);
            ventaHeader.setSpacingAfter(6);
            document.add(ventaHeader);

            // Estructura de Tabla Estilizada
            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{12f, 43f, 12f, 15f, 16f});

            // Definición de las cabeceras de columnas
            String[] headers = {"ID", "Descripción del Producto", "Cant.", "P. Unitario", "Subtotal"};
            for (int i = 0; i < headers.length; i++) {
                PdfPCell cell = new PdfPCell(new Phrase(headers[i], fontTableHeader));
                cell.setBackgroundColor(COLOR_FONDO_HEADER);
                cell.setBorderColor(COLOR_LINEAS);
                cell.setPadding(6);
                
                // Alineación correcta: Texto a la izquierda, números a la derecha
                if (i >= 2) {
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                } else {
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                }
                table.addCell(cell);
            }

            // Cuerpo de la Tabla
            if (venta.getDetalles() != null) {
                for (var detalle : venta.getDetalles()) {
                    
                    PdfPCell cId = new PdfPCell(new Phrase(detalle.getIdProducto().toString(), fontTableBody));
                    cId.setBorderColor(COLOR_LINEAS);
                    cId.setPadding(5);
                    table.addCell(cId);

                    PdfPCell cNombre = new PdfPCell(new Phrase(detalle.getNombreProducto(), fontTableBody));
                    cNombre.setBorderColor(COLOR_LINEAS);
                    cNombre.setPadding(5);
                    table.addCell(cNombre);

                    PdfPCell cCant = new PdfPCell(new Phrase(detalle.getCantidad().toString(), fontTableBody));
                    cCant.setBorderColor(COLOR_LINEAS);
                    cCant.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    cCant.setPadding(5);
                    table.addCell(cCant);

                    PdfPCell cPrecio = new PdfPCell(new Phrase("$" + String.format("%,d", detalle.getPrecioUnitario()), fontTableBody));
                    cPrecio.setBorderColor(COLOR_LINEAS);
                    cPrecio.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    cPrecio.setPadding(5);
                    table.addCell(cPrecio);

                    PdfPCell cSub = new PdfPCell(new Phrase("$" + String.format("%,d", detalle.getSubtotal()), fontTableBody));
                    cSub.setBorderColor(COLOR_LINEAS);
                    cSub.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    cSub.setPadding(5);
                    table.addCell(cSub);
                }
            }

            document.add(table);

            // Bloque de Totales alineado a la derecha debajo de la tabla
            int totalVal = venta.getTotalVenta() != null ? venta.getTotalVenta() : 0;
            int netoVal = venta.getNeto() != null ? venta.getNeto() : (int) Math.round(totalVal / 1.19);
            int ivaVal = venta.getIva() != null ? venta.getIva() : (totalVal - netoVal);

            PdfPTable totalTable = new PdfPTable(2);
            totalTable.setWidthPercentage(100);
            totalTable.setWidths(new float[]{80f, 20f});
            
            PdfPCell cellNetoLabel = new PdfPCell(new Phrase("NETO:", fontTableBody));
            cellNetoLabel.setBorder(Rectangle.NO_BORDER);
            cellNetoLabel.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totalTable.addCell(cellNetoLabel);

            PdfPCell cellNetoValue = new PdfPCell(new Phrase("$" + String.format("%,d", netoVal), fontTableBody));
            cellNetoValue.setBorder(Rectangle.NO_BORDER);
            cellNetoValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totalTable.addCell(cellNetoValue);

            PdfPCell cellIvaLabel = new PdfPCell(new Phrase("IVA (19%):", fontTableBody));
            cellIvaLabel.setBorder(Rectangle.NO_BORDER);
            cellIvaLabel.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totalTable.addCell(cellIvaLabel);

            PdfPCell cellIvaValue = new PdfPCell(new Phrase("$" + String.format("%,d", ivaVal), fontTableBody));
            cellIvaValue.setBorder(Rectangle.NO_BORDER);
            cellIvaValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totalTable.addCell(cellIvaValue);

            PdfPCell cellTotalLabel = new PdfPCell(new Phrase("TOTAL VENTA:", fontTotal));
            cellTotalLabel.setBorder(Rectangle.NO_BORDER);
            cellTotalLabel.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cellTotalLabel.setPaddingTop(4);
            cellTotalLabel.setPaddingBottom(10);
            totalTable.addCell(cellTotalLabel);

            PdfPCell cellTotalValue = new PdfPCell(new Phrase("$" + String.format("%,d", totalVal), fontTotal));
            cellTotalValue.setBorder(Rectangle.NO_BORDER);
            cellTotalValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cellTotalValue.setPaddingTop(4);
            cellTotalValue.setPaddingBottom(10);
            totalTable.addCell(cellTotalValue);

            document.add(totalTable);

            // Línea sutil de separación entre ventas
            Paragraph itemSeparator = new Paragraph();
            itemSeparator.add(separator);
            itemSeparator.setSpacingAfter(10);
            document.add(itemSeparator);
        }

        // 5. Bloque de Totales Acumulados del Reporte (Resumen de Impuestos)
        int granTotalNeto = 0;
        int granTotalIva = 0;
        int granTotalVenta = 0;

        for (VentaDTO venta : ventas) {
            int totalVal = venta.getTotalVenta() != null ? venta.getTotalVenta() : 0;
            int netoVal = venta.getNeto() != null ? venta.getNeto() : (int) Math.round(totalVal / 1.19);
            int ivaVal = venta.getIva() != null ? venta.getIva() : (totalVal - netoVal);

            granTotalNeto += netoVal;
            granTotalIva += ivaVal;
            granTotalVenta += totalVal;
        }

        // Título del bloque de resumen
        Paragraph resumenHeader = new Paragraph("RESUMEN DE TOTALES DEL REPORTE (DESGLOSE DE IVA 19%)", fontSeccion);
        resumenHeader.setSpacingBefore(15);
        resumenHeader.setSpacingAfter(8);
        document.add(resumenHeader);

        PdfPTable resumenTable = new PdfPTable(2);
        resumenTable.setWidthPercentage(100);
        resumenTable.setWidths(new float[]{80f, 20f});

        // Cantidad de Comprobantes
        PdfPCell cellCantLabel = new PdfPCell(new Phrase("Total Comprobantes:", fontTableBody));
        cellCantLabel.setBorder(Rectangle.BOX);
        cellCantLabel.setBorderColor(COLOR_LINEAS);
        cellCantLabel.setBackgroundColor(COLOR_FONDO_HEADER);
        cellCantLabel.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cellCantLabel.setPadding(6);
        resumenTable.addCell(cellCantLabel);

        PdfPCell cellCantValue = new PdfPCell(new Phrase(String.valueOf(ventas.size()), fontTableBody));
        cellCantValue.setBorder(Rectangle.BOX);
        cellCantValue.setBorderColor(COLOR_LINEAS);
        cellCantValue.setBackgroundColor(COLOR_FONDO_HEADER);
        cellCantValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cellCantValue.setPadding(6);
        resumenTable.addCell(cellCantValue);

        // MONTO NETO TOTAL
        PdfPCell cellNetoAcumLabel = new PdfPCell(new Phrase("MONTO NETO TOTAL:", fontTableBody));
        cellNetoAcumLabel.setBorder(Rectangle.BOX);
        cellNetoAcumLabel.setBorderColor(COLOR_LINEAS);
        cellNetoAcumLabel.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cellNetoAcumLabel.setPadding(6);
        resumenTable.addCell(cellNetoAcumLabel);

        PdfPCell cellNetoAcumValue = new PdfPCell(new Phrase("$" + String.format("%,d", granTotalNeto), fontTableBody));
        cellNetoAcumValue.setBorder(Rectangle.BOX);
        cellNetoAcumValue.setBorderColor(COLOR_LINEAS);
        cellNetoAcumValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cellNetoAcumValue.setPadding(6);
        resumenTable.addCell(cellNetoAcumValue);

        // IVA TOTAL (19%)
        PdfPCell cellIvaAcumLabel = new PdfPCell(new Phrase("IVA TOTAL (19%):", fontTableBody));
        cellIvaAcumLabel.setBorder(Rectangle.BOX);
        cellIvaAcumLabel.setBorderColor(COLOR_LINEAS);
        cellIvaAcumLabel.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cellIvaAcumLabel.setPadding(6);
        resumenTable.addCell(cellIvaAcumLabel);

        PdfPCell cellIvaAcumValue = new PdfPCell(new Phrase("$" + String.format("%,d", granTotalIva), fontTableBody));
        cellIvaAcumValue.setBorder(Rectangle.BOX);
        cellIvaAcumValue.setBorderColor(COLOR_LINEAS);
        cellIvaAcumValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cellIvaAcumValue.setPadding(6);
        resumenTable.addCell(cellIvaAcumValue);

        // TOTAL RECAUDADO
        PdfPCell cellGranTotalLabel = new PdfPCell(new Phrase("TOTAL RECAUDADO:", fontTotal));
        cellGranTotalLabel.setBorder(Rectangle.BOX);
        cellGranTotalLabel.setBorderColor(COLOR_LINEAS);
        cellGranTotalLabel.setBackgroundColor(COLOR_FONDO_HEADER);
        cellGranTotalLabel.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cellGranTotalLabel.setPadding(8);
        resumenTable.addCell(cellGranTotalLabel);

        PdfPCell cellGranTotalValue = new PdfPCell(new Phrase("$" + String.format("%,d", granTotalVenta), fontTotal));
        cellGranTotalValue.setBorder(Rectangle.BOX);
        cellGranTotalValue.setBorderColor(COLOR_LINEAS);
        cellGranTotalValue.setBackgroundColor(COLOR_FONDO_HEADER);
        cellGranTotalValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cellGranTotalValue.setPadding(8);
        resumenTable.addCell(cellGranTotalValue);

        document.add(resumenTable);

        document.close();
        log.info("Documento PDF escrito con éxito en los flujos de salida.");
    }

    /**
     * Formatea un RUT con puntos y guion (ej: 12.222.222-2)
     */
    private String formatearRut(String rut) {
        if (rut == null || rut.isBlank()) {
            return "";
        }
        String limpio = rut.replace(".", "").replace("-", "").replace(" ", "").trim();
        if (limpio.length() < 2) {
            return rut;
        }
        String dv = limpio.substring(limpio.length() - 1);
        String numero = limpio.substring(0, limpio.length() - 1);
        
        try {
            double parsed = Double.parseDouble(numero);
            java.text.DecimalFormat df = new java.text.DecimalFormat("###,###,###");
            df.setDecimalFormatSymbols(new java.text.DecimalFormatSymbols(java.util.Locale.GERMAN));
            return df.format(parsed) + "-" + dv.toUpperCase();
        } catch (NumberFormatException e) {
            return rut;
        }
    }
}
