package ferrefix.ms_ventas.service;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

/**
 * Servicio de negocio para la gestión de Ventas.
 * Orquesta la comunicación entre repositorios locales y llamadas a otros microservicios.
 */
@Service
@Transactional // Garantiza que si la venta falla a la mitad, no se guarda nada en la BD (Rollback).
@RequiredArgsConstructor 
public class VentaService {
    
    private static final Logger logger = LoggerFactory.getLogger(VentaService.class);

    // EXPLICACIÓN DE INYECCIÓN DE DEPENDENCIAS:
    // @RequiredArgsConstructor de Lombok genera automáticamente un constructor que recibe
    // todos estos campos 'final'. Spring Boot inyecta los repositorios y clientes Feign 
    // al momento de arrancar. Es más seguro y limpio que usar @Autowired.
    private final VentaRepository ventaRepository;
    private final DetalleVentaRepository detalleVentaRepository;
    private final TipoPagoRepository tipoPagoRepository;
    private final UsuariosClient usuariosClient;
    private final InventarioClient inventarioClient;
    private final VentaMapper ventaMapper;

    /**
     * Guarda una nueva venta validando primero contra otros microservicios.
     * @param request Datos de la venta enviados por el frontend.
     * @return VentaResponseDTO con el resumen de la boleta.
     */
    public VentaResponseDTO guardar(VentaRequestDTO request) {
        logger.info("Iniciando creación de nueva venta para cliente RUN: {}", request.getRunCliente());
        
        // 1. Validar Tipo de Pago (Local)
        TipoPago tipoPago = tipoPagoRepository.findById(request.getIdTipoPago())
                .orElseThrow(() -> {
                    logger.warn("El tipo de pago con ID {} no existe en la BD local", request.getIdTipoPago());
                    return new ResourceNotFoundException("Tipo de pago no existe");
                });

        // 2. Validar Cliente (Llamada Externa a MS-Usuarios)
        try {
            // El FeignClient viaja al API Gateway -> MS-Usuarios
            usuariosClient.obtenerClientePorRun(request.getRunCliente());
        } catch (FeignException.NotFound ex) {
            // Si el MS-Usuarios devuelve un 404, Feign lanza esta excepción. 
            // La atrapamos y la convertimos en una excepción nuestra para el GlobalExceptionHandler.
            logger.warn("Cliente no existe con RUN: {}", request.getRunCliente());
            throw new BadRequestException("El cliente con RUN " + request.getRunCliente() + " no está registrado.");
        }

        // 3. Validar Empleado (Llamada Externa a MS-Usuarios)
        try {
            usuariosClient.obtenerEmpleadoPorRun(request.getRunEmpleado());
        } catch (FeignException.NotFound ex) {
            logger.warn("Empleado no existe con RUN: {}", request.getRunEmpleado());
            throw new BadRequestException("El empleado vendedor no existe en los registros.");
        }

        // 4. Crear la cabecera de la venta (Aún con total 0)
        Venta venta = ventaMapper.toVentaEntity(request, tipoPago);
        venta = ventaRepository.save(venta); // Se guarda para generar el ID que usarán los detalles

        // 5. Procesar los Detalles (Llamadas Externas a MS-Inventario)
        int acumuladorTotal = 0;
        
        for (var item : request.getDetalles()) {
            ProductoDTO producto;
            try {
                // Consultamos el MS-Inventario para traer el producto real y su precio ACTUAL
                producto = inventarioClient.obtenerProductoPorId(item.getIdProducto());
            } catch (FeignException.NotFound ex) {
                logger.warn("El producto con ID {} no existe en el MS-Inventario", item.getIdProducto());
                // Si un producto no existe, la transacción hace Rollback de la Venta creada arriba.
                throw new BadRequestException("Producto no existe: " + item.getIdProducto());
            }

            int subtotal = producto.getPrecioVenta() * item.getCantidad();
            acumuladorTotal += subtotal;

            // Guardamos el detalle histórico para que, si el precio cambia mañana, esta boleta mantenga el precio antiguo
            DetalleVenta detalle = ventaMapper.toDetalleEntity(item, venta, producto.getPrecioVenta());
            detalleVentaRepository.save(detalle);
        }

        // 6. Actualizar el total final de la cabecera
        venta.setTotalVenta(acumuladorTotal);
        ventaRepository.save(venta);
        
        logger.info("Venta creada exitosamente con ID: {}. Total a cobrar: {}", venta.getIdVenta(), acumuladorTotal);

        return mapVentaToDTO(venta, "Venta registrada exitosamente");
    }

    /**
     * Lista todas las ventas registradas.
     */
    public List<VentaResponseDTO> listarVentas() {
        logger.info("Iniciando búsqueda de todas las ventas");
        List<VentaResponseDTO> ventas = ventaRepository.findAll().stream()
                .map(venta -> mapVentaToDTO(venta, null))
                .toList();
        logger.info("Búsqueda completada. Total encontrado: {}", ventas.size());
        return ventas;
    }

    /**
     * Busca una venta específica por su ID.
     */
    public VentaResponseDTO buscarVentaPorId(Long idVenta) {
        logger.info("Iniciando búsqueda de venta con ID: {}", idVenta);
        Venta venta = ventaRepository.findById(idVenta)
                .orElseThrow(() -> {
                    logger.warn("Venta no encontrada con ID: {}", idVenta);
                    return new ResourceNotFoundException("No se encontró la venta con id: " + idVenta);
                });
        logger.info("Venta obtenida exitosamente");
        return mapVentaToDTO(venta, null);
    }

    // ... (Mantén los métodos buscarVentasPorCliente y buscarVentasPorEmpleado que estaban perfectos)

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

    /**
     * Convierte la entidad Venta en un DTO enriquecido para el Frontend.
     * NOTA: Este método también llama a mapDetalleToDTO, el cual hace consultas a Inventario
     * para rellenar los nombres de los productos en la boleta.
     */
    private VentaResponseDTO mapVentaToDTO(Venta venta, String mensaje) {
        List<DetalleVentaResponseDTO> detalles = detalleVentaRepository.findByVenta_IdVenta(venta.getIdVenta())
                .stream()
                .map(this::mapDetalleToDTO)
                .toList();

        return ventaMapper.toVentaResponseDTO(venta, detalles, mensaje);
    }

    /**
     * Convierte el detalle y enriquece con el nombre del producto desde el MS-Inventario.
     */
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
}