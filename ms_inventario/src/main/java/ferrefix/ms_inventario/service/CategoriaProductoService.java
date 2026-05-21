package ferrefix.ms_inventario.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import ferrefix.ms_inventario.dto.CategoriaProductoRequestDTO;
import ferrefix.ms_inventario.dto.CategoriaProductoResponseDTO;
import ferrefix.ms_inventario.exception.BadRequestException;
import ferrefix.ms_inventario.exception.ResourceNotFoundException;
import ferrefix.ms_inventario.mapper.CategoriaProductoMapper;
import ferrefix.ms_inventario.model.CategoriaProducto;
import ferrefix.ms_inventario.repository.CategoriaProductoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoriaProductoService {

    private static final Logger logger = LoggerFactory.getLogger(CategoriaProductoService.class);
    private final CategoriaProductoRepository categoriaProductoRepository;

    // Inyeccion de los mapper para los mappeos
    private final CategoriaProductoMapper categoriaProductoMapper;

    public CategoriaProductoResponseDTO crearCategoriaProducto(CategoriaProductoRequestDTO dto) {
        logger.info("Iniciando creación de categoría: '{}'", dto.getNombreCategoria());

        if (categoriaProductoRepository.existsByNombreCategoria(dto.getNombreCategoria())) {
            logger.warn("Conflicto: la categoría '{}' ya existe", dto.getNombreCategoria());
            throw new BadRequestException("La categoría de producto '" + dto.getNombreCategoria() + "' ya existe.");
        }

        CategoriaProducto guardada = categoriaProductoMapper.toEntity(dto);
        guardada = categoriaProductoRepository.save(guardada);
        logger.info("Categoría '{}' creada exitosamente con ID: {}", guardada.getNombreCategoria(), guardada.getIdCategoria());
        return categoriaProductoMapper.toResponseDTO(guardada);
    }

    public List<CategoriaProductoResponseDTO> buscarTodasCategorias() {
        logger.info("Listando todas las categorías de productos");
        List<CategoriaProductoResponseDTO> lista = categoriaProductoRepository.findAll().stream()
                .map(cat -> categoriaProductoMapper.toResponseDTO(cat))
                .toList();
        logger.info("Listado completado. Total: {}", lista.size());
        return lista;
    }

    public CategoriaProductoResponseDTO buscarCategoriaPorId(Integer id) {
        logger.info("Buscando categoría por ID: {}", id);
        CategoriaProducto categoria = categoriaProductoRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("404 - Categoría ID {} no encontrada", id);
                    return new ResourceNotFoundException("No se encontró la categoría con id: " + id);
                });
        logger.info("Categoría ID {} encontrada: '{}'", id, categoria.getNombreCategoria());
        return categoriaProductoMapper.toResponseDTO(categoria);
    }

    public CategoriaProductoResponseDTO actualizarCategoriaProducto(Integer id, CategoriaProductoRequestDTO dto) {
        logger.info("Iniciando actualización de categoría ID: {}", id);

        CategoriaProducto existente = categoriaProductoRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("404 - Categoría ID {} no encontrada para actualizar", id);
                    return new ResourceNotFoundException("No se encontró la categoría con id: " + id);
                });

        if (categoriaProductoRepository.existsByNombreCategoria(dto.getNombreCategoria())
                && !existente.getNombreCategoria().equalsIgnoreCase(dto.getNombreCategoria())) {
            logger.warn("Conflicto: el nombre '{}' ya pertenece a otra categoría", dto.getNombreCategoria());
            throw new BadRequestException("La categoría de producto '" + dto.getNombreCategoria() + "' ya existe.");
        }

        // Aplicando actualizacion de la entidad con el Response DTO
        categoriaProductoMapper.updateEntity(existente, dto);
        CategoriaProducto actualizada = categoriaProductoRepository.save(existente);
        logger.info("Categoría ID {} actualizada exitosamente a '{}'", id, actualizada.getNombreCategoria());
        return categoriaProductoMapper.toResponseDTO(actualizada);
    }

    public void eliminarCategoriaProducto(Integer id) {
        logger.info("Iniciando eliminación de categoría ID: {}", id);
        if (!categoriaProductoRepository.existsById(id)) {
            logger.warn("404 - Categoría ID {} no encontrada para eliminar", id);
            throw new ResourceNotFoundException("No se encontró la categoría con id: " + id);
        }
        categoriaProductoRepository.deleteById(id);
        logger.info("Categoría ID {} eliminada exitosamente", id);
    }
}
