package ferrefix.ms_arriendo.controller;

import ferrefix.ms_arriendo.dto.MaquinaRequestDTO;
import ferrefix.ms_arriendo.dto.MaquinaResponseDTO;
import ferrefix.ms_arriendo.dto.ProcesarArriendoRequestDTO;
import ferrefix.ms_arriendo.exception.ApiSuccessResponse;
import ferrefix.ms_arriendo.service.MaquinaArriendoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/arriendos/maquinas")
@RequiredArgsConstructor
public class MaquinaArriendoController {

    private final MaquinaArriendoService maquinaArriendoService;

    @GetMapping
    public ResponseEntity<ApiSuccessResponse<List<MaquinaResponseDTO>>> listarTodas() {
        List<MaquinaResponseDTO> maquinas = maquinaArriendoService.listarTodas();
        return ResponseEntity.ok(ApiSuccessResponse.<List<MaquinaResponseDTO>>builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message("Listado de máquinas obtenido con éxito")
                .data(maquinas)
                .build());
    }

    @PostMapping
    public ResponseEntity<ApiSuccessResponse<MaquinaResponseDTO>> registrar(@Valid @RequestBody MaquinaRequestDTO dto) {
        MaquinaResponseDTO registrada = maquinaArriendoService.registrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiSuccessResponse.<MaquinaResponseDTO>builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CREATED.value())
                .message("Máquina registrada con éxito")
                .data(registrada)
                .build());
    }

    @PutMapping("/{idEquipo}/arrendar")
    public ResponseEntity<ApiSuccessResponse<MaquinaResponseDTO>> arrendar(
            @PathVariable Integer idEquipo,
            @Valid @RequestBody ProcesarArriendoRequestDTO dto) {
        
        MaquinaResponseDTO arrendada = maquinaArriendoService.procesarArriendo(idEquipo, dto);
        return ResponseEntity.ok(ApiSuccessResponse.<MaquinaResponseDTO>builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message("Arriendo procesado exitosamente")
                .data(arrendada)
                .build());
    }

    @PutMapping("/{idEquipo}/devolver")
    public ResponseEntity<ApiSuccessResponse<MaquinaResponseDTO>> devolver(@PathVariable Integer idEquipo) {
        MaquinaResponseDTO devuelta = maquinaArriendoService.procesarDevolucion(idEquipo);
        return ResponseEntity.ok(ApiSuccessResponse.<MaquinaResponseDTO>builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message("Devolución procesada exitosamente")
                .data(devuelta)
                .build());
    }
}
