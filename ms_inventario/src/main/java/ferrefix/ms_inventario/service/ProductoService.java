package ferrefix.ms_inventario.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import ferrefix.ms_inventario.dto.ProductoRequestDTO;
import ferrefix.ms_inventario.dto.ProductoResponseDTO;
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

    // Inyectando el logger
    private static final Logger logger = LoggerFactory.getLogger(ProductoService.class);

    public Producto crearProducto(ProductoRequestDTO dto) {
        logger.info("Iniciando creación de producto: '{}' con código: {}", dto.getNombre(), dto.getCodigoBarras());

        if (productoRepository.existsByCodigoBarrasProducto(dto.getCodigoBarras())) {
            logger.warn("Fallo al crear producto: El código de barras {} ya está registrado", dto.getCodigoBarras());
            throw new IllegalArgumentException("Ya existe un producto con código de barras " + dto.getCodigoBarras());
        }

        CategoriaProducto categoria = categoriaProductoRepository.findById(dto.getCategoria())
                .orElseThrow(() -> {
                    logger.warn("Fallo al crear producto: Categoría ID {} no encontrada", dto.getCategoria());
                    return new IllegalArgumentException("No se encontró la categoría de producto con id: " + dto.getCategoria());
                });

        UnidadMedida unidadMedida = unidadMedidaRepository.findById(dto.getUnidadMedida())
                .orElseThrow(() -> {
                    logger.warn("Fallo al crear producto: Unidad de Medida ID {} no encontrada", dto.getUnidadMedida());
                    return new IllegalArgumentException("No se encontró la unidad de medida con id: " + dto.getUnidadMedida());
                });

        Producto producto = Producto.builder()
                .categoriaProducto(categoria)
                .unidadMedida(unidadMedida)
                .codigoBarrasProducto(dto.getCodigoBarras())
                .nombreProducto(dto.getNombre())
                .stockProducto(dto.getStock())
                .precioVentaProducto(dto.getPrecioVenta())
                .build();

        Producto productoGuardado = productoRepository.save(producto);
        logger.info("Producto '{}' creado exitosamente con ID interno: {}", dto.getNombre(), productoGuardado.getIdProducto());
        return productoGuardado;
    }

    public List<ProductoResponseDTO> buscarTodosProductos() {
        logger.info("Listando todos los productos del inventario");
        return productoRepository.findAll().stream()
                .map(this::mapToDTO)
                .toList();
    }

    public ProductoResponseDTO buscarProductoPorId(Long idProducto) {
        logger.info("Buscando producto por ID: {}", idProducto);
        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> {
                    logger.warn("Búsqueda fallida: No existe producto con ID: {}", idProducto);
                    return new IllegalArgumentException("No se encontró el producto con id: " + idProducto);
                });
        return mapToDTO(producto);
    }

    public Producto actualizarProducto(Long idProducto, ProductoRequestDTO dto) {
        logger.info("Actualizando producto con ID: {}. Nuevo nombre: '{}'", idProducto, dto.getNombre());

        Producto productoExistente = productoRepository.findById(idProducto)
                .orElseThrow(() -> {
                    logger.warn("Fallo al actualizar: Producto con ID {} no encontrado", idProducto);
                    return new IllegalArgumentException("No se encontró el producto con id: " + idProducto);
                });

        productoRepository.findByCodigoBarrasProducto(dto.getCodigoBarras())
                .filter(producto -> !producto.getIdProducto().equals(idProducto))
                .ifPresent(producto -> {
                    logger.warn("Conflicto de actualización: El código de barras {} ya pertenece a otro producto", dto.getCodigoBarras());
                    throw new IllegalArgumentException("Ya existe un producto con código de barras " + dto.getCodigoBarras());
                });

        CategoriaProducto categoria = categoriaProductoRepository.findById(dto.getCategoria())
                .orElseThrow(() -> {
                    logger.warn("Fallo al actualizar: Nueva categoría ID {} no encontrada", dto.getCategoria());
                    return new IllegalArgumentException("No se encontró la categoría de producto con id: " + dto.getCategoria());
                });

        UnidadMedida unidadMedida = unidadMedidaRepository.findById(dto.getUnidadMedida())
                .orElseThrow(() -> {
                    logger.warn("Fallo al actualizar: Nueva unidad de medida ID {} no encontrada", dto.getUnidadMedida());
                    return new IllegalArgumentException("No se encontró la unidad de medida con id: " + dto.getUnidadMedida());
                });

        productoExistente.setCategoriaProducto(categoria);
        productoExistente.setUnidadMedida(unidadMedida);
        productoExistente.setCodigoBarrasProducto(dto.getCodigoBarras());
        productoExistente.setNombreProducto(dto.getNombre());
        productoExistente.setStockProducto(dto.getStock());
        productoExistente.setPrecioVentaProducto(dto.getPrecioVenta());

        Producto productoActualizado = productoRepository.save(productoExistente);
        logger.info("Producto ID {} ('{}') actualizado correctamente", idProducto, dto.getNombre());
        return productoActualizado;
    }

    public void eliminarProducto(Long idProducto) {
        logger.info("Iniciando eliminación de producto con ID: {}", idProducto);
        
        if (!productoRepository.existsById(idProducto)) {
            logger.warn("Fallo al eliminar: No existe producto con ID: {}", idProducto);
            throw new IllegalArgumentException("No se encontró el producto con id: " + idProducto);
        }
        
        productoRepository.deleteById(idProducto);
        logger.info("Producto con ID: {} eliminado exitosamente", idProducto);
    }

    private ProductoResponseDTO mapToDTO(Producto producto) {
        return ProductoResponseDTO.builder()
                .id(producto.getIdProducto().intValue())
                .codigoBarras(producto.getCodigoBarrasProducto())
                .nombre(producto.getNombreProducto())
                .stock(producto.getStockProducto())
                .precioVenta(producto.getPrecioVentaProducto())
                .unidadMedida(producto.getUnidadMedida().getIdUnidadMedida())
                .categoria(producto.getCategoriaProducto().getNombreCategoria())
                .build();
    }
}
