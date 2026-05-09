package ferrefix.ms_inventario.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import ferrefix.ms_inventario.dto.UnidadMedidaRequestDTO;
import ferrefix.ms_inventario.dto.UnidadMedidaResponseDTO;
import ferrefix.ms_inventario.model.UnidadMedida;
import ferrefix.ms_inventario.repository.UnidadMedidaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class UnidadMedidaService {

    private final UnidadMedidaRepository unidadMedidaRepository;
    
    // Inyectando el logger de slf4j
    private static final Logger logger = LoggerFactory.getLogger(UnidadMedidaService.class);

    public UnidadMedida crearUnidadMedida(UnidadMedidaRequestDTO dto) {
        logger.info("Iniciando creación de Unidad de Medida: '{}'", dto.getNombreUnidadMedida());

        if (unidadMedidaRepository.existsByNombreUnidadMedida(dto.getNombreUnidadMedida())) {
            logger.warn("Fallo al crear: La unidad de medida '{}' ya existe en la base de datos", dto.getNombreUnidadMedida());
            throw new IllegalArgumentException("La unidad de medida con nombre " + dto.getNombreUnidadMedida() + " ya existe");
        }

        UnidadMedida unidad = UnidadMedida.builder()
                .nombreUnidadMedida(dto.getNombreUnidadMedida())
                .build();
        
        UnidadMedida unidadGuardada = unidadMedidaRepository.save(unidad);
        logger.info("Unidad de Medida '{}' creada exitosamente con ID: {}", dto.getNombreUnidadMedida(), unidadGuardada.getIdUnidadMedida());
        
        return unidadGuardada;
    }

    public List<UnidadMedidaResponseDTO> buscarTodasUnidadesMedida() {
        logger.info("Consultando listado completo de unidades de medida");
        return unidadMedidaRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public UnidadMedidaResponseDTO buscarUnidadMedidaPorId(Integer id) {
        logger.info("Buscando unidad de medida por ID: {}", id);
        
        UnidadMedida unidad = unidadMedidaRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Búsqueda fallida: No se encontró la unidad de medida con ID: {}", id);
                    return new IllegalArgumentException("No se encontró la unidad de medida con id: " + id);
                });
                
        return toResponse(unidad);
    }

    public UnidadMedida actualizarUnidadMedida(Integer id, UnidadMedidaRequestDTO dto) {
        logger.info("Solicitud para actualizar unidad de medida ID: {}", id);

        UnidadMedida unidadExistente = unidadMedidaRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Fallo al actualizar: No existe unidad de medida con ID: {}", id);
                    return new IllegalArgumentException("No se encontró la unidad de medida con id: " + id);
                });

        if (unidadMedidaRepository.existsByNombreUnidadMedida(dto.getNombreUnidadMedida())
                && !unidadExistente.getNombreUnidadMedida().equalsIgnoreCase(dto.getNombreUnidadMedida())) {
            logger.warn("Conflicto de nombre: Se intentó renombrar a '{}', pero ese nombre ya existe", dto.getNombreUnidadMedida());
            throw new IllegalArgumentException("La unidad de medida con nombre " + dto.getNombreUnidadMedida() + " ya existe");
        }

        String nombreAnterior = unidadExistente.getNombreUnidadMedida();
        unidadExistente.setNombreUnidadMedida(dto.getNombreUnidadMedida());
        UnidadMedida unidadActualizada = unidadMedidaRepository.save(unidadExistente);
        
        logger.info("Unidad de Medida ID: {} actualizada. (Anterior: '{}' -> Nueva: '{}')", 
                    id, nombreAnterior, dto.getNombreUnidadMedida());
                    
        return unidadActualizada;
    }

    public void eliminarUnidadMedida(Integer id) {
        logger.info("Solicitud de eliminación para unidad de medida ID: {}", id);
        
        if (!unidadMedidaRepository.existsById(id)) {
            logger.warn("Fallo al eliminar: No se encontró la unidad de medida con ID: {}", id);
            throw new IllegalArgumentException("No se encontró la unidad de medida con id: " + id);
        }
        
        unidadMedidaRepository.deleteById(id);
        logger.info("Unidad de medida con ID: {} eliminada correctamente", id);
    }

    private UnidadMedidaResponseDTO toResponse(UnidadMedida unidad) {
        return UnidadMedidaResponseDTO.builder()
                .idUnidadMedida(unidad.getIdUnidadMedida())
                .nombreUnidadMedida(unidad.getNombreUnidadMedida())
                .build();
    }
}
