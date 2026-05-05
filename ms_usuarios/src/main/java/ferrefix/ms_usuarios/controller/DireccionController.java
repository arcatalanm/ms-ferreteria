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

import ferrefix.ms_usuarios.dto.DireccionRequestDTO;
import ferrefix.ms_usuarios.dto.DireccionResponseDTO;
import ferrefix.ms_usuarios.model.Direccion;
import ferrefix.ms_usuarios.service.DireccionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/usuarios/direcciones")
@RequiredArgsConstructor
public class DireccionController {
    private final DireccionService direccionService;

    @PostMapping
    public ResponseEntity<Direccion> crearDireccion(@Valid @RequestBody DireccionRequestDTO dto) {
        Direccion direccionCreada = direccionService.crearDireccion(dto);
        return ResponseEntity.created(URI.create("/api/usuarios/direcciones/" + direccionCreada.getIdDireccion()))
                .body(direccionCreada);
    }

    @GetMapping
    public ResponseEntity<List<DireccionResponseDTO>> listarDirecciones() {
        return ResponseEntity.ok(direccionService.buscarTodasDirecciones());
    }

    @GetMapping("/{idDireccion}")
    public ResponseEntity<DireccionResponseDTO> obtenerDireccionPorId(@PathVariable Long idDireccion) {
        DireccionResponseDTO dto = direccionService.buscarDireccionPorId(idDireccion);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/cliente/{runCliente}")
    public ResponseEntity<List<DireccionResponseDTO>> obtenerDireccionesPorCliente(@PathVariable Integer runCliente) {
        return ResponseEntity.ok(direccionService.buscarDireccionesPorCliente(runCliente));
    }

    @PutMapping("/{idDireccion}")
    public ResponseEntity<Direccion> actualizarDireccion(@PathVariable Long idDireccion,
            @Valid @RequestBody DireccionRequestDTO dto) {
        return ResponseEntity.ok(direccionService.actualizarDireccion(idDireccion, dto));
    }

    @DeleteMapping("/{idDireccion}")
    public ResponseEntity<Void> eliminarDireccion(@PathVariable Long idDireccion) {
        direccionService.eliminarDireccion(idDireccion);
        return ResponseEntity.noContent().build();
    }
}
