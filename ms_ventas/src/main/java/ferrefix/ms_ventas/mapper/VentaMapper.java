package ferrefix.ms_ventas.mapper;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Component;

import ferrefix.ms_ventas.dto.DetalleVentaRequestDTO;
import ferrefix.ms_ventas.dto.DetalleVentaResponseDTO;
import ferrefix.ms_ventas.dto.VentaRequestDTO;
import ferrefix.ms_ventas.dto.VentaResponseDTO;
import ferrefix.ms_ventas.exception.BadRequestException;
import ferrefix.ms_ventas.model.DetalleVenta;
import ferrefix.ms_ventas.model.TipoPago;
import ferrefix.ms_ventas.model.Venta;

@Component
public class VentaMapper {

    // --- DE DTO A ENTIDAD ---
    public Venta toVentaEntity(VentaRequestDTO dto, TipoPago tipoPago) {
        return Venta.builder()
                .fechaVenta(LocalDateTime.now())
                .totalVenta(0)
                .runCliente(parseRun(dto.getRunCliente()))
                .runEmpleado(parseRun(dto.getRunEmpleado()))
                .tipoPago(tipoPago)
                .build();
    }

    private Integer parseRun(String run) {
        if (run == null) {
            throw new BadRequestException("El run no puede ser nulo");
        }
        String limpio = run.replace(".", "").replace(" ", "").trim();
        if (!limpio.contains("-")) {
            throw new BadRequestException("El run debe incluir DV y guion: " + run);
        }
        String[] partes = limpio.split("-");
        if (partes.length != 2 || partes[0].isBlank()) {
            throw new BadRequestException("El run debe tener formato válido: " + run);
        }
        try {
            return Integer.valueOf(partes[0]);
        } catch (NumberFormatException ex) {
            throw new BadRequestException("El run debe ser numérico antes del DV: " + run);
        }
    }

    public DetalleVenta toDetalleEntity(DetalleVentaRequestDTO dto, Venta venta, Integer precioUnitario ) {
        return DetalleVenta.builder()
                .idProducto(dto.getIdProducto())
                .cantidad(dto.getCantidad())
                .precioUnitario(precioUnitario)
                .venta(venta)
                .build();
    }

    // --- DE ENTIDAD A DTO ---
    public DetalleVentaResponseDTO toDetalleResponseDTO(DetalleVenta detalle, String nombreProducto) {
        return DetalleVentaResponseDTO.builder()
                .idProducto(detalle.getIdProducto())
                .nombreProducto(nombreProducto)
                .cantidad(detalle.getCantidad())
                .precioUnitario(detalle.getPrecioUnitario())
                .subtotal(detalle.getCantidad() * detalle.getPrecioUnitario())
                .build();
    }

    public VentaResponseDTO toVentaResponseDTO(Venta venta, List<DetalleVentaResponseDTO> detallesDTO) {
        return VentaResponseDTO.builder()
                .idVenta(venta.getIdVenta())
                .runEmpleado(venta.getRunEmpleado() != null ? String.valueOf(venta.getRunEmpleado()) : null)
                .runCliente(venta.getRunCliente() != null ? String.valueOf(venta.getRunCliente()) : null)
                .fechaVenta(venta.getFechaVenta())
                .totalVenta(venta.getTotalVenta())
                .nombreTipoPago(venta.getTipoPago().getNombreTipoPago())
                .detalles(detallesDTO)
                .build();
    }
}
