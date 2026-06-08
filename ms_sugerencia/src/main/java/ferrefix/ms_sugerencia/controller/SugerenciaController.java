package ferrefix.ms_sugerencia.controller;

import ferrefix.ms_sugerencia.dto.SugerenciaRequestDTO;
import ferrefix.ms_sugerencia.dto.SugerenciaResponseDTO;
import ferrefix.ms_sugerencia.exception.ApiSuccessResponse;
import ferrefix.ms_sugerencia.service.SugerenciaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/sugerencias")
@RequiredArgsConstructor
public class SugerenciaController {

    private final SugerenciaService sugerenciaService;

    @GetMapping
    public ResponseEntity<ApiSuccessResponse<List<SugerenciaResponseDTO>>> listarTodas() {
        log.info("Petición recibida: GET /api/sugerencias");
        List<SugerenciaResponseDTO> sugerencias = sugerenciaService.listarTodas();
        return ResponseEntity.ok(ApiSuccessResponse.<List<SugerenciaResponseDTO>>builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message("Listado de sugerencias obtenido con éxito")
                .data(sugerencias)
                .build());
    }

    @PostMapping
    public ResponseEntity<ApiSuccessResponse<SugerenciaResponseDTO>> crear(@Valid @RequestBody SugerenciaRequestDTO dto) {
        log.info("Petición recibida: POST /api/sugerencias");
        SugerenciaResponseDTO response = sugerenciaService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiSuccessResponse.<SugerenciaResponseDTO>builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.CREATED.value())
                        .message("Sugerencia/Reclamo ingresado con éxito")
                        .data(response)
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiSuccessResponse<Void>> eliminar(@PathVariable Long id) {
        log.info("Petición recibida: DELETE /api/sugerencias/{}", id);
        sugerenciaService.eliminar(id);
        return ResponseEntity.ok(ApiSuccessResponse.<Void>builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message("Sugerencia eliminada con éxito")
                .build());
    }
}
