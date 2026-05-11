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

import ferrefix.ms_usuarios.dto.ClienteRequestDTO;
import ferrefix.ms_usuarios.dto.ClienteResponseDTO;
import ferrefix.ms_usuarios.model.Cliente;
import ferrefix.ms_usuarios.service.ClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/usuarios/clientes")
public class ClienteController {

    private static final Logger logger = LoggerFactory.getLogger(ClienteController.class);
    private final ClienteService clienteService;

    @PostMapping
    public ResponseEntity<Cliente> registrarCliente(@Valid @RequestBody ClienteRequestDTO clienteRequestDTO) {
        logger.info("POST /api/usuarios/clientes - Solicitud para registrar cliente RUN: {}", clienteRequestDTO.getRunCliente());
        Cliente clienteCreado = clienteService.crearCliente(clienteRequestDTO);
        logger.info("POST /api/usuarios/clientes - Cliente registrado exitosamente. Respondiendo 201 CREATED");
        return ResponseEntity.created(URI.create("/api/usuarios/clientes/" + clienteCreado.getRunCliente()))
                .body(clienteCreado);
    }

    @GetMapping
    public ResponseEntity<List<ClienteResponseDTO>> listarClientes() {
        logger.info("GET /api/usuarios/clientes - Solicitud para listar todos los clientes");
        List<ClienteResponseDTO> clientes = clienteService.buscarTodosClientes();
        logger.info("GET /api/usuarios/clientes - Listado enviado con éxito. Respondiendo 200 OK");
        return ResponseEntity.ok(clientes);
    }

    @PutMapping("/run/{runCliente}")
    public ResponseEntity<Cliente> actualizarCliente(@PathVariable Integer runCliente,
            @Valid @RequestBody ClienteRequestDTO clienteRequestDTO) {
        logger.info("PUT /api/usuarios/clientes/run/{} - Solicitud para actualizar cliente", runCliente);
        Cliente clienteActualizado = clienteService.actualizarCliente(runCliente, clienteRequestDTO);
        logger.info("PUT /api/usuarios/clientes/run/{} - Cliente actualizado. Respondiendo 200 OK", runCliente);
        return ResponseEntity.ok(clienteActualizado);
    }

    @GetMapping("/run/{runCliente}")
    public ResponseEntity<ClienteResponseDTO> obtenerClientePorRun (@PathVariable Integer runCliente) {
        logger.info("GET /api/usuarios/clientes/run/{} - Solicitud para obtener cliente", runCliente);
        ClienteResponseDTO clienteDTO = clienteService.buscarClientePorRun(runCliente);
        logger.info("GET /api/usuarios/clientes/run/{} - Cliente obtenido. Respondiendo 200 OK", runCliente);
        return ResponseEntity.ok(clienteDTO);
    }

    @DeleteMapping("/run/{runCliente}")
    public ResponseEntity<Void> eliminarClientePorRun(@PathVariable Integer runCliente) {
        logger.info("DELETE /api/usuarios/clientes/run/{} - Solicitud para eliminar cliente", runCliente);
        clienteService.eliminarClientePorRun(runCliente);
        logger.info("DELETE /api/usuarios/clientes/run/{} - Cliente eliminado. Respondiendo 204 NO CONTENT", runCliente);
        return ResponseEntity.noContent().build();
    }
}