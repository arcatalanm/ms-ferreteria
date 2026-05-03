package ferrefix.ms_ventas.service;

import java.util.List;

import org.springframework.stereotype.Service;

import ferrefix.ms_ventas.client.InventarioClient;
import ferrefix.ms_ventas.dto.DetalleVentaResponseDTO;
import ferrefix.ms_ventas.dto.ProductoDTO;
import ferrefix.ms_ventas.model.DetalleVenta;
import ferrefix.ms_ventas.repository.DetalleVentaRepository;
import feign.FeignException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class DetalleVentaService {

    private final DetalleVentaRepository detalleVentaRepository;
    private final InventarioClient inventarioClient;

    public List<DetalleVentaResponseDTO> buscarDetallesPorVenta(Long idVenta) {
        return detalleVentaRepository.findByVenta_IdVenta(idVenta).stream()
                .map(this::mapToDTO)
                .toList();
    }

    public DetalleVentaResponseDTO buscarDetallePorId(Long idDetalle) {
        DetalleVenta detalle = detalleVentaRepository.findById(idDetalle)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el detalle de venta con id: " + idDetalle));
        return mapToDTO(detalle);
    }

    private DetalleVentaResponseDTO mapToDTO(DetalleVenta detalleVenta) {
        String nombreProducto = null;
        try {
            ProductoDTO producto = inventarioClient.obtenerProductoPorId(detalleVenta.getIdProducto());
            nombreProducto = producto != null ? producto.getNombre() : null;
        } catch (FeignException.NotFound ex) {
            nombreProducto = "Producto no disponible";
        }

        return DetalleVentaResponseDTO.builder()
                .idProducto(detalleVenta.getIdProducto())
                .nombreProducto(nombreProducto)
                .cantidad(detalleVenta.getCantidad())
                .precioUnitario(detalleVenta.getPrecioUnitario())
                .subtotal(detalleVenta.getCantidad() * detalleVenta.getPrecioUnitario())
                .build();
    }
}
