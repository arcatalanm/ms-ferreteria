package ferrefix.ms_ventas.service;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ferrefix.ms_ventas.client.InventarioClient;
import ferrefix.ms_ventas.client.UsuariosClient;
import ferrefix.ms_ventas.dto.DetalleVentaResponseDTO;
import ferrefix.ms_ventas.dto.ProductoDTO;
import ferrefix.ms_ventas.dto.VentaRequestDTO;
import ferrefix.ms_ventas.dto.VentaResponseDTO;
import ferrefix.ms_ventas.exception.BadRequestException;
import ferrefix.ms_ventas.exception.ResourceNotFoundException;
import ferrefix.ms_ventas.mapper.VentaMapper;
import ferrefix.ms_ventas.model.DetalleVenta;
import ferrefix.ms_ventas.model.TipoPago;
import ferrefix.ms_ventas.model.Venta;
import ferrefix.ms_ventas.repository.DetalleVentaRepository;
import ferrefix.ms_ventas.repository.TipoPagoRepository;
import ferrefix.ms_ventas.repository.VentaRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;

/* Servicio de negocio para la gestión de Ventas. */
@Service
@Transactional // Garantiza que si la venta falla a la mitad, no se guarda nada en la BD (Rollback).
@RequiredArgsConstructor 
public class VentaService {
    
    private static final Logger logger = LoggerFactory.getLogger(VentaService.class);
    private final VentaRepository ventaRepository;
    private final DetalleVentaRepository detalleVentaRepository;
    private final TipoPagoRepository tipoPagoRepository;
    private final UsuariosClient usuariosClient;
    private final InventarioClient inventarioClient;
    private final VentaMapper ventaMapper;


    // Método para guardar una venta
    public VentaResponseDTO guardar(VentaRequestDTO request) {
        logger.info("Iniciando creación de nueva venta para cliente RUN: {}", request.getRunCliente());
        
        // 1. Validar Tipo de Pago
        TipoPago tipoPago = tipoPagoRepository.findById(request.getIdTipoPago())
                .orElseThrow(() -> {
                    logger.warn("Tipo de pago ID {} no encontrado", request.getIdTipoPago());
                    return new ResourceNotFoundException("Tipo de pago no existe");
                });

        // 2. Validar Cliente y Empleado
        try {
            usuariosClient.obtenerClientePorRun(request.getRunCliente());
            usuariosClient.obtenerEmpleadoPorRun(request.getRunEmpleado());
        } catch (FeignException.NotFound ex) {
            logger.warn("Cliente o Empleado no encontrado");
            throw new BadRequestException("El cliente o empleado no existe.");
        }

        // 3. Crear cabecera
        Venta venta = ventaMapper.toVentaEntity(request, tipoPago);
        venta = ventaRepository.save(venta);

        // 4. Procesar Detalles
        int subtotalAcumulado = 0;
        
        for (var item : request.getDetalles()) {
            ProductoDTO producto;
            try {
                producto = inventarioClient.obtenerProductoPorId(item.getIdProducto());
            } catch (FeignException.NotFound ex) {
                throw new BadRequestException("Producto ID " + item.getIdProducto() + " no existe.");
            }
            
            if (producto.getStock() < item.getCantidad()) {
                throw new BadRequestException("Stock insuficiente para ID " + item.getIdProducto());
            }
            
            subtotalAcumulado += (producto.getPrecioVenta() * item.getCantidad());
            detalleVentaRepository.save(ventaMapper.toDetalleEntity(item, venta, producto.getPrecioVenta()));
            
            inventarioClient.descontarStock(item.getIdProducto(), item.getCantidad());
            logger.info("Stock descontado para ID {}: -{}", item.getIdProducto(), item.getCantidad());
        }

        // 5. Totalizar y finalizar
        venta.setTotalVenta(subtotalAcumulado);
        ventaRepository.save(venta);
        
        logger.info("Venta creada exitosamente. ID: {}, Total: ${}", venta.getIdVenta(), subtotalAcumulado);
        return mapVentaToDTO(venta, "Venta registrada exitosamente");
    }

    /* Lista todas las ventas registradas. */
    public List<VentaResponseDTO> listarVentas() {
        logger.info("Iniciando búsqueda de todas las ventas");
        List<VentaResponseDTO> ventas = ventaRepository.findAll().stream()
                .map(venta -> mapVentaToDTO(venta, null))
                .toList();
        logger.info("Búsqueda completada. Total encontrado: {}", ventas.size());
        return ventas;
    }

