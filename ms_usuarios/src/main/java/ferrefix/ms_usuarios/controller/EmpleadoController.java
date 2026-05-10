package ferrefix.ms_usuarios.controller;

import java.net.URI;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ferrefix.ms_usuarios.dto.EmpleadoRequestDTO;
import ferrefix.ms_usuarios.dto.EmpleadoResponseDTO;
import ferrefix.ms_usuarios.model.Empleado;
import ferrefix.ms_usuarios.service.EmpleadoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/usuarios/empleados")
@RequiredArgsConstructor
public class EmpleadoController {

    private static final Logger logger = LoggerFactory.getLogger(EmpleadoController.class);
    private final EmpleadoService empleadoService;

    @PostMapping
    public ResponseEntity<Empleado> crearEmpleado(@Valid @RequestBody EmpleadoRequestDTO dto) {
        logger.info("POST /api/usuarios/empleados - Solicitud para registrar empleado RUN: {}", dto.getRunEmpleado());
        Empleado empleadoCreado = empleadoService.crearEmpleado(dto);
        logger.info("POST /api/usuarios/empleados - Empleado registrado exitosamente. Respondiendo 201 CREATED");
        return ResponseEntity.created(URI.create("/api/usuarios/empleados/run/" + empleadoCreado.getRunEmpleado()))
                .body(empleadoCreado);
    }

    @GetMapping
    public ResponseEntity<List<EmpleadoResponseDTO>> listarEmpleados() {
        logger.info("GET /api/usuarios/empleados - Solicitud para listar todos los empleados");
        List<EmpleadoResponseDTO> empleados = empleadoService.buscarTodosEmpleados();
        logger.info("GET /api/usuarios/empleados - Listado enviado con éxito. Respondiendo 200 OK");
        return ResponseEntity.ok(empleados);
    }

    @GetMapping("/run/{runEmpleado}")
    public ResponseEntity<EmpleadoResponseDTO> obtenerEmpleadoPorRun(@PathVariable Integer runEmpleado) {
        logger.info("GET /api/usuarios/empleados/run/{} - Solicitud para obtener empleado", runEmpleado);
        EmpleadoResponseDTO dto = empleadoService.buscarEmpleadoPorRun(runEmpleado);
        logger.info("GET /api/usuarios/empleados/run/{} - Empleado obtenido. Respondiendo 200 OK", runEmpleado);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/run/{runEmpleado}")
    public ResponseEntity<Empleado> actualizarEmpleado(@PathVariable Integer runEmpleado,
            @Valid @RequestBody EmpleadoRequestDTO dto) {
        logger.info("PUT /api/usuarios/empleados/run/{} - Solicitud para actualizar empleado", runEmpleado);
        Empleado empleadoActualizado = empleadoService.actualizarEmpleado(runEmpleado, dto);
        logger.info("PUT /api/usuarios/empleados/run/{} - Empleado actualizado. Respondiendo 200 OK", runEmpleado);
        return ResponseEntity.ok(empleadoActualizado);
    }

    @DeleteMapping("/run/{runEmpleado}")
    public ResponseEntity<Void> eliminarEmpleado(@PathVariable Integer runEmpleado) {
        logger.info("DELETE /api/usuarios/empleados/run/{} - Solicitud para dar de baja empleado", runEmpleado);
        empleadoService.eliminarEmpleado(runEmpleado);
        logger.info("DELETE /api/usuarios/empleados/run/{} - Empleado desactivado. Respondiendo 204 NO CONTENT", runEmpleado);
        return ResponseEntity.noContent().build();
    }
}