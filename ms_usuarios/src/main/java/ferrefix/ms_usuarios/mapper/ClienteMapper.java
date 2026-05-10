package ferrefix.ms_usuarios.mapper;

import org.springframework.stereotype.Component;
import ferrefix.ms_usuarios.dto.ClienteRequestDTO;
import ferrefix.ms_usuarios.dto.ClienteResponseDTO;
import ferrefix.ms_usuarios.model.Cliente;
import java.time.LocalDate;

@Component
public class ClienteMapper {

    /**
     * Convierte el DTO de entrada en una Entidad Cliente.
     * Recibe el dv ya transformado a Character desde el Service.
     */
    public Cliente toEntity(ClienteRequestDTO dto, Character dvChar) {
        return Cliente.builder()
                .runCliente(dto.getRunCliente())
                .dvCliente(dvChar)
                .pnombreCliente(dto.getPnombreCliente())
                .snombreCliente(dto.getSnombreCliente())
                .appaternoCliente(dto.getAppaternoCliente())
                .apmaternoCliente(dto.getApmaternoCliente())
                .fechaNacimientoCliente(dto.getFechaNacimientoCliente())
                .emailCliente(dto.getEmailCliente())
                .contrasenaCliente(dto.getContrasenaCliente())
                .telefonoCliente(dto.getTelefonoCliente())
                .fechaRegistroCliente(LocalDate.now())
                .idDireccion(dto.getIdDireccion())
                .build();
    }

    /**
     * Actualiza una entidad existente con los datos del DTO.
     */
    public void updateEntity(Cliente existing, ClienteRequestDTO dto, Character dvChar) {
        existing.setDvCliente(dvChar);
        existing.setPnombreCliente(dto.getPnombreCliente());
        existing.setSnombreCliente(dto.getSnombreCliente());
        existing.setAppaternoCliente(dto.getAppaternoCliente());
        existing.setApmaternoCliente(dto.getApmaternoCliente());
        existing.setFechaNacimientoCliente(dto.getFechaNacimientoCliente());
        existing.setEmailCliente(dto.getEmailCliente());
        existing.setContrasenaCliente(dto.getContrasenaCliente());
        existing.setTelefonoCliente(dto.getTelefonoCliente());
        existing.setIdDireccion(dto.getIdDireccion());
    }

    /**
     * Convierte la Entidad Cliente en un DTO de respuesta enriquecido.
     */
    public ClienteResponseDTO toResponseDTO(Cliente entity) {
        // Concatenación de RUT
        String runCompleto = entity.getRunCliente() + "-" + entity.getDvCliente();

        // Concatenación de Nombre Completo
        String sNombre = (entity.getSnombreCliente() != null && !entity.getSnombreCliente().trim().isEmpty()) 
                 ? entity.getSnombreCliente() + " " : "";
        String nombreCompleto = entity.getPnombreCliente() + " " + sNombre + 
                                entity.getAppaternoCliente() + " " + entity.getApmaternoCliente();

        return ClienteResponseDTO.builder()
                .runClienteCompleto(runCompleto)
                .nombreClienteCompleto(nombreCompleto.trim())
                .emailCliente(entity.getEmailCliente())
                .telefonoCliente(entity.getTelefonoCliente())
                .idDireccion(entity.getIdDireccion())
                .build();
    }
}