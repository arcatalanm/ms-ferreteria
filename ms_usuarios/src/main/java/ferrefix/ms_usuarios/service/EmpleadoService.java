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

    public Empleado crearEmpleado(EmpleadoRequestDTO dto) {
        logger.info("Iniciando creación de empleado RUN: {}", dto.getRunEmpleado());

        if (empleadoRepository.existsByRunEmpleado(dto.getRunEmpleado())) {
            logger.warn("Conflicto al crear: El empleado RUN {} ya existe", dto.getRunEmpleado());
            throw new BadRequestException("El empleado con run " + dto.getRunEmpleado() + " ya existe");
        }

        Character dvTransformado = dto.getDvEmpleado().toUpperCase().charAt(0);
        if (!RutUtil.isRutValido(dto.getRunEmpleado(), dvTransformado)) {
            logger.warn("Validación fallida: El RUN {}-{} del empleado es inválido", dto.getRunEmpleado(), dvTransformado);
            throw new BadRequestException("El run del empleado no es válido");
        }

        if (empleadoRepository.existsByEmailEmpleado(dto.getEmailEmpleado())) {
            logger.warn("Conflicto al crear: El email {} ya está registrado", dto.getEmailEmpleado());
            throw new BadRequestException("El email del empleado ya está registrado: " + dto.getEmailEmpleado());
        }

        Cargo cargo = cargoRepository.findById(dto.getIdCargo())
                .orElseThrow(() -> {
                    logger.warn("Fallo al crear empleado: No existe el cargo ID {}", dto.getIdCargo());
                    return new ResourceNotFoundException("No se encontró el cargo con id " + dto.getIdCargo());
                });

        // DELEGACIÓN: El Mapper arma el empleado pasándole el cargo ya validado
        Empleado empleado = empleadoMapper.toEntity(dto, dvTransformado, cargo);

        Empleado empleadoGuardado = empleadoRepository.save(empleado);
        logger.info("Empleado RUN {} creado exitosamente", empleadoGuardado.getRunEmpleado());
        return empleadoGuardado;
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

    public Empleado actualizarEmpleado(Integer runEmpleado, EmpleadoRequestDTO dto) {
        logger.info("Iniciando actualización de empleado RUN: {}", runEmpleado);

        Empleado empleadoExistente = empleadoRepository.findById(runEmpleado)
                .orElseThrow(() -> {
                    logger.warn("Fallo al actualizar: Empleado RUN {} no encontrado", runEmpleado);
                    return new ResourceNotFoundException("No se encontró el empleado con run " + runEmpleado);
                });
        
        if (!runEmpleado.equals(dto.getRunEmpleado())) {
            logger.warn("Conflicto de integridad: RUN URL ({}) no coincide con Body ({})", runEmpleado, dto.getRunEmpleado());
            throw new BadRequestException("El run de la ruta debe coincidir con el run del cuerpo de la solicitud");
        }

        Character dvTransformado = dto.getDvEmpleado().toUpperCase().charAt(0);
        if (!RutUtil.isRutValido(dto.getRunEmpleado(), dvTransformado)) {
            logger.warn("Validación fallida al actualizar: El RUN {}-{} es inválido", dto.getRunEmpleado(), dvTransformado);
            throw new BadRequestException("El run del empleado no es válido");
        }

        Empleado empleadoPorEmail = empleadoRepository.findByEmailEmpleado(dto.getEmailEmpleado());
        if (empleadoPorEmail != null && !empleadoPorEmail.getRunEmpleado().equals(runEmpleado)) {
            logger.warn("Conflicto al actualizar: Email {} ya ocupado", dto.getEmailEmpleado());
            throw new BadRequestException("El email ya está registrado por otro empleado");
        }

        Cargo cargo = cargoRepository.findById(dto.getIdCargo())
                .orElseThrow(() -> {
                    logger.warn("Fallo al actualizar empleado: Cargo ID {} no encontrado", dto.getIdCargo());
                    return new ResourceNotFoundException("No se encontró el cargo con id " + dto.getIdCargo());
                });

        // DELEGACIÓN: El Mapper actualiza la entidad
        empleadoMapper.updateEntity(empleadoExistente, dto, dvTransformado, cargo);

        Empleado empleadoActualizado = empleadoRepository.save(empleadoExistente);
        logger.info("Empleado RUN {} actualizado exitosamente", runEmpleado);
        return empleadoActualizado;
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
}