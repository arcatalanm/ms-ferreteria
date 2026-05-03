package ferrefix.ms_ventas.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import ferrefix.ms_ventas.client.InventarioClient;
import ferrefix.ms_ventas.client.UsuariosClient;
import ferrefix.ms_ventas.dto.DetalleVentaResponseDTO;
import ferrefix.ms_ventas.dto.ProductoDTO;
import ferrefix.ms_ventas.dto.VentaRequestDTO;
import ferrefix.ms_ventas.dto.VentaResponseDTO;
import ferrefix.ms_ventas.model.DetalleVenta;
import ferrefix.ms_ventas.model.TipoPago;
import ferrefix.ms_ventas.model.Venta;
import ferrefix.ms_ventas.repository.DetalleVentaRepository;
import ferrefix.ms_ventas.repository.TipoPagoRepository;
import ferrefix.ms_ventas.repository.VentaRepository;
import feign.FeignException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service

// Evitamos problemas en las reglas de negocio por si falla, podemos volver hacer un rollback
@Transactional
// Constructor para inyectar las dependencias de los Repositorios (Los necesarios)
// Ademas Evitamos Saturar la pantalla con constructores y Getters y Setters
@RequiredArgsConstructor
public class VentaService {

    private final VentaRepository ventaRepository;
    private final DetalleVentaRepository detalleVentaRepository;
    private final TipoPagoRepository tipoPagoRepository;
    private final UsuariosClient usuariosClient;
    private final InventarioClient inventarioClient;

    public VentaResponseDTO guardar(VentaRequestDTO request) {
        TipoPago tipoPago = tipoPagoRepository.findById(request.getIdTipoPago())
                .orElseThrow(() -> new IllegalArgumentException("Tipo de pago no existe"));

        try {
            usuariosClient.obtenerClientePorRun(request.getRunCliente());
        } catch (FeignException.NotFound ex) {
            throw new IllegalArgumentException("Cliente no existe");
        }

        try {
            usuariosClient.obtenerEmpleadoPorRun(request.getRunEmpleado());
        } catch (FeignException.NotFound ex) {
            throw new IllegalArgumentException("Empleado no existe");
        }

        // Crear la cabecera de la venta
        Venta venta = Venta.builder()
                .fechaVenta(LocalDateTime.now())
                .runCliente(request.getRunCliente())
                .runEmpleado(request.getRunEmpleado())
                .tipoPago(tipoPago)
                .totalVenta(0)
                .build();
        venta = ventaRepository.save(venta);

        int acumuladorTotal = 0;
        for (var item : request.getDetalles()) {
            ProductoDTO producto;
            try {
                producto = inventarioClient.obtenerProductoPorId(item.getIdProducto());
            } catch (FeignException.NotFound ex) {
                throw new IllegalArgumentException("Producto no existe: " + item.getIdProducto());
            }

            int subtotal = producto.getPrecioVenta() * item.getCantidad();
            acumuladorTotal += subtotal;

            DetalleVenta detalle = DetalleVenta.builder()
                    .venta(venta)
                    .idProducto(item.getIdProducto())
                    .cantidad(item.getCantidad())
                    .precioUnitario(producto.getPrecioVenta())
                    .build();
            detalleVentaRepository.save(detalle);
        }

        venta.setTotalVenta(acumuladorTotal);
        ventaRepository.save(venta);

        return mapVentaToDTO(venta, "Venta registrada exitosamente");
    }

    public List<VentaResponseDTO> listarVentas() {
        return ventaRepository.findAll().stream()
                .map(venta -> mapVentaToDTO(venta, null))
                .toList();
    }

    public VentaResponseDTO buscarVentaPorId(Long idVenta) {
        Venta venta = ventaRepository.findById(idVenta)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró la venta con id: " + idVenta));
        return mapVentaToDTO(venta, null);
    }

    public List<VentaResponseDTO> buscarVentasPorCliente(Integer runCliente) {
        return ventaRepository.findByRunCliente(runCliente).stream()
                .map(venta -> mapVentaToDTO(venta, null))
                .toList();
    }

    public List<VentaResponseDTO> buscarVentasPorEmpleado(Integer runEmpleado) {
        return ventaRepository.findByRunEmpleado(runEmpleado).stream()
                .map(venta -> mapVentaToDTO(venta, null))
                .toList();
    }

    public void eliminarVenta(Long idVenta) {
        Venta venta = ventaRepository.findById(idVenta)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró la venta con id: " + idVenta));

        detalleVentaRepository.deleteAll(detalleVentaRepository.findByVenta_IdVenta(idVenta));
        ventaRepository.delete(venta);
    }

    private VentaResponseDTO mapVentaToDTO(Venta venta, String mensaje) {
        List<DetalleVentaResponseDTO> detalles = detalleVentaRepository.findByVenta_IdVenta(venta.getIdVenta())
                .stream()
                .map(this::mapDetalleToDTO)
                .toList();

        return VentaResponseDTO.builder()
                .idVenta(venta.getIdVenta())
                .fechaVenta(LocalDateTime.now())
                .totalVenta(venta.getTotalVenta())
                .nombreTipoPago(venta.getTipoPago().getNombreTipoPago())
                .detalles(detalles)
                .mensaje(mensaje)
                .build();
    }

    private DetalleVentaResponseDTO mapDetalleToDTO(DetalleVenta detalleVenta) {
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
