package ferrefix.ms_inventario.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import ferrefix.ms_inventario.dto.ProductoRequestDTO;
import ferrefix.ms_inventario.dto.ProductoResponseDTO;
import ferrefix.ms_inventario.exception.BadRequestException;
import ferrefix.ms_inventario.exception.ResourceNotFoundException;
import ferrefix.ms_inventario.mapper.ProductoMapper;
import ferrefix.ms_inventario.model.CategoriaProducto;
import ferrefix.ms_inventario.model.Producto;
import ferrefix.ms_inventario.model.UnidadMedida;
import ferrefix.ms_inventario.repository.CategoriaProductoRepository;
import ferrefix.ms_inventario.repository.ProductoRepository;
import ferrefix.ms_inventario.repository.UnidadMedidaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaProductoRepository categoriaProductoRepository;
    private final UnidadMedidaRepository unidadMedidaRepository;

    private final ProductoMapper productoMapper;

    // Inyectando el logger
    private static final Logger logger = LoggerFactory.getLogger(ProductoService.class);

    public ProductoResponseDTO crearProducto(ProductoRequestDTO dto) {
        logger.info("Iniciando creación de producto: '{}' con código: {}", dto.getNombre(), dto.getCodigoBarras());

        if (productoRepository.existsByCodigoBarrasProducto(dto.getCodigoBarras())) {
            logger.warn("Fallo al crear producto: El código de barras {} ya está registrado", dto.getCodigoBarras());
            throw new BadRequestException("Ya existe un producto con el código de barras " + dto.getCodigoBarras());
        }
        // Validacion para evitar inconsistencia por si no encuentra el recurso
        CategoriaProducto categoria = categoriaProductoRepository.findById(dto.getCategoria())
            .orElseThrow(() -> new ResourceNotFoundException("La categoría ID " + dto.getCategoria() + " no existe."));
            
        UnidadMedida unidadMedida = unidadMedidaRepository.findById(dto.getUnidadMedida())
            .orElseThrow(() -> new ResourceNotFoundException("La unidad de medida ID " + dto.getUnidadMedida() + " no existe."));   

        Producto productoGuardado = productoMapper.toEntity(dto, categoria, unidadMedida);
        // Guardando la entidad en la base de datos
        productoGuardado = productoRepository.save(productoGuardado);
        logger.info("Producto '{}' creado exitosamente con ID interno: {}", dto.getNombre(), productoGuardado.getIdProducto());
        return productoMapper.toDTO(productoGuardado);
    }

    public List<ProductoResponseDTO> buscarTodosProductos() {
        logger.info("Listando todos los productos del inventario");
        return productoRepository.findAll().stream()
                .map(producto -> productoMapper.toDTO(producto))
                .toList();
    }

    public ProductoResponseDTO buscarProductoPorId(Long idProducto) {
        logger.info("Buscando producto por ID: {}", idProducto);
        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> {
                    logger.warn("Búsqueda fallida: No existe producto con ID: {}", idProducto);
                    // Excepcion de recurso no encontrado 
                    return new ResourceNotFoundException("No se encontró el producto con id: " + idProducto);
                });
        return productoMapper.toDTO(producto);
    }

    public ProductoResponseDTO actualizarProducto(Long idProducto, ProductoRequestDTO dto) {
        logger.info("Actualizando producto con ID: {}. Nuevo nombre: '{}'", idProducto, dto.getNombre());

        Producto productoExistente = productoRepository.findById(idProducto)
                .orElseThrow(() -> {
                    logger.warn("Fallo al actualizar: Producto con ID {} no encontrado", idProducto);
                    return new ResourceNotFoundException("Producto con id: "+ idProducto+ " no encontrado");
                });

        productoRepository.findByCodigoBarrasProducto(dto.getCodigoBarras())
                .filter(producto -> !producto.getIdProducto().equals(idProducto))
                .ifPresent(producto -> {
                    logger.warn("Conflicto de actualización: El código de barras {} ya pertenece a otro producto", dto.getCodigoBarras());
                    throw new BadRequestException("Ya existe un producto con código de barras " + dto.getCodigoBarras());
                });
        
        // Validando las relaciones para evitar errores 500
        CategoriaProducto categoria = categoriaProductoRepository.findById(dto.getCategoria())
            .orElseThrow(() -> new ResourceNotFoundException("La categoría ID " + dto.getCategoria() + " no existe."));
            
        UnidadMedida unidadMedida = unidadMedidaRepository.findById(dto.getUnidadMedida())
                .orElseThrow(() -> new ResourceNotFoundException("La unidad de medida ID " + dto.getUnidadMedida() + " no existe."));

        // Aplicando actualizacion de la entidad
        productoMapper.updateEntity(productoExistente, dto, categoria, unidadMedida);
        // Guardando entidad actualizada a la base de datos
        productoExistente = productoRepository.save(productoExistente);
        logger.info("Producto ID {} ('{}') actualizado correctamente", idProducto, dto.getNombre());
        // Retornamos el producto actualizado como DTO
        return productoMapper.toDTO(productoExistente);
    }

    public void eliminarProducto(Long idProducto) {
        logger.info("Iniciando eliminación de producto con ID: {}", idProducto);
        
        if (!productoRepository.existsById(idProducto)) {
            logger.warn("Fallo al eliminar: No existe producto con ID: {}", idProducto);
            throw new ResourceNotFoundException("No se encontró el producto con id: " + idProducto);
        }
        
        productoRepository.deleteById(idProducto);
        logger.info("Producto con ID: {} eliminado exitosamente", idProducto);
    }
    
    // Metodo para descontar el stock automaticamente al momento de realizar un venta
    public void descontarStock(Long idProducto, Integer cantidad) {
    logger.info("Descontando {} unidades del producto ID: {}", cantidad, idProducto);

    Producto producto = productoRepository.findById(idProducto)
            .orElseThrow(() -> {
                logger.warn("404 - Producto ID {} no encontrado para descontar stock", idProducto);
                return new ResourceNotFoundException("No se encontró el producto con id: " + idProducto);
            });

    if (producto.getStockProducto() < cantidad) {
        logger.warn("Stock insuficiente para producto ID {}. Stock actual: {} | Solicitado: {}",
                idProducto, producto.getStockProducto(), cantidad);
        throw new BadRequestException(
                "Stock insuficiente para el producto '" + producto.getNombreProducto()
                + "'. Disponible: " + producto.getStockProducto()
                + ", solicitado: " + cantidad + ".");
    }

    producto.setStockProducto(producto.getStockProducto() - cantidad);
    productoRepository.save(producto);
    logger.info("Stock del producto ID {} actualizado. Nuevo stock: {}", idProducto, producto.getStockProducto());
}

}
