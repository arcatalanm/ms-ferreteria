package ferrefix.ms_inventorario.service;

import java.util.List;

import org.springframework.stereotype.Service;

import ferrefix.ms_inventorario.dto.ProductoRequestDTO;
import ferrefix.ms_inventorario.dto.ProductoResponseDTO;
import ferrefix.ms_inventorario.model.CategoriaProducto;
import ferrefix.ms_inventorario.model.Producto;
import ferrefix.ms_inventorario.model.UnidadMedida;
import ferrefix.ms_inventorario.repository.CategoriaProductoRepository;
import ferrefix.ms_inventorario.repository.ProductoRepository;
import ferrefix.ms_inventorario.repository.UnidadMedidaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductoService {
    private final ProductoRepository productoRepository;
    private final CategoriaProductoRepository categoriaProductoRepository;
    private final UnidadMedidaRepository unidadMedidaRepository;

    public Producto crearProducto(ProductoRequestDTO dto) {
        if (productoRepository.existsByCodigoBarrasProducto(dto.getCodigoBarras())) {
            throw new IllegalArgumentException("Ya existe un producto con código de barras " + dto.getCodigoBarras());
        }

        CategoriaProducto categoria = categoriaProductoRepository.findById(dto.getCategoria())
                .orElseThrow(() -> new IllegalArgumentException("No se encontró la categoría de producto con id: " + dto.getCategoria()));

        UnidadMedida unidadMedida = unidadMedidaRepository.findById(dto.getUnidadMedida())
                .orElseThrow(() -> new IllegalArgumentException("No se encontró la unidad de medida con id: " + dto.getUnidadMedida()));

        Producto producto = Producto.builder()
                .categoriaProducto(categoria)
                .unidadMedida(unidadMedida)
                .codigoBarrasProducto(dto.getCodigoBarras())
                .nombreProducto(dto.getNombre())
                .stockProducto(dto.getStock())
                .precioVentaProducto(dto.getPrecioVenta())
                .build();

        return productoRepository.save(producto);
    }

    public List<ProductoResponseDTO> buscarTodosProductos() {
        return productoRepository.findAll().stream()
                .map(this::mapToDTO)
                .toList();
    }

    public ProductoResponseDTO buscarProductoPorId(Long idProducto) {
        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el producto con id: " + idProducto));
        return mapToDTO(producto);
    }

    public Producto actualizarProducto(Long idProducto, ProductoRequestDTO dto) {
        Producto productoExistente = productoRepository.findById(idProducto)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el producto con id: " + idProducto));

        productoRepository.findByCodigoBarrasProducto(dto.getCodigoBarras())
                .filter(producto -> !producto.getIdProducto().equals(idProducto))
                .ifPresent(producto -> {
                    throw new IllegalArgumentException("Ya existe un producto con código de barras " + dto.getCodigoBarras());
                });

        CategoriaProducto categoria = categoriaProductoRepository.findById(dto.getCategoria())
                .orElseThrow(() -> new IllegalArgumentException("No se encontró la categoría de producto con id: " + dto.getCategoria()));

        UnidadMedida unidadMedida = unidadMedidaRepository.findById(dto.getUnidadMedida())
                .orElseThrow(() -> new IllegalArgumentException("No se encontró la unidad de medida con id: " + dto.getUnidadMedida()));

        productoExistente.setCategoriaProducto(categoria);
        productoExistente.setUnidadMedida(unidadMedida);
        productoExistente.setCodigoBarrasProducto(dto.getCodigoBarras());
        productoExistente.setNombreProducto(dto.getNombre());
        productoExistente.setStockProducto(dto.getStock());
        productoExistente.setPrecioVentaProducto(dto.getPrecioVenta());

        return productoRepository.save(productoExistente);
    }

    public void eliminarProducto(Long idProducto) {
        if (!productoRepository.existsById(idProducto)) {
            throw new IllegalArgumentException("No se encontró el producto con id: " + idProducto);
        }
        productoRepository.deleteById(idProducto);
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
