package ferrefix.ms_usuarios.mapper;

import org.springframework.stereotype.Component;
import ferrefix.ms_usuarios.dto.CargoRequestDTO;
import ferrefix.ms_usuarios.dto.CargoResponseDTO;
import ferrefix.ms_usuarios.model.Cargo;

@Component
public class CargoMapper {

    public Cargo toEntity(CargoRequestDTO dto) {
        return Cargo.builder()
                .nombreCargo(dto.getNombreCargo())
                .build();
    }

    public void updateEntity(Cargo cargo, CargoRequestDTO dto) {
        cargo.setNombreCargo(dto.getNombreCargo());
    }

    public CargoResponseDTO toResponseDTO(Cargo cargo) {
        return CargoResponseDTO.builder()
                .idCargo(cargo.getIdCargo())
                .nombreCargo(cargo.getNombreCargo())
                .build();
    }
}