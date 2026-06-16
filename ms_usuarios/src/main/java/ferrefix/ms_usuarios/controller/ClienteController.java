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

import ferrefix.ms_usuarios.dto.ClienteRequestDTO;
import ferrefix.ms_usuarios.dto.ClienteResponseDTO;
import ferrefix.ms_usuarios.exception.BadRequestException;
import ferrefix.ms_usuarios.service.ClienteService;
import ferrefix.ms_usuarios.util.RutUtil;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/usuarios/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private static final Logger logger = LoggerFactory.getLogger(ClienteController.class);
    private final ClienteService clienteService;

    @PostMapping
    public ResponseEntity<EntityModel<ClienteResponseDTO>> registrarCliente(
            @Valid @RequestBody ClienteRequestDTO dto, HttpServletRequest request) {

        logger.info("POST /api/usuarios/clientes - RUT: {}", dto.getRunCliente());
        ClienteResponseDTO creado = clienteService.crearCliente(dto);
        
        EntityModel<ClienteResponseDTO> model = EntityModel.of(creado,
                linkTo(methodOn(ClienteController.class).obtenerPorRun(creado.getRunClienteCompleto(), null)).withSelfRel(),
                linkTo(methodOn(ClienteController.class).listarClientes(null)).withRel("clientes")
        );

        logger.info("POST /api/usuarios/clientes - Cliente creado. Respondiendo 201 CREATED");
        return ResponseEntity.status(HttpStatus.CREATED).body(model);
    }

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<ClienteResponseDTO>>> listarClientes(HttpServletRequest request) {
        logger.info("GET /api/usuarios/clientes - Listando todos los clientes");
        List<ClienteResponseDTO> lista = clienteService.buscarTodosClientes();
        
        List<EntityModel<ClienteResponseDTO>> models = lista.stream()
                .map(c -> EntityModel.of(c,
                        linkTo(methodOn(ClienteController.class).obtenerPorRun(c.getRunClienteCompleto(), null)).withSelfRel(),
                        linkTo(methodOn(ClienteController.class).listarClientes(null)).withRel("clientes")
                ))
                .toList();

        CollectionModel<EntityModel<ClienteResponseDTO>> collection = CollectionModel.of(
                models,
                linkTo(methodOn(ClienteController.class).listarClientes(null)).withSelfRel()
        );

        logger.info("GET /api/usuarios/clientes - {} registros. Respondiendo 200 OK", lista.size());
        return ResponseEntity.ok(collection);
    }

    @GetMapping("/run/{runCliente}")
    public ResponseEntity<EntityModel<ClienteResponseDTO>> obtenerPorRun(@PathVariable String runCliente, HttpServletRequest request) {
        if (!RutUtil.esValido(runCliente)) {
            logger.warn("RUN inválido en ruta cliente: {}", runCliente);
            throw new BadRequestException("El RUN de la ruta debe incluir DV y ser válido.");
        }
        Integer run = RutUtil.extraerRun(runCliente);
        logger.info("GET /api/usuarios/clientes/run/{} - Buscando cliente", runCliente);
        ClienteResponseDTO dto = clienteService.buscarClientePorRun(run);
        
        EntityModel<ClienteResponseDTO> model = EntityModel.of(dto,
                linkTo(methodOn(ClienteController.class).obtenerPorRun(runCliente, null)).withSelfRel(),
                linkTo(methodOn(ClienteController.class).listarClientes(null)).withRel("clientes"),
                linkTo(methodOn(ClienteController.class).actualizarCliente(runCliente, null, null)).withRel("actualizar"),
                linkTo(methodOn(ClienteController.class).eliminarCliente(runCliente, null)).withRel("eliminar")
        );

        logger.info("GET /api/usuarios/clientes/run/{} - Encontrado. Respondiendo 200 OK", runCliente);
        return ResponseEntity.ok(model);
    }

    @PutMapping("/run/{runCliente}")
    public ResponseEntity<EntityModel<ClienteResponseDTO>> actualizarCliente(
            @PathVariable String runCliente,
            @Valid @RequestBody ClienteRequestDTO dto, HttpServletRequest request) {
        if (!RutUtil.esValido(runCliente)) {
            logger.warn("RUN inválido en ruta cliente: {}", runCliente);
            throw new BadRequestException("El RUN de la ruta debe incluir DV y ser válido.");
        }
        Integer run = RutUtil.extraerRun(runCliente);

        logger.info("PUT /api/usuarios/clientes/run/{} - Actualizando cliente", runCliente);
        ClienteResponseDTO actualizado = clienteService.actualizarCliente(run, dto);
        
        EntityModel<ClienteResponseDTO> model = EntityModel.of(actualizado,
                linkTo(methodOn(ClienteController.class).obtenerPorRun(actualizado.getRunClienteCompleto(), null)).withSelfRel(),
                linkTo(methodOn(ClienteController.class).listarClientes(null)).withRel("clientes")
        );

        logger.info("PUT /api/usuarios/clientes/run/{} - Actualizado. Respondiendo 200 OK", runCliente);
        return ResponseEntity.ok(model);
    }

    @DeleteMapping("/run/{runCliente}")
    public ResponseEntity<Void> eliminarCliente(
            @PathVariable String runCliente,
            HttpServletRequest request) {

        if (!RutUtil.esValido(runCliente)) {
            logger.warn("RUN inválido en ruta cliente: {}", runCliente);
            throw new BadRequestException("El RUN de la ruta debe incluir DV y ser válido.");
        }
        Integer run = RutUtil.extraerRun(runCliente);

        logger.info("DELETE /api/usuarios/clientes/run/{} - Solicitud de eliminación", runCliente);
        clienteService.eliminarClientePorRun(run);

        logger.info("DELETE /api/usuarios/clientes/run/{} - Eliminado. Respondiendo 204 NO CONTENT", runCliente);
        return ResponseEntity.noContent().build();
    }
}
