package ferrefix.ms_inventario.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import ferrefix.ms_inventario.dto.CategoriaProductoRequestDTO;
import ferrefix.ms_inventario.dto.CategoriaProductoResponseDTO;
import ferrefix.ms_inventario.model.CategoriaProducto;
import ferrefix.ms_inventario.repository.CategoriaProductoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoriaProductoService {
    // Inyectando el repositorio
    private final CategoriaProductoRepository categoriaProductoRepository;
    // Inyectando logger de la clase {slf4j}
    private static final Logger logger = LoggerFactory.getLogger(CategoriaProductoService.class);

    public CategoriaProducto crearCategoriaProducto(CategoriaProductoRequestDTO dto) {
        logger.info("Creando Categoria del Producto");
        if (categoriaProductoRepository.existsByNombreCategoria(dto.getNombreCategoria())) {
            logger.warn("Fallo al crear: La categoria del producto con nombre '{}' ya existe", dto.getNombreCategoria());
            throw new IllegalArgumentException("La categoría de producto '" + dto.getNombreCategoria() + "' ya existe");
        }

        CategoriaProducto categoria = CategoriaProducto.builder()
                .nombreCategoria(dto.getNombreCategoria())
                .build();
        logger.info("Categoria '{}' guardada exitosamente con ID: {}", dto.getNombreCategoria(), categoria.getIdCategoria());
        return categoriaProductoRepository.save(categoria);
    }

    public List<CategoriaProductoResponseDTO> buscarTodasCategorias() {
        logger.info("Listando todas las categorias de productos");
        return categoriaProductoRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public CategoriaProductoResponseDTO buscarCategoriaPorId(Integer idCategoria) {
        logger.info("Buscando categoria de producto por ID: {}", idCategoria);

        CategoriaProducto categoria = categoriaProductoRepository.findById(idCategoria)
                .orElseThrow(() -> {
                    logger.warn("Busqueda fallida: No se encontro la categoria con ID: {}", idCategoria);
                    return new IllegalArgumentException("No se encontró la categoría de producto con id: " + idCategoria);
                });
                
        logger.info("Categoria encontrada: {}", categoria.getNombreCategoria());
        return toResponse(categoria);
    }

    public CategoriaProducto actualizarCategoriaProducto(Integer idCategoria, CategoriaProductoRequestDTO dto) {
        logger.info("Actualizando categoria de producto con ID: {}", idCategoria);
        
        CategoriaProducto categoriaExistente = categoriaProductoRepository.findById(idCategoria)
                .orElseThrow(() -> {
                    logger.warn("Fallo al actualizar: No se encontro la categoria con ID: {}", idCategoria);
                    return new IllegalArgumentException("No se encontró la categoría de producto con id: " + idCategoria);
                });

        if (categoriaProductoRepository.existsByNombreCategoria(dto.getNombreCategoria())
                && !categoriaExistente.getNombreCategoria().equalsIgnoreCase(dto.getNombreCategoria())) {
            logger.warn("Conflicto al actualizar: La categoria con nombre '{}' ya existe en otro registro", dto.getNombreCategoria());
            throw new IllegalArgumentException("La categoría de producto '" + dto.getNombreCategoria() + "' ya existe");
        }

        categoriaExistente.setNombreCategoria(dto.getNombreCategoria());
        // Categoria actualizada con los valores del dto
        CategoriaProducto categoriaActualizada = categoriaProductoRepository.save(categoriaExistente);
        
        logger.info("Categoria con ID: {} actualizada exitosamente al nuevo nombre: '{}'", idCategoria, dto.getNombreCategoria());
        return categoriaActualizada;
    }

    public void eliminarCategoriaProducto(Integer idCategoria) {
        logger.info("Inciando eliminacion de categoria con ID: {}", idCategoria);
        if (!categoriaProductoRepository.existsById(idCategoria)) {
            logger.warn("Fallo al eliminar: No se encontro la categoria con ID: {}", idCategoria);
            throw new IllegalArgumentException("No se encontró la categoría de producto con id: " + idCategoria);
        }
        categoriaProductoRepository.deleteById(idCategoria);
        // Podemos ejecutar esto despues del deleById porque es una funcion void
        logger.info("Categoria con ID: {} eliminada exitosamente", idCategoria);
    }

    private CategoriaProductoResponseDTO toResponse(CategoriaProducto categoriaProducto) {
        return CategoriaProductoResponseDTO.builder()
                .idCategoria(categoriaProducto.getIdCategoria())
                .nombreCategoria(categoriaProducto.getNombreCategoria())
                .build();
    }
}
