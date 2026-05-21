package ferrefix.ms_usuarios.controller;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ferrefix.ms_usuarios.dto.EmpleadoRequestDTO;
import ferrefix.ms_usuarios.dto.EmpleadoResponseDTO;
import ferrefix.ms_usuarios.exception.ApiSuccessResponse;
import ferrefix.ms_usuarios.exception.BadRequestException;
import ferrefix.ms_usuarios.service.EmpleadoService;
import ferrefix.ms_usuarios.util.RutUtil;

@RestController
@RequestMapping("/api/usuarios/empleados")
@RequiredArgsConstructor
public class EmpleadoController {

    private static final Logger logger = LoggerFactory.getLogger(EmpleadoController.class);
    private final EmpleadoService empleadoService;

    @PostMapping
    public ResponseEntity<EmpleadoResponseDTO> crearEmpleado(
            @Valid @RequestBody EmpleadoRequestDTO dto) {

        logger.info("POST /api/usuarios/empleados - RUT: {}", dto.getRutEmpleado());
        EmpleadoResponseDTO creado = empleadoService.crearEmpleado(dto);
        logger.info("POST /api/usuarios/empleados - Empleado registrado. Respondiendo 201 CREATED");
        return ResponseEntity
                .created(URI.create("/api/usuarios/empleados/run/" + creado.getRunEmpleadoCompleto()))
                .body(creado);
    }

    @GetMapping
    public ResponseEntity<List<EmpleadoResponseDTO>> listarEmpleados() {
        logger.info("GET /api/usuarios/empleados - Listando todos los empleados");
        List<EmpleadoResponseDTO> empleados = empleadoService.buscarTodosEmpleados();
        logger.info("GET /api/usuarios/empleados - {} registros. Respondiendo 200 OK", empleados.size());
        return ResponseEntity.ok(empleados);
    }

    @GetMapping("/run/{runEmpleado}")
    public ResponseEntity<EmpleadoResponseDTO> obtenerEmpleadoPorRun(
            @PathVariable String runEmpleado) {
        if (!RutUtil.esValido(runEmpleado)) {
            logger.warn("RUN inválido en ruta empleado: {}", runEmpleado);
            throw new BadRequestException("El RUN de la ruta debe incluir DV y ser válido.");
        }
        Integer run = RutUtil.extraerRun(runEmpleado);

        logger.info("GET /api/usuarios/empleados/run/{} - Buscando empleado", runEmpleado);
        EmpleadoResponseDTO response = empleadoService.buscarEmpleadoPorRun(run);
        logger.info("GET /api/usuarios/empleados/run/{} - Encontrado. Respondiendo 200 OK", runEmpleado);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/run/{runEmpleado}")
    public ResponseEntity<EmpleadoResponseDTO> actualizarEmpleado(
            @PathVariable String runEmpleado,
            @Valid @RequestBody EmpleadoRequestDTO dto) {
        if (!RutUtil.esValido(runEmpleado)) {
            logger.warn("RUN inválido en ruta empleado: {}", runEmpleado);
            throw new BadRequestException("El RUN de la ruta debe incluir DV y ser válido.");
        }
        Integer run = RutUtil.extraerRun(runEmpleado);

        logger.info("PUT /api/usuarios/empleados/run/{} - Actualizando empleado", runEmpleado);
        EmpleadoResponseDTO actualizado = empleadoService.actualizarEmpleado(run, dto);
        logger.info("PUT /api/usuarios/empleados/run/{} - Actualizado. Respondiendo 200 OK", runEmpleado);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/run/{runEmpleado}")
    public ResponseEntity<ApiSuccessResponse> eliminarEmpleado(
            @PathVariable String runEmpleado,
            HttpServletRequest request) {
        if (!RutUtil.esValido(runEmpleado)) {
            logger.warn("RUN inválido en ruta empleado: {}", runEmpleado);
            throw new BadRequestException("El RUN de la ruta debe incluir DV y ser válido.");
        }
        Integer run = RutUtil.extraerRun(runEmpleado);

        logger.info("DELETE /api/usuarios/empleados/run/{} - Solicitud de baja lógica", runEmpleado);
        empleadoService.eliminarEmpleado(run);

        ApiSuccessResponse respuesta = ApiSuccessResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message("El empleado con RUN " + runEmpleado + " fue dado de baja correctamente.")
                .path(request.getRequestURI())
                .build();

        logger.info("DELETE /api/usuarios/empleados/run/{} - Baja completada. Respondiendo 200 OK", runEmpleado);
        return ResponseEntity.ok(respuesta);
    }
}