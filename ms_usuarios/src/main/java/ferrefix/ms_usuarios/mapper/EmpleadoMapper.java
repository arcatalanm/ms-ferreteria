package ferrefix.ms_usuarios.mapper;

import org.springframework.stereotype.Component;
import ferrefix.ms_usuarios.dto.EmpleadoRequestDTO;
import ferrefix.ms_usuarios.dto.EmpleadoResponseDTO;
import ferrefix.ms_usuarios.model.Cargo;
import ferrefix.ms_usuarios.model.Empleado;

@Component
public class EmpleadoMapper {

    public Empleado toEntity(EmpleadoRequestDTO dto, Character dvChar, Cargo cargo) {
        return Empleado.builder()
                .runEmpleado(dto.getRunEmpleado())
                .dvEmpleado(dvChar) // Usamos el validado
                .pnombreEmpleado(dto.getPnombreEmpleado())
                .snombreEmpleado(dto.getSnombreEmpleado())
                .appaternoEmpleado(dto.getAppaternoEmpleado())
                .apmaternoEmpleado(dto.getApmaternoEmpleado())
                .emailEmpleado(dto.getEmailEmpleado())
                .contrasenaEmpleado(dto.getContrasenaEmpleado())
                .sueldoBaseEmpleado(dto.getSueldoBaseEmpleado())
                .fechaContratacionEmpleado(dto.getFechaContratacionEmpleado())
                .telefonoEmpleado(dto.getTelefonoEmpleado())
                .cargo(cargo) // Asignamos el cargo real
                .build();
    }

    public void updateEntity(Empleado existing, EmpleadoRequestDTO dto, Character dvChar, Cargo cargo) {
        existing.setDvEmpleado(dvChar);
        existing.setPnombreEmpleado(dto.getPnombreEmpleado());
        existing.setSnombreEmpleado(dto.getSnombreEmpleado());
        existing.setAppaternoEmpleado(dto.getAppaternoEmpleado());
        existing.setApmaternoEmpleado(dto.getApmaternoEmpleado());
        existing.setEmailEmpleado(dto.getEmailEmpleado());
        existing.setContrasenaEmpleado(dto.getContrasenaEmpleado());
        existing.setSueldoBaseEmpleado(dto.getSueldoBaseEmpleado());
        existing.setFechaContratacionEmpleado(dto.getFechaContratacionEmpleado());
        existing.setTelefonoEmpleado(dto.getTelefonoEmpleado());
        existing.setCargo(cargo);
    }

    public EmpleadoResponseDTO toResponseDTO(Empleado empleado) {
        String runCompleto = empleado.getRunEmpleado() + "-" + empleado.getDvEmpleado();
        String nombreCompleto = empleado.getPnombreEmpleado() + " "
                + (empleado.getSnombreEmpleado() != null ? empleado.getSnombreEmpleado() + " " : "")
                + empleado.getAppaternoEmpleado() + " " + empleado.getApmaternoEmpleado();

        return EmpleadoResponseDTO.builder()
                .runEmpleadoCompleto(runCompleto)
                .nombreEmpleadoCompleto(nombreCompleto.trim())
                .emailEmpleado(empleado.getEmailEmpleado())
                .telefonoEmpleado(empleado.getTelefonoEmpleado())                
                .nombreCargo(empleado.getCargo().getNombreCargo())
                .fechaContratacionEmpleado(empleado.getFechaContratacionEmpleado())
                .build();
    }
}