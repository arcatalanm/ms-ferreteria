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

import ferrefix.ms_usuarios.dto.ClienteRequestDTO;
import ferrefix.ms_usuarios.dto.ClienteResponseDTO;
import ferrefix.ms_usuarios.exception.ApiSuccessResponse;
import ferrefix.ms_usuarios.exception.BadRequestException;
import ferrefix.ms_usuarios.service.ClienteService;
import ferrefix.ms_usuarios.util.RutUtil;

@RestController
@RequestMapping("/api/usuarios/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private static final Logger logger = LoggerFactory.getLogger(ClienteController.class);
    private final ClienteService clienteService;

    @PostMapping
    public ResponseEntity<ClienteResponseDTO> registrarCliente(
            @Valid @RequestBody ClienteRequestDTO dto) {

        logger.info("POST /api/usuarios/clientes - RUT: {}", dto.getRunCliente());
        ClienteResponseDTO creado = clienteService.crearCliente(dto);
        logger.info("POST /api/usuarios/clientes - Cliente creado. Respondiendo 201 CREATED");
        return ResponseEntity
                .created(URI.create("/api/usuarios/clientes/run/" + creado.getRunClienteCompleto()))
                .body(creado);
    }

    @GetMapping
    public ResponseEntity<List<ClienteResponseDTO>> listarClientes() {
        logger.info("GET /api/usuarios/clientes - Listando todos los clientes");
        List<ClienteResponseDTO> lista = clienteService.buscarTodosClientes();
        logger.info("GET /api/usuarios/clientes - {} registros. Respondiendo 200 OK", lista.size());
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/run/{runCliente}")
    public ResponseEntity<ClienteResponseDTO> obtenerPorRun(@PathVariable String runCliente) {
        if (!RutUtil.esValido(runCliente)) {
            logger.warn("RUN inválido en ruta cliente: {}", runCliente);
            throw new BadRequestException("El RUN de la ruta debe incluir DV y ser válido.");
        }
        Integer run = RutUtil.extraerRun(runCliente);
        logger.info("GET /api/usuarios/clientes/run/{} - Buscando cliente", runCliente);
        ClienteResponseDTO dto = clienteService.buscarClientePorRun(run);
        logger.info("GET /api/usuarios/clientes/run/{} - Encontrado. Respondiendo 200 OK", runCliente);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/run/{runCliente}")
    public ResponseEntity<ClienteResponseDTO> actualizarCliente(
            @PathVariable String runCliente,
            @Valid @RequestBody ClienteRequestDTO dto) {
        if (!RutUtil.esValido(runCliente)) {
            logger.warn("RUN inválido en ruta cliente: {}", runCliente);
            throw new BadRequestException("El RUN de la ruta debe incluir DV y ser válido.");
        }
        Integer run = RutUtil.extraerRun(runCliente);

        logger.info("PUT /api/usuarios/clientes/run/{} - Actualizando cliente", runCliente);
        ClienteResponseDTO actualizado = clienteService.actualizarCliente(run, dto);
        logger.info("PUT /api/usuarios/clientes/run/{} - Actualizado. Respondiendo 200 OK", runCliente);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/run/{runCliente}")
    public ResponseEntity<ApiSuccessResponse> eliminarCliente(
            @PathVariable String runCliente,
            HttpServletRequest request) {

        if (!RutUtil.esValido(runCliente)) {
            logger.warn("RUN inválido en ruta cliente: {}", runCliente);
            throw new BadRequestException("El RUN de la ruta debe incluir DV y ser válido.");
        }
        Integer run = RutUtil.extraerRun(runCliente);

        logger.info("DELETE /api/usuarios/clientes/run/{} - Solicitud de eliminación", runCliente);
        clienteService.eliminarClientePorRun(run);

        ApiSuccessResponse respuesta = ApiSuccessResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message("El cliente con RUN " + runCliente + " fue eliminado correctamente.")
                .path(request.getRequestURI())
                .build();

        logger.info("DELETE /api/usuarios/clientes/run/{} - Eliminado. Respondiendo 200 OK", runCliente);
        return ResponseEntity.ok(respuesta);
    }
}