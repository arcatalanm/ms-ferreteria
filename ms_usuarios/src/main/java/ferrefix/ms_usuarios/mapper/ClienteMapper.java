package ferrefix.ms_usuarios.mapper;

import org.springframework.stereotype.Component;
import ferrefix.ms_usuarios.dto.ClienteRequestDTO;
import ferrefix.ms_usuarios.dto.ClienteResponseDTO;
import ferrefix.ms_usuarios.dto.DireccionDTO;
import ferrefix.ms_usuarios.model.Cliente;
import java.time.LocalDate;

@Component
public class ClienteMapper {

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
                .idDireccion(dto.getIdDireccion()) // <-- CORREGIDO
                .build();
    }

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
        existing.setIdDireccion(dto.getIdDireccion()); // <-- CORREGIDO
    }

    public ClienteResponseDTO toResponseDTO(Cliente entity, DireccionDTO direccionDTO) {
        String runCompleto = entity.getRunCliente() + "-" + entity.getDvCliente();

        String sNombre = (entity.getSnombreCliente() != null && !entity.getSnombreCliente().trim().isEmpty()) 
                 ? entity.getSnombreCliente() + " " : "";
        String nombreCompleto = entity.getPnombreCliente() + " " + sNombre + 
                                entity.getAppaternoCliente() + " " + entity.getApmaternoCliente();
        
        // --- PROTECCIÓN CONTRA CAÍDAS DEL MS-DIRECCIONES ---
        String dirCompleta = "Dirección no disponible"; 
        
        if (direccionDTO != null) {
            dirCompleta = direccionDTO.getCalle() + " " + direccionDTO.getNumero();
            if (direccionDTO.getDepartamento() != null && !direccionDTO.getDepartamento().isBlank()) {
                dirCompleta += " Depto. " + direccionDTO.getDepartamento();
            }
        }

        return ClienteResponseDTO.builder()
                .runClienteCompleto(runCompleto)
                .nombreClienteCompleto(nombreCompleto.trim())
                .emailCliente(entity.getEmailCliente())
                .telefonoCliente(entity.getTelefonoCliente())
                .direccionCliente(dirCompleta) 
                .build();
    }
}