package ferrefix.ms_usuarios.service;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import ferrefix.ms_usuarios.dto.EmpleadoRequestDTO;
import ferrefix.ms_usuarios.dto.EmpleadoResponseDTO;
import ferrefix.ms_usuarios.exception.BadRequestException;
import ferrefix.ms_usuarios.exception.ResourceNotFoundException;
import ferrefix.ms_usuarios.mapper.EmpleadoMapper;
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

    private static final Logger logger = LoggerFactory.getLogger(EmpleadoService.class);

    private final EmpleadoRepository empleadoRepository;
    private final CargoRepository cargoRepository;
    private final EmpleadoMapper empleadoMapper; // Inyectamos el Mapper

    public EmpleadoResponseDTO crearEmpleado(EmpleadoRequestDTO dto) {
        logger.info("Iniciando creación de empleado RUT: {}", dto.getRutEmpleado());

        if (!RutUtil.esValido(dto.getRutEmpleado())) {
            logger.warn("RUT inválido recibido: {}", dto.getRutEmpleado());
            throw new BadRequestException("El RUT ingresado no es válido: " + dto.getRutEmpleado());
        }

        Integer run = RutUtil.extraerRun(dto.getRutEmpleado());
        Character dv  = RutUtil.extraerDv(dto.getRutEmpleado());

        if (empleadoRepository.existsById(run)) {
            logger.warn("Conflicto: el empleado RUN {} ya existe", run);
            throw new BadRequestException("El empleado con RUN " + run + " ya existe.");
        }

        validarEmailUnico(dto.getEmailEmpleado(), null);

        Cargo cargo = cargoRepository.findById(dto.getIdCargo())
                .orElseThrow(() -> {
                    logger.warn("Fallo al crear empleado: Cargo ID {} no encontrado", dto.getIdCargo());
                    return new ResourceNotFoundException("No se encontró el cargo con id " + dto.getIdCargo());
                });

        Empleado empleado = empleadoMapper.toEntity(dto, run, dv, cargo); // ← cargo incluido
        Empleado guardado = empleadoRepository.save(empleado);

        logger.info("Empleado RUN {} creado exitosamente", guardado.getRunEmpleado());
        return empleadoMapper.toResponseDTO(guardado);
    }

    public List<EmpleadoResponseDTO> buscarTodosEmpleados() {
        logger.info("Consultando la lista de todos los empleados");
        List<EmpleadoResponseDTO> empleados = empleadoRepository.findAll().stream()
                .map(empleadoMapper::toResponseDTO) // Delegación
                .toList();
        logger.info("Consulta exitosa. Total empleados: {}", empleados.size());
        return empleados;
    }

    public EmpleadoResponseDTO buscarEmpleadoPorRun(Integer runEmpleado) {
        logger.info("Buscando empleado por RUN: {}", runEmpleado);
        return empleadoRepository.findById(runEmpleado) // findById porque RUN es la PK
                .map(empleadoMapper::toResponseDTO)
                .orElseThrow(() -> {
                    logger.warn("Búsqueda fallida: Empleado RUN {} no encontrado", runEmpleado);
                    return new ResourceNotFoundException("No se encontró el empleado con run " + runEmpleado);
                });
    }

    public EmpleadoResponseDTO actualizarEmpleado(Integer runEmpleado, EmpleadoRequestDTO dto) {
        logger.info("Iniciando actualización de empleado RUN: {}", runEmpleado);

        Empleado empleadoExistente = empleadoRepository.findById(runEmpleado)
                .orElseThrow(() -> {
                    logger.warn("Fallo al actualizar: Empleado RUN {} no encontrado", runEmpleado);
                    return new ResourceNotFoundException("No se encontró el empleado con run " + runEmpleado);
                });

        if (!RutUtil.esValido(dto.getRutEmpleado())) {
            logger.warn("RUT inválido al actualizar: {}", dto.getRutEmpleado());
            throw new BadRequestException("El RUT ingresado no es válido: " + dto.getRutEmpleado());
        }

        Integer run = RutUtil.extraerRun(dto.getRutEmpleado());
        Character dv  = RutUtil.extraerDv(dto.getRutEmpleado());

        if (!runEmpleado.equals(run)) {
            logger.warn("Conflicto de integridad: RUN URL ({}) no coincide con body ({})", runEmpleado, run);
            throw new BadRequestException("El RUT del cuerpo debe corresponder al mismo empleado de la URL.");
        }

        validarEmailUnico(dto.getEmailEmpleado(), runEmpleado);

        Cargo cargo = cargoRepository.findById(dto.getIdCargo())
                .orElseThrow(() -> {
                    logger.warn("Fallo al actualizar empleado: Cargo ID {} no encontrado", dto.getIdCargo());
                    return new ResourceNotFoundException("No se encontró el cargo con id " + dto.getIdCargo());
                });

        empleadoMapper.updateEntity(empleadoExistente, dto, run, dv, cargo); // ← firma nueva
        Empleado actualizado = empleadoRepository.save(empleadoExistente);

        logger.info("Empleado RUN {} actualizado exitosamente", runEmpleado);
        return empleadoMapper.toResponseDTO(actualizado);
    }

    public void eliminarEmpleado(Integer runEmpleado) {
        logger.info("Iniciando baja lógica de empleado RUN: {}", runEmpleado);
        Empleado empleadoExistente = empleadoRepository.findById(runEmpleado)
                .orElseThrow(() -> {
                    logger.warn("Fallo al desactivar: Empleado RUN {} no encontrado", runEmpleado);
                    return new ResourceNotFoundException("No se encontró el empleado con run " + runEmpleado);
                });

        empleadoExistente.setActivoEmpleado(false);
        empleadoRepository.save(empleadoExistente);
        logger.info("Empleado RUN {} desactivado exitosamente", runEmpleado);
    }

    private void validarEmailUnico(String email, Integer runActual) {
    Empleado empleadoPorEmail = empleadoRepository.findByEmailEmpleado(email);
    if (empleadoPorEmail != null && !empleadoPorEmail.getRunEmpleado().equals(runActual)) {
        logger.warn("Conflicto: El email {} ya pertenece a otro empleado", email);
        throw new BadRequestException("El email ya está registrado por otro empleado.");
    }
}
}