package ferrefix.ms_usuarios.service;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ferrefix.ms_usuarios.dto.CargoRequestDTO;
import ferrefix.ms_usuarios.dto.CargoResponseDTO;
import ferrefix.ms_usuarios.exception.BadRequestException;
import ferrefix.ms_usuarios.exception.ResourceNotFoundException;
import ferrefix.ms_usuarios.mapper.CargoMapper;
import ferrefix.ms_usuarios.model.Cargo;
import ferrefix.ms_usuarios.repository.CargoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class CargoService {

    private static final Logger logger = LoggerFactory.getLogger(CargoService.class);
    
    private final CargoRepository cargoRepository;
    private final CargoMapper cargoMapper; // Inyectamos el Mapper

    public Cargo crearCargo(CargoRequestDTO dto) {
        logger.info("Iniciando creación de cargo: '{}'", dto.getNombreCargo());

        if (cargoRepository.existsByNombreCargo(dto.getNombreCargo())) {
            logger.warn("Conflicto al crear: El cargo '{}' ya existe", dto.getNombreCargo());
            throw new BadRequestException("El cargo '" + dto.getNombreCargo() + "' ya existe");
        }
        
        Cargo cargo = cargoMapper.toEntity(dto); // Delegamos la construcción
        Cargo cargoGuardado = cargoRepository.save(cargo);
        
        logger.info("Cargo creado exitosamente con ID: {}", cargoGuardado.getIdCargo());
        return cargoGuardado;
    }

    public List<CargoResponseDTO> buscarTodos() {
        logger.info("Iniciando búsqueda de todos los cargos");
        List<CargoResponseDTO> cargos = cargoRepository.findAll().stream()
                .map(cargoMapper::toResponseDTO) // Delegamos el mapeo
                .toList();
        logger.info("Búsqueda completada. Total de cargos encontrados: {}", cargos.size());
        return cargos;
    }

    public CargoResponseDTO buscarCargoPorId(Integer idCargo) {
        logger.info("Buscando cargo con ID: {}", idCargo);
        return cargoRepository.findById(idCargo)
                .map(cargoMapper::toResponseDTO) // Delegamos el mapeo
                .orElseThrow(() -> {
                    logger.warn("Búsqueda fallida: No se encontró el cargo con ID: {}", idCargo);
                    return new ResourceNotFoundException("No se encontró el cargo con id " + idCargo);
                });
    }

    public Cargo actualizarCargo(Integer idCargo, CargoRequestDTO dto) {
        logger.info("Iniciando actualización de cargo con ID: {}", idCargo);

        Cargo cargoExistente = cargoRepository.findById(idCargo)
                .orElseThrow(() -> {
                    logger.warn("Fallo al actualizar: No se encontró el cargo con ID: {}", idCargo);
                    return new ResourceNotFoundException("No se encontró el cargo con id " + idCargo);
                });

        if (cargoRepository.existsByNombreCargo(dto.getNombreCargo())
                && !cargoExistente.getNombreCargo().equalsIgnoreCase(dto.getNombreCargo())) {
            logger.warn("Conflicto al actualizar: El nombre '{}' ya pertenece a otro cargo", dto.getNombreCargo());
            throw new BadRequestException("El cargo '" + dto.getNombreCargo() + "' ya existe");
        }

        cargoMapper.updateEntity(cargoExistente, dto); // Delegamos la actualización
        Cargo cargoActualizado = cargoRepository.save(cargoExistente);
        
        logger.info("Cargo con ID {} actualizado exitosamente", idCargo);
        return cargoActualizado;
    }

    public void eliminarCargo(Integer idCargo) {
        logger.info("Iniciando eliminación de cargo con ID: {}", idCargo);
        if (!cargoRepository.existsById(idCargo)) {
            logger.warn("Fallo al eliminar: No se encontró el cargo con ID: {}", idCargo);
            throw new ResourceNotFoundException("No se encontró el cargo con id " + idCargo);
        }
        cargoRepository.deleteById(idCargo);
        logger.info("Cargo con ID {} eliminado exitosamente", idCargo);
    }
}