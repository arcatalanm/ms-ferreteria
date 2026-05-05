package ferrefix.ms_usuarios.service;

import java.util.List;

import org.springframework.stereotype.Service;

import ferrefix.ms_usuarios.dto.EmpleadoRequestDTO;
import ferrefix.ms_usuarios.dto.EmpleadoResponseDTO;
import ferrefix.ms_usuarios.model.Cargo;
import ferrefix.ms_usuarios.model.Empleado;
import ferrefix.ms_usuarios.repository.CargoRepository;
import ferrefix.ms_usuarios.repository.EmpleadoRepository;
import ferrefix.ms_usuarios.util.RutUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class EmpleadoService {
    private final EmpleadoRepository empleadoRepository;
    private final CargoRepository cargoRepository;
    public RutUtil rutUtil;

    public Empleado crearEmpleado(EmpleadoRequestDTO dto) {
        if (empleadoRepository.existsByRunEmpleado(dto.getRunEmpleado())) {
            throw new IllegalArgumentException("El empleado con run " + dto.getRunEmpleado() + " ya existe");
        }

        // Validacion del run del empleado utilizando el RutUtil para asegurar que el run es válido antes de crear el empleado
        // VALIDACION MATEMATICA
        if (!rutUtil.isRutValido(dto.getRunEmpleado(), dto.getDvEmpleado())) {
            throw new IllegalArgumentException("El run del empleado no es válido");
        }

        if (empleadoRepository.existsByEmailEmpleado(dto.getEmailEmpleado())) {
            throw new IllegalArgumentException("El email del empleado ya está registrado: " + dto.getEmailEmpleado());
        }

        Cargo cargo = cargoRepository.findById(dto.getIdCargo())
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el cargo con id " + dto.getIdCargo()));

        Empleado empleado = Empleado.builder()
                .runEmpleado(dto.getRunEmpleado())
                .dvEmpleado(dto.getDvEmpleado())
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

        return empleadoRepository.save(empleado);
    }

    public List<EmpleadoResponseDTO> buscarTodosEmpleados() {
        return empleadoRepository.findAll().stream()
                .map(this::mapToDTO)
                .toList();
    }

    public EmpleadoResponseDTO buscarEmpleadoPorRun(Integer runEmpleado) {
        Empleado empleado = empleadoRepository.findByRunEmpleado(runEmpleado);
        if (empleado == null) {
            throw new IllegalArgumentException("No se encontró el empleado con run " + runEmpleado);
        }
        return mapToDTO(empleado);
    }

    public Empleado actualizarEmpleado(Integer runEmpleado, EmpleadoRequestDTO dto) {
        Empleado empleadoExistente = empleadoRepository.findByRunEmpleado(runEmpleado);
        if (empleadoExistente == null) {
            throw new IllegalArgumentException("No se encontró el empleado con run " + runEmpleado);
        }
        if (!runEmpleado.equals(dto.getRunEmpleado())) {
            throw new IllegalArgumentException("El run de la ruta debe coincidir con el run del cuerpo de la solicitud");
        }

        Empleado empleadoPorEmail = empleadoRepository.findByEmailEmpleado(dto.getEmailEmpleado());
        if (empleadoPorEmail != null && !empleadoPorEmail.getRunEmpleado().equals(runEmpleado)) {
            throw new IllegalArgumentException("El email ya está registrado por otro empleado");
        }

        Cargo cargo = cargoRepository.findById(dto.getIdCargo())
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el cargo con id " + dto.getIdCargo()));

        empleadoExistente.setDvEmpleado(dto.getDvEmpleado());
        empleadoExistente.setPnombreEmpleado(dto.getPnombreEmpleado());
        empleadoExistente.setSnombreEmpleado(dto.getSnombreEmpleado());
        empleadoExistente.setAppaternoEmpleado(dto.getAppaternoEmpleado());
        empleadoExistente.setApmaternoEmpleado(dto.getApmaternoEmpleado());
        empleadoExistente.setEmailEmpleado(dto.getEmailEmpleado());
        empleadoExistente.setContrasenaEmpleado(dto.getContrasenaEmpleado());
        empleadoExistente.setSueldoBaseEmpleado(dto.getSueldoBaseEmpleado());
        empleadoExistente.setFechaContratacionEmpleado(dto.getFechaContratacionEmpleado());
        empleadoExistente.setTelefonoEmpleado(dto.getTelefonoEmpleado());
        empleadoExistente.setCargo(cargo);

        return empleadoRepository.save(empleadoExistente);
    }

    public void eliminarEmpleado(Integer runEmpleado) {
        Empleado empleadoExistente = empleadoRepository.findByRunEmpleado(runEmpleado);
        if (empleadoExistente == null) {
            throw new IllegalArgumentException("No se encontró el empleado con run " + runEmpleado);
        }
        empleadoExistente.setActivoEmpleado(false);
        empleadoRepository.save(empleadoExistente);
    }

    private EmpleadoResponseDTO mapToDTO(Empleado empleado) {
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
