package ferrefix.ms_usuarios.controller;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ferrefix.ms_usuarios.dto.EmpleadoRequestDTO;
import ferrefix.ms_usuarios.dto.EmpleadoResponseDTO;
import ferrefix.ms_usuarios.exception.BadRequestException;
import ferrefix.ms_usuarios.service.EmpleadoService;
import ferrefix.ms_usuarios.util.RutUtil;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/usuarios/empleados")
@RequiredArgsConstructor
public class EmpleadoController {

    private static final Logger logger = LoggerFactory.getLogger(EmpleadoController.class);
    private final EmpleadoService empleadoService;

    @PostMapping
    public ResponseEntity<EntityModel<EmpleadoResponseDTO>> crearEmpleado(
            @Valid @RequestBody EmpleadoRequestDTO dto, HttpServletRequest request) {

        logger.info("POST /api/usuarios/empleados - RUT: {}", dto.getRutEmpleado());
        EmpleadoResponseDTO creado = empleadoService.crearEmpleado(dto);
        
        EntityModel<EmpleadoResponseDTO> model = EntityModel.of(creado,
                linkTo(methodOn(EmpleadoController.class).obtenerEmpleadoPorRun(creado.getRunEmpleadoCompleto(), null)).withSelfRel(),
                linkTo(methodOn(EmpleadoController.class).listarEmpleados(null)).withRel("empleados")
        );

        logger.info("POST /api/usuarios/empleados - Empleado registrado. Respondiendo 201 CREATED");
        return ResponseEntity.status(HttpStatus.CREATED).body(model);
    }

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<EmpleadoResponseDTO>>> listarEmpleados(HttpServletRequest request) {
        logger.info("GET /api/usuarios/empleados - Listando todos los empleados");
        List<EmpleadoResponseDTO> empleados = empleadoService.buscarTodosEmpleados();
        
        List<EntityModel<EmpleadoResponseDTO>> models = empleados.stream()
                .map(e -> EntityModel.of(e,
                        linkTo(methodOn(EmpleadoController.class).obtenerEmpleadoPorRun(e.getRunEmpleadoCompleto(), null)).withSelfRel(),
                        linkTo(methodOn(EmpleadoController.class).listarEmpleados(null)).withRel("empleados")
                ))
                .toList();

        CollectionModel<EntityModel<EmpleadoResponseDTO>> collection = CollectionModel.of(
                models,
                linkTo(methodOn(EmpleadoController.class).listarEmpleados(null)).withSelfRel()
        );

        logger.info("GET /api/usuarios/empleados - {} registros. Respondiendo 200 OK", empleados.size());
        return ResponseEntity.ok(collection);
    }

    @GetMapping("/run/{runEmpleado}")
    public ResponseEntity<EntityModel<EmpleadoResponseDTO>> obtenerEmpleadoPorRun(
            @PathVariable String runEmpleado, HttpServletRequest request) {
        if (!RutUtil.esValido(runEmpleado)) {
            logger.warn("RUN inválido en ruta empleado: {}", runEmpleado);
            throw new BadRequestException("El RUN de la ruta debe incluir DV y ser válido.");
        }
        Integer run = RutUtil.extraerRun(runEmpleado);

        logger.info("GET /api/usuarios/empleados/run/{} - Buscando empleado", runEmpleado);
        EmpleadoResponseDTO response = empleadoService.buscarEmpleadoPorRun(run);
        
        EntityModel<EmpleadoResponseDTO> model = EntityModel.of(response,
                linkTo(methodOn(EmpleadoController.class).obtenerEmpleadoPorRun(runEmpleado, null)).withSelfRel(),
                linkTo(methodOn(EmpleadoController.class).listarEmpleados(null)).withRel("empleados"),
                linkTo(methodOn(EmpleadoController.class).actualizarEmpleado(runEmpleado, null, null)).withRel("actualizar"),
                linkTo(methodOn(EmpleadoController.class).eliminarEmpleado(runEmpleado, null)).withRel("eliminar")
        );

        logger.info("GET /api/usuarios/empleados/run/{} - Encontrado. Respondiendo 200 OK", runEmpleado);
        return ResponseEntity.ok(model);
    }

    @PutMapping("/run/{runEmpleado}")
    public ResponseEntity<EntityModel<EmpleadoResponseDTO>> actualizarEmpleado(
            @PathVariable String runEmpleado,
            @Valid @RequestBody EmpleadoRequestDTO dto, HttpServletRequest request) {
        if (!RutUtil.esValido(runEmpleado)) {
            logger.warn("RUN inválido en ruta empleado: {}", runEmpleado);
            throw new BadRequestException("El RUN de la ruta debe incluir DV y ser válido.");
        }
        Integer run = RutUtil.extraerRun(runEmpleado);

        logger.info("PUT /api/usuarios/empleados/run/{} - Actualizando empleado", runEmpleado);
        EmpleadoResponseDTO actualizado = empleadoService.actualizarEmpleado(run, dto);
        
        EntityModel<EmpleadoResponseDTO> model = EntityModel.of(actualizado,
                linkTo(methodOn(EmpleadoController.class).obtenerEmpleadoPorRun(actualizado.getRunEmpleadoCompleto(), null)).withSelfRel(),
                linkTo(methodOn(EmpleadoController.class).listarEmpleados(null)).withRel("empleados")
        );

        logger.info("PUT /api/usuarios/empleados/run/{} - Actualizado. Respondiendo 200 OK", runEmpleado);
        return ResponseEntity.ok(model);
    }

    @DeleteMapping("/run/{runEmpleado}")
    public ResponseEntity<Void> eliminarEmpleado(
            @PathVariable String runEmpleado,
            HttpServletRequest request) {
        if (!RutUtil.esValido(runEmpleado)) {
            logger.warn("RUN inválido en ruta empleado: {}", runEmpleado);
            throw new BadRequestException("El RUN de la ruta debe incluir DV y ser válido.");
        }
        Integer run = RutUtil.extraerRun(runEmpleado);

        logger.info("DELETE /api/usuarios/empleados/run/{} - Solicitud de baja lógica", runEmpleado);
        empleadoService.eliminarEmpleado(run);

        logger.info("DELETE /api/usuarios/empleados/run/{} - Baja completada. Respondiendo 204 NO CONTENT", runEmpleado);
        return ResponseEntity.noContent().build();
    }
}
