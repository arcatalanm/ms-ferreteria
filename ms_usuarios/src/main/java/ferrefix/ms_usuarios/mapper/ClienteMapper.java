package ferrefix.ms_usuarios.mapper;


import java.time.LocalDate;

import org.springframework.stereotype.Component;

import ferrefix.ms_usuarios.dto.ClienteRequestDTO;
import ferrefix.ms_usuarios.dto.ClienteResponseDTO;
import ferrefix.ms_usuarios.dto.DireccionDTO;
import ferrefix.ms_usuarios.model.Cliente;
import ferrefix.ms_usuarios.util.RutUtil;

@Component
public class ClienteMapper {

    public Cliente toEntity(ClienteRequestDTO dto, Integer run, Character dv) {
        return Cliente.builder()
                .runCliente(run)
                .dvCliente(dv)
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

    public void updateEntity(Cliente existing, ClienteRequestDTO dto, Integer run, Character dv) {
        // El run (PK) no se modifica; solo actualizamos los datos editables
        existing.setDvCliente(dv);
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

    public ClienteResponseDTO toResponseDTO(Cliente entity, DireccionDTO direccionDTO) {
        String sNombre = (entity.getSnombreCliente() != null && !entity.getSnombreCliente().trim().isEmpty())
                ? entity.getSnombreCliente() + " " : "";
        String nombreCompleto = entity.getPnombreCliente() + " " + sNombre
                + entity.getAppaternoCliente() + " " + entity.getApmaternoCliente();

        // Protección ante caída del ms_direcciones
        String dirCompleta = "Dirección no disponible";
        if (direccionDTO != null) {
            dirCompleta = direccionDTO.getCalle() + " " + direccionDTO.getNumero();
            if (direccionDTO.getDepartamento() != null && !direccionDTO.getDepartamento().isBlank()) {
                dirCompleta += " Depto. " + direccionDTO.getDepartamento();
            }
        }

        return ClienteResponseDTO.builder()
                .runClienteCompleto(RutUtil.formatear(entity.getRunCliente(), entity.getDvCliente()))
                .nombreClienteCompleto(nombreCompleto.trim())
                .emailCliente(entity.getEmailCliente())
                .telefonoCliente(entity.getTelefonoCliente())
                .direccionCliente(dirCompleta)
                .build();
    }
}