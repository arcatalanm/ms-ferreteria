package ferrefix.ms_usuarios.controller;

import java.net.URI;
import java.util.List;

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
// Inyeccion de dependencias con Lombok para el service del Cliente
@RequiredArgsConstructor
@RequestMapping("/api/usuarios/clientes")
public class ClienteController {

    // Inyeccion del service del Cliente
    private final ClienteService clienteService;

    @PostMapping
    // Ocupamos Valid para ejercer las validaciones del DTO de ClienteRequestDTO
    public ResponseEntity<Cliente> registrarCliente(@Valid @RequestBody ClienteRequestDTO clienteRequestDTO) {
        Cliente clienteCreado = clienteService.crearCliente(clienteRequestDTO);
        return ResponseEntity.created(URI.create("/api/usuarios/clientes/" + clienteCreado.getRunCliente()))
                .body(clienteCreado);
    }

    @GetMapping
    public ResponseEntity<List<ClienteResponseDTO>> listarClientes() {
        return ResponseEntity.ok(clienteService.buscarTodosClientes());
    }

    @PutMapping("/run/{runCliente}")
    public ResponseEntity<Cliente> actualizarCliente(@PathVariable Integer runCliente,
            @Valid @RequestBody ClienteRequestDTO clienteRequestDTO) {
        Cliente clienteActualizado = clienteService.actualizarCliente(runCliente, clienteRequestDTO);
        return ResponseEntity.ok(clienteActualizado);
    }

    @GetMapping("/run/{runCliente}")
    public ResponseEntity<ClienteResponseDTO> obtenerClientePorRun (@PathVariable Integer runCliente) {
        ClienteResponseDTO clienteDTO = clienteService.buscarClientePorRun(runCliente);
        return ResponseEntity.ok(clienteDTO);
    }

    @GetMapping("/email/{emailCliente:.+}")
    public ResponseEntity<ClienteResponseDTO> obtenerClientePorEmail(@PathVariable String emailCliente) {
        ClienteResponseDTO clienteDTO = clienteService.buscarClienteEmail(emailCliente);
        return ResponseEntity.ok(clienteDTO);
    }

    @DeleteMapping("/run/{runCliente}")
    public ResponseEntity<Void> eliminarClientePorRun(@PathVariable Integer runCliente) {
        clienteService.eliminarClientePorRun(runCliente);
        return ResponseEntity.noContent().build();
    }
}
