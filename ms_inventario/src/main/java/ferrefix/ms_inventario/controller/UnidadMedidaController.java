package ferrefix.ms_inventario.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ferrefix.ms_inventario.dto.UnidadMedidaRequestDTO;
import ferrefix.ms_inventario.dto.UnidadMedidaResponseDTO;
import ferrefix.ms_inventario.model.UnidadMedida;
import ferrefix.ms_inventario.service.UnidadMedidaService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/unidades_medida")
@RequiredArgsConstructor
public class UnidadMedidaController {

    private final UnidadMedidaService unidadMedidaService;
    
    private static final Logger logger = LoggerFactory.getLogger(UnidadMedidaController.class);

    @PostMapping
    public ResponseEntity<UnidadMedidaResponseDTO> crearUnidadMedida(@RequestBody UnidadMedidaRequestDTO dto) {
        logger.info("POST /api/unidades_medida - Solicitud para crear unidad de medida: '{}'", 
                    dto.getNombreUnidadMedida());
        
        UnidadMedida unidadCreada = unidadMedidaService.crearUnidadMedida(dto);
        UnidadMedidaResponseDTO response = UnidadMedidaResponseDTO.builder()
                .idUnidadMedida(unidadCreada.getIdUnidadMedida())
                .nombreUnidadMedida(unidadCreada.getNombreUnidadMedida())
                .build();
        
        logger.info("Unidad de medida creada exitosamente. ID: {}, Nombre: '{}'", 
                    unidadCreada.getIdUnidadMedida(), unidadCreada.getNombreUnidadMedida());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<UnidadMedidaResponseDTO>> buscarTodasUnidadesMedida() {
        logger.info("GET /api/unidades_medida - Solicitud para obtener listado completo de unidades de medida");
        
        List<UnidadMedidaResponseDTO> unidades = unidadMedidaService.buscarTodasUnidadesMedida();
        
        logger.info("Listado de unidades de medida obtenido exitosamente. Total de registros: {}", unidades.size());
        
        return ResponseEntity.ok(unidades);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UnidadMedidaResponseDTO> buscarUnidadMedidaPorId(@PathVariable Integer id) {
        logger.info("GET /api/unidades_medida/{} - Solicitud para obtener unidad de medida por ID", id);
        
        UnidadMedidaResponseDTO unidad = unidadMedidaService.buscarUnidadMedidaPorId(id);
        
        logger.info("Unidad de medida obtenida exitosamente. ID: {}, Nombre: '{}'", 
                    unidad.getIdUnidadMedida(), unidad.getNombreUnidadMedida());
        
        return ResponseEntity.ok(unidad);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UnidadMedidaResponseDTO> actualizarUnidadMedida(@PathVariable Integer id, @RequestBody UnidadMedidaRequestDTO dto) {
        logger.info("PUT /api/unidades_medida/{} - Solicitud para actualizar unidad de medida. Nuevo nombre: '{}'", 
                    id, dto.getNombreUnidadMedida());
        
        UnidadMedida unidadActualizada = unidadMedidaService.actualizarUnidadMedida(id, dto);
        UnidadMedidaResponseDTO response = UnidadMedidaResponseDTO.builder()
                .idUnidadMedida(unidadActualizada.getIdUnidadMedida())
                .nombreUnidadMedida(unidadActualizada.getNombreUnidadMedida())
                .build();
        
        logger.info("Unidad de medida actualizada exitosamente. ID: {}, Nuevo nombre: '{}'", 
                    unidadActualizada.getIdUnidadMedida(), unidadActualizada.getNombreUnidadMedida());
        
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUnidadMedida(@PathVariable Integer id) {
        logger.info("DELETE /api/unidades_medida/{} - Solicitud para eliminar unidad de medida", id);
        
        unidadMedidaService.eliminarUnidadMedida(id);
        
        logger.info("Unidad de medida eliminada exitosamente. ID: {}", id);
        
        return ResponseEntity.noContent().build();
    }
}
