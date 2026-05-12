package ferrefix.ms_direcciones.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ferrefix.ms_direcciones.dto.DireccionRequestDTO;
import ferrefix.ms_direcciones.dto.DireccionResponseDTO;
import ferrefix.ms_direcciones.exception.ResourceNotFoundException;
import ferrefix.ms_direcciones.mapper.DireccionMapper;
import ferrefix.ms_direcciones.model.Direccion;
import ferrefix.ms_direcciones.repository.DireccionRepository;

import lombok.RequiredArgsConstructor;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class DireccionService {

    private static final Logger logger = LoggerFactory.getLogger(DireccionService.class);
    
    private final DireccionRepository direccionRepository;
    private final DireccionMapper direccionMapper;

    public DireccionResponseDTO crearDireccion(DireccionRequestDTO dto) {
        logger.info("Iniciando creación de nueva dirección");
        
        Direccion direccion = direccionMapper.toEntity(dto);
        Direccion direccionGuardada = direccionRepository.save(direccion);
        
        logger.info("Dirección creada exitosamente con ID: {}", direccionGuardada.getIdDireccion());
        return direccionMapper.toResponseDTO(direccionGuardada);
    }

    public List<DireccionResponseDTO> buscarTodas() {
        logger.info("Iniciando búsqueda de todas las direcciones");
        
        List<DireccionResponseDTO> direcciones = direccionRepository.findAll().stream()
                .map(direccionMapper::toResponseDTO)
                .toList();
                
        logger.info("Búsqueda completada. Total encontrado: {}", direcciones.size());
        return direcciones;
    }

    public DireccionResponseDTO buscarPorId(Long idDireccion) {
        logger.info("Buscando dirección con ID: {}", idDireccion);
        
        return direccionRepository.findById(idDireccion)
                .map(direccionMapper::toResponseDTO)
                .orElseThrow(() -> {
                    logger.warn("Búsqueda fallida: No se encontró dirección con ID: {}", idDireccion);
                    return new ResourceNotFoundException("No se encontró la dirección con ID: " + idDireccion);
                });
    }

    public DireccionResponseDTO actualizarDireccion(Long idDireccion, DireccionRequestDTO dto) {
        logger.info("Iniciando actualización de dirección con ID: {}", idDireccion);

        Direccion direccionExistente = direccionRepository.findById(idDireccion)
                .orElseThrow(() -> {
                    logger.warn("Fallo al actualizar: No se encontró dirección con ID: {}", idDireccion);
                    return new ResourceNotFoundException("No se encontró la dirección con ID: " + idDireccion);
                });

        direccionMapper.updateEntity(direccionExistente, dto);
        Direccion direccionActualizada = direccionRepository.save(direccionExistente);
        
        logger.info("Dirección ID {} actualizada exitosamente", idDireccion);
        return direccionMapper.toResponseDTO(direccionActualizada);
    }

    public void eliminarDireccion(Long idDireccion) {
        logger.info("Iniciando eliminación de dirección con ID: {}", idDireccion);
        
        if (!direccionRepository.existsById(idDireccion)) {
            logger.warn("Fallo al eliminar: No se encontró dirección con ID: {}", idDireccion);
            throw new ResourceNotFoundException("No se encontró la dirección con ID: " + idDireccion);
        }
        
        direccionRepository.deleteById(idDireccion);
        logger.info("Dirección ID {} eliminada exitosamente", idDireccion);
    }
}