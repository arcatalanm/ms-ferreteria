package ferrefix.ms_ventas.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import ferrefix.ms_ventas.client.InventarioClient;
import ferrefix.ms_ventas.dto.DetalleVentaResponseDTO;
import ferrefix.ms_ventas.dto.ProductoDTO;
import ferrefix.ms_ventas.exception.BadRequestException;
import ferrefix.ms_ventas.exception.ResourceNotFoundException;
import ferrefix.ms_ventas.mapper.VentaMapper;
import ferrefix.ms_ventas.model.DetalleVenta;
import ferrefix.ms_ventas.repository.DetalleVentaRepository;
import feign.FeignException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

/**
 * Servicio de negocio para la entidad DetalleVenta.
 * Gestiona la lógica de validación, persistencia y transformación de datos.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class DetalleVentaService {
    
    private static final Logger logger = LoggerFactory.getLogger(DetalleVentaService.class);

    private final DetalleVentaRepository detalleVentaRepository;

    // Inyeccion InventarioClient
    private final InventarioClient inventarioClient;

    private final VentaMapper ventaMapper;

    /**
     * Busca todos los detalles de una venta específica.
     */
    public List<DetalleVentaResponseDTO> buscarDetallesPorVenta(Long idVenta) {
        logger.info("Iniciando búsqueda de detalles para venta ID: {}", idVenta);
        
        if (idVenta == null || idVenta <= 0) {
            logger.warn("ID de venta inválido: {}", idVenta);
            throw new BadRequestException("El ID de la venta debe ser mayor a 0");
        }
        
        List<DetalleVentaResponseDTO> detalles = detalleVentaRepository.findByVenta_IdVenta(idVenta).stream()
                .map(this::mapToDTO)
                .toList();
        
        logger.info("Búsqueda completada. Total de detalles encontrados: {}", detalles.size());
        return detalles;
    }

    /**
     * Busca un detalle de venta por su ID.
     */
    public DetalleVentaResponseDTO buscarDetallePorId(Long idDetalle) {
        logger.info("Iniciando búsqueda de detalle venta con ID: {}", idDetalle);
        
        if (idDetalle == null || idDetalle <= 0) {
            logger.warn("ID inválido para detalle venta: {}", idDetalle);
            throw new BadRequestException("El ID del detalle debe ser mayor a 0");
        }
        
        DetalleVenta detalle = detalleVentaRepository.findById(idDetalle)
                .orElseThrow(() -> {
                    logger.warn("Detalle venta no encontrado con ID: {}", idDetalle);
                    return new ResourceNotFoundException("No se encontró el detalle de venta con id: " + idDetalle);
                });
        
        DetalleVentaResponseDTO resultado = mapToDTO(detalle);
        logger.info("Detalle venta obtenido exitosamente con ID: {}", idDetalle);
        
        return resultado;
    }

    /**
     * Convierte una entidad DetalleVenta a DetalleVentaResponseDTO.
     */
    private DetalleVentaResponseDTO mapToDTO(DetalleVenta detalleVenta) {
        String nombreProducto = "Desconocido";
        try {
            ProductoDTO producto = inventarioClient.obtenerProductoPorId(detalleVenta.getIdProducto());
            if (producto != null) {
                nombreProducto = producto.getNombre();
            }
        } catch (FeignException.NotFound ex) {
            logger.warn("Producto descontinuado. ID: {}", detalleVenta.getIdProducto());
            nombreProducto = "Producto Descontinuado";
        } catch (FeignException ex) {
            // ¡ESTE ES EL CATCH QUE FALTA! Atrapa el Error 500 que te está mandando Inventario
            logger.warn("El MS-Inventario arrojó un error al buscar el producto ID {}: {}", detalleVenta.getIdProducto(), ex.getMessage());
            nombreProducto = "Producto No Encontrado (Error en Inventario)";
        }

        return ventaMapper.toDetalleResponseDTO(detalleVenta, nombreProducto);
    }
}
