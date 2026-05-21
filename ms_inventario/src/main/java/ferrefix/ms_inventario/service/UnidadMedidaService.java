package ferrefix.ms_inventario.service;


import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import ferrefix.ms_inventario.dto.UnidadMedidaRequestDTO;
import ferrefix.ms_inventario.dto.UnidadMedidaResponseDTO;
import ferrefix.ms_inventario.exception.BadRequestException;
import ferrefix.ms_inventario.exception.ResourceNotFoundException;
import ferrefix.ms_inventario.mapper.UnidadMedidaMapper;
import ferrefix.ms_inventario.model.UnidadMedida;
import ferrefix.ms_inventario.repository.UnidadMedidaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class UnidadMedidaService {

    private static final Logger logger = LoggerFactory.getLogger(UnidadMedidaService.class);
    private final UnidadMedidaRepository unidadMedidaRepository;

    // Inyeccion del mapper
    private final UnidadMedidaMapper mapper;

    public UnidadMedidaResponseDTO crearUnidadMedida(UnidadMedidaRequestDTO dto) {
        logger.info("Iniciando creación de unidad de medida: '{}'", dto.getNombreUnidadMedida());

        if (unidadMedidaRepository.existsByNombreUnidadMedida(dto.getNombreUnidadMedida())) {
            logger.warn("Conflicto: la unidad '{}' ya existe", dto.getNombreUnidadMedida());
            throw new BadRequestException("La unidad de medida '" + dto.getNombreUnidadMedida() + "' ya existe.");
        }

        UnidadMedida entidadParaGuardar = mapper.toEntity(dto);
        UnidadMedida unidadGuardadda = unidadMedidaRepository.save(entidadParaGuardar);
        logger.info("Unidad '{}' creada exitosamente con ID: {}", 
            unidadGuardadda.getNombreUnidadMedida(),
            unidadGuardadda.getIdUnidadMedida());
        return mapper.toDTO(unidadGuardadda);
    }

    public List<UnidadMedidaResponseDTO> buscarTodasUnidadesMedida() {
        logger.info("Listando todas las unidades de medida");
        List<UnidadMedidaResponseDTO> lista = unidadMedidaRepository.findAll().stream()
                .map(unidad -> mapper.toDTO(unidad))
                .toList();
        logger.info("Listado completado. Total: {}", lista.size());
        return lista;
    }

    public UnidadMedidaResponseDTO buscarUnidadMedidaPorId(Integer id) {
        logger.info("Buscando unidad de medida por ID: {}", id);
        UnidadMedida unidad = unidadMedidaRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("404 - Unidad de medida ID {} no encontrada", id);
                    return new ResourceNotFoundException("No se encontró la unidad de medida con id: " + id);
                });
        logger.info("Unidad ID {} encontrada: '{}'", id, unidad.getNombreUnidadMedida());
        return mapper.toDTO(unidad);
    }

    public UnidadMedidaResponseDTO actualizarUnidadMedida(Integer id, UnidadMedidaRequestDTO dto) {
        logger.info("Iniciando actualización de unidad de medida ID: {}", id);

        UnidadMedida existente = unidadMedidaRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("404 - Unidad de medida ID {} no encontrada para actualizar", id);
                    return new ResourceNotFoundException("No se encontró la unidad de medida con id: " + id);
                });

        if (unidadMedidaRepository.existsByNombreUnidadMedida(dto.getNombreUnidadMedida())
                && !existente.getNombreUnidadMedida().equalsIgnoreCase(dto.getNombreUnidadMedida())) {
            logger.warn("Conflicto: el nombre '{}' ya pertenece a otra unidad", dto.getNombreUnidadMedida());
            throw new BadRequestException("La unidad de medida '" + dto.getNombreUnidadMedida() + "' ya existe.");
        }

        // Mappeamos el dto a Entidad
        mapper.updateEntity(existente, dto);

        // Guardando entidad modificada
        UnidadMedida unidadGuardada = unidadMedidaRepository.save(existente);

        logger.info("Unidad ID {} actualizada exitosamente a '{}'", id, unidadGuardada.getNombreUnidadMedida());
        return mapper.toDTO(unidadGuardada);
    }

    public void eliminarUnidadMedida(Integer id) {
        logger.info("Iniciando eliminación de unidad de medida ID: {}", id);
        if (!unidadMedidaRepository.existsById(id)) {
            logger.warn("404 - Unidad de medida ID {} no encontrada para eliminar", id);
            throw new ResourceNotFoundException("No se encontró la unidad de medida con id: " + id);
        }
        unidadMedidaRepository.deleteById(id);
        logger.info("Unidad de medida ID {} eliminada exitosamente", id);
    }
}