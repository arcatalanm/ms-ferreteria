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
    private final EmpleadoService empleadoService;

    @PostMapping
    public ResponseEntity<Empleado> crearEmpleado(@Valid @RequestBody EmpleadoRequestDTO dto) {
        Empleado empleadoCreado = empleadoService.crearEmpleado(dto);
        return ResponseEntity.created(URI.create("/api/usuarios/empleados/run/" + empleadoCreado.getRunEmpleado()))
                .body(empleadoCreado);
    }

    @GetMapping
    public ResponseEntity<List<EmpleadoResponseDTO>> listarEmpleados() {
        return ResponseEntity.ok(empleadoService.buscarTodosEmpleados());
    }

    @GetMapping("/run/{runEmpleado}")
    public ResponseEntity<EmpleadoResponseDTO> obtenerEmpleadoPorRun(@PathVariable Integer runEmpleado) {
        EmpleadoResponseDTO dto = empleadoService.buscarEmpleadoPorRun(runEmpleado);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/run/{runEmpleado}")
    public ResponseEntity<Empleado> actualizarEmpleado(@PathVariable Integer runEmpleado,
            @Valid @RequestBody EmpleadoRequestDTO dto) {
        return ResponseEntity.ok(empleadoService.actualizarEmpleado(runEmpleado, dto));
    }

    @DeleteMapping("/run/{runEmpleado}")
    public ResponseEntity<Void> eliminarEmpleado(@PathVariable Integer runEmpleado) {
        empleadoService.eliminarEmpleado(runEmpleado);
        return ResponseEntity.noContent().build();
    }
}
