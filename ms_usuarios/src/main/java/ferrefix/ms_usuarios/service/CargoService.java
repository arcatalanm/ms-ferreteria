package ferrefix.ms_usuarios.service;

import java.util.List;

import org.springframework.stereotype.Service;

import ferrefix.ms_usuarios.dto.CargoRequestDTO;
import ferrefix.ms_usuarios.dto.CargoResponseDTO;
import ferrefix.ms_usuarios.model.Cargo;
import ferrefix.ms_usuarios.repository.CargoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class CargoService {
    private final CargoRepository cargoRepository;

    // Metodo para crear un nuevo cargo
    public Cargo crearCargo(CargoRequestDTO dto) {
        // Validacion de que no exista un cargo con el mismo nombre para evitar duplicados
        if (cargoRepository.existsByNombreCargo(dto.getNombreCargo())) {
            throw new IllegalArgumentException("El cargo '" + dto.getNombreCargo() + "' ya existe");
        }
        
        Cargo cargo = Cargo.builder()
                .nombreCargo(dto.getNombreCargo())
                .build();

        return cargoRepository.save(cargo);
    }

    // Metodo que lista todos los cargos disponibles
    public List<CargoResponseDTO> buscarTodos() {
        return cargoRepository.findAll().stream()
                .map(this::mapToDTO)
                .toList();
    }

    public CargoResponseDTO buscarCargoPorId(Integer idCargo) {
        Cargo cargo = cargoRepository.findById(idCargo)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el cargo con id " + idCargo));
        return mapToDTO(cargo);
    }

    public Cargo actualizarCargo(Integer idCargo, CargoRequestDTO dto) {
        Cargo cargoExistente = cargoRepository.findById(idCargo)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el cargo con id " + idCargo));
        if (cargoRepository.existsByNombreCargo(dto.getNombreCargo())
                && !cargoExistente.getNombreCargo().equalsIgnoreCase(dto.getNombreCargo())) {
            throw new IllegalArgumentException("El cargo '" + dto.getNombreCargo() + "' ya existe");
        }
        cargoExistente.setNombreCargo(dto.getNombreCargo());
        return cargoRepository.save(cargoExistente);
    }

    public void eliminarCargo(Integer idCargo) {
        if (!cargoRepository.existsById(idCargo)) {
            throw new IllegalArgumentException("No se encontró el cargo con id " + idCargo);
        }
        cargoRepository.deleteById(idCargo);
    }

    // Mappeo de entidad Cargo a DTO como respuesta
    private CargoResponseDTO mapToDTO(Cargo cargo) {
        return CargoResponseDTO.builder()
                .idCargo(cargo.getIdCargo())
                .nombreCargo(cargo.getNombreCargo())
                .build();
    }
}