    /* Buscar <Ventas> por runCliente */
    public List<VentaResponseDTO> buscarVentasPorRunCliente(String runCliente) {
        Integer run = parseRun(runCliente, "cliente");
        logger.info("Buscando venta por run: {}", runCliente);
        // ventas de por un cliente en especifico
        List<Venta> ventas = ventaRepository.findByRunCliente(run);
        if (ventas.isEmpty()) {
            logger.warn("No se encontraron ventas de ese usuario");
            throw new ResourceNotFoundException("No se encontraron ventas para el cliente con RUN: " + runCliente);
        }
        // Retornamos las ventas o la venta mappeada
        logger.info("Ventas encontradas con el run: {}", runCliente);
        return ventas.stream()
                .map(venta -> mapVentaToDTO(venta, "Haciendo transformación de la entidad a DTO"))
                .toList();
    }

    // Conversion del rut
    private Integer parseRun(String runConDv, String tipo) {
        if (runConDv == null) {
            throw new BadRequestException("El run de " + tipo + " es obligatorio.");
        }
        String limpio = runConDv.replace(".", "").replace(" ", "").trim();
        if (!limpio.contains("-")) {
            throw new BadRequestException("El run de " + tipo + " debe incluir DV y guion: " + runConDv);
        }
        String[] partes = limpio.split("-");
        if (partes.length != 2 || partes[0].isBlank()) {
            throw new BadRequestException("El run de " + tipo + " no tiene formato válido: " + runConDv);
        }
        try {
            return Integer.valueOf(partes[0]);
        } catch (NumberFormatException ex) {
            throw new BadRequestException("El run de " + tipo + " debe ser numérico antes del DV: " + runConDv);
        }
    }

    /* Metodo que elimina una venta */
    public void eliminarVenta(Long idVenta) {
        logger.info("Iniciando eliminación de venta con ID: {}", idVenta);
        Venta venta = ventaRepository.findById(idVenta)
                .orElseThrow(() -> {
                    logger.warn("Fallo al eliminar: Venta no encontrada con ID: {}", idVenta);
                    return new ResourceNotFoundException("No se encontró la venta con id: " + idVenta);
                });

        // Eliminar detalles primero para evitar violaciones de clave foránea
        detalleVentaRepository.deleteAll(detalleVentaRepository.findByVenta_IdVenta(idVenta));
        ventaRepository.delete(venta);
        logger.info("Venta y sus detalles eliminados exitosamente con ID: {}", idVenta);
    }

    private VentaResponseDTO mapVentaToDTO(Venta venta, String mensaje) {
        List<DetalleVentaResponseDTO> detalles = detalleVentaRepository.findByVenta_IdVenta(venta.getIdVenta())
                .stream()
                .map(this::mapDetalleToDTO)
                .toList();

        return ventaMapper.toVentaResponseDTO(venta, detalles);
    }

    /* Convierte el detalle y trae el nombre del producto desde el MS-Inventario. */
    private DetalleVentaResponseDTO mapDetalleToDTO(DetalleVenta detalleVenta) {
        String nombreProducto = "Desconocido";
        try {
            // Buscamos el nombre del producto para que la boleta no muestre solo un ID
            ProductoDTO producto = inventarioClient.obtenerProductoPorId(detalleVenta.getIdProducto());
            if (producto != null) {
                nombreProducto = producto.getNombre();
            }
        } catch (FeignException.NotFound ex) {
            logger.warn("Producto eliminado del inventario, pero registrado en histórico. ID: {}", detalleVenta.getIdProducto());
            nombreProducto = "Producto Descontinuado";
        }

        return ventaMapper.toDetalleResponseDTO(detalleVenta, nombreProducto);
    }

    /* ========================================================== */
       /* AQUI ESTARA DEFINIDO LO DE LOS DETALLES DE LA VENTA */
    /* ========================================================== */

    public List<DetalleVentaResponseDTO> buscarDetallesPorVenta(Long idVenta) {
    // Primero validar que la venta relacionada exista
    ventaRepository.findById(idVenta)
        .orElseThrow(() -> new ResourceNotFoundException(
            "No se encontró la venta con id: " + idVenta));

    return detalleVentaRepository.findByVenta_IdVenta(idVenta)
            .stream()
            .map(this::mapDetalleToDTO)
            .toList();
    }

    public DetalleVentaResponseDTO buscarDetallePorId(Long idVenta, Long idDetalle) {
    // Validar que la venta existe
    ventaRepository.findById(idVenta)
        .orElseThrow(() -> new ResourceNotFoundException(
            "No se encontró la venta con id: " + idVenta));

    // Validar que el detalle existe Y pertenece a ESA venta
    DetalleVenta detalle = detalleVentaRepository.findById(idDetalle)
        .orElseThrow(() -> new ResourceNotFoundException(
            "No se encontró el detalle con id: " + idDetalle));

    if (!detalle.getVenta().getIdVenta().equals(idVenta)) {
        throw new BadRequestException(
            "El detalle " + idDetalle + " no pertenece a la venta " + idVenta);
    }

    return mapDetalleToDTO(detalle);
}
}