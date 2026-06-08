package ferrefix.ms_marcas.controller;

import ferrefix.ms_marcas.dto.MarcaRequestDTO;
import ferrefix.ms_marcas.dto.MarcaResponseDTO;
import ferrefix.ms_marcas.exception.ApiSuccessResponse;
import ferrefix.ms_marcas.service.MarcaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/marcas")
@RequiredArgsConstructor
public class MarcaController {

    private final MarcaService marcaService;

    @GetMapping
    public ResponseEntity<ApiSuccessResponse<List<MarcaResponseDTO>>> listarTodas() {
        List<MarcaResponseDTO> marcas = marcaService.listarTodas();
        return ResponseEntity.ok(ApiSuccessResponse.<List<MarcaResponseDTO>>builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message("Listado de marcas obtenido con éxito")
                .data(marcas)
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiSuccessResponse<MarcaResponseDTO>> obtenerPorId(@PathVariable Integer id) {
        MarcaResponseDTO marca = marcaService.obtenerPorId(id);
        return ResponseEntity.ok(ApiSuccessResponse.<MarcaResponseDTO>builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message("Marca encontrada")
                .data(marca)
                .build());
    }

    @PostMapping
    public ResponseEntity<ApiSuccessResponse<MarcaResponseDTO>> crear(@Valid @RequestBody MarcaRequestDTO dto) {
        MarcaResponseDTO creada = marcaService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiSuccessResponse.<MarcaResponseDTO>builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CREATED.value())
                .message("Marca creada con éxito")
                .data(creada)
                .build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiSuccessResponse<MarcaResponseDTO>> actualizar(
            @PathVariable Integer id, @Valid @RequestBody MarcaRequestDTO dto) {
        MarcaResponseDTO actualizada = marcaService.actualizar(id, dto);
        return ResponseEntity.ok(ApiSuccessResponse.<MarcaResponseDTO>builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message("Marca actualizada con éxito")
                .data(actualizada)
                .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiSuccessResponse<Void>> eliminar(@PathVariable Integer id) {
        marcaService.eliminar(id);
        return ResponseEntity.ok(ApiSuccessResponse.<Void>builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message("Marca eliminada con éxito")
                .build());
    }
}
