package ferrefix.ms_direcciones.mapper;

import org.springframework.stereotype.Component;
import ferrefix.ms_direcciones.dto.DireccionRequestDTO;
import ferrefix.ms_direcciones.dto.DireccionResponseDTO;
import ferrefix.ms_direcciones.model.Direccion;

@Component
public class DireccionMapper {

    public Direccion toEntity(DireccionRequestDTO dto) {
        return Direccion.builder()
                .calle(dto.getCalle().trim())
                .numero(dto.getNumero())
                .departamento(dto.getDepartamento() != null ? dto.getDepartamento().trim() : null)
                .comuna(dto.getComuna().trim())
                .ciudad(dto.getCiudad().trim())
                .build();
    }

    public void updateEntity(Direccion direccion, DireccionRequestDTO dto) {
        direccion.setCalle(dto.getCalle().trim());
        direccion.setNumero(dto.getNumero());
        direccion.setDepartamento(dto.getDepartamento() != null ? dto.getDepartamento().trim() : null);
        direccion.setComuna(dto.getComuna().trim());
        direccion.setCiudad(dto.getCiudad().trim());
    }

    public DireccionResponseDTO toResponseDTO(Direccion direccion) {
        String deptoStr = (direccion.getDepartamento() != null && !direccion.getDepartamento().isEmpty()) 
                ? ", Depto " + direccion.getDepartamento() 
                : "";
                
        String direccionCompleta = String.format("%s %d%s, %s, %s", 
                direccion.getCalle(), 
                direccion.getNumero(), 
                deptoStr, 
                direccion.getComuna(), 
                direccion.getCiudad());

        return DireccionResponseDTO.builder()
                .idDireccion(direccion.getIdDireccion())
                .calle(direccion.getCalle())
                .numero(direccion.getNumero())
                .departamento(direccion.getDepartamento())
                .comuna(direccion.getComuna())
                .ciudad(direccion.getCiudad())
                .direccionCompleta(direccionCompleta)
                .build();
    }
}