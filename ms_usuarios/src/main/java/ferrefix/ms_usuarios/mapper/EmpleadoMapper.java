package ferrefix.ms_usuarios.mapper;

import org.springframework.stereotype.Component;

import ferrefix.ms_usuarios.dto.EmpleadoRequestDTO;
import ferrefix.ms_usuarios.dto.EmpleadoResponseDTO;
import ferrefix.ms_usuarios.model.Cargo;
import ferrefix.ms_usuarios.model.Empleado;
import ferrefix.ms_usuarios.util.RutUtil;

@Component
public class EmpleadoMapper {

    public Empleado toEntity(EmpleadoRequestDTO dto, Integer run, Character dv, Cargo cargo) {
        return Empleado.builder()
                .runEmpleado(run)
                .dvEmpleado(dv)
                .pnombreEmpleado(dto.getPnombreEmpleado())
                .snombreEmpleado(dto.getSnombreEmpleado())
                .appaternoEmpleado(dto.getAppaternoEmpleado())
                .apmaternoEmpleado(dto.getApmaternoEmpleado())
                .emailEmpleado(dto.getEmailEmpleado())
                .contrasenaEmpleado(dto.getContrasenaEmpleado())
                .sueldoBaseEmpleado(dto.getSueldoBaseEmpleado())
                .fechaContratacionEmpleado(dto.getFechaContratacionEmpleado())
                .telefonoEmpleado(dto.getTelefonoEmpleado())
                .cargo(cargo)
                .build();
    }

    public void updateEntity(Empleado existing, EmpleadoRequestDTO dto, Integer run, Character dv, Cargo cargo) {
        // El run (PK) no se modifica asi que solo actualizamos los datos editables
        existing.setDvEmpleado(dv);
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
        String nombreCompleto = empleado.getPnombreEmpleado() + " "
                + (empleado.getSnombreEmpleado() != null ? empleado.getSnombreEmpleado() + " " : "")
                + empleado.getAppaternoEmpleado() + " " + empleado.getApmaternoEmpleado();

        return EmpleadoResponseDTO.builder()
                .runEmpleadoCompleto(RutUtil.formatear(empleado.getRunEmpleado(), empleado.getDvEmpleado()))
                .nombreEmpleadoCompleto(nombreCompleto.trim())
                .emailEmpleado(empleado.getEmailEmpleado())
                .telefonoEmpleado(empleado.getTelefonoEmpleado())
                .nombreCargo(empleado.getCargo().getNombreCargo())
                .fechaContratacionEmpleado(empleado.getFechaContratacionEmpleado())
                .build();
    }
}