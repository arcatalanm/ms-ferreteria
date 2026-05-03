package ferrefix.ms_inventorario.service;

import java.util.List;

import org.springframework.stereotype.Service;

import ferrefix.ms_inventorario.dto.UnidadMedidaRequestDTO;
import ferrefix.ms_inventorario.dto.UnidadMedidaResponseDTO;
import ferrefix.ms_inventorario.model.UnidadMedida;
import ferrefix.ms_inventorario.repository.UnidadMedidaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class UnidadMedidaService {
    // Inyectamos el repositorio para poder acceder a la base de datos
    private final UnidadMedidaRepository unidadMedidaRepository;

    public UnidadMedida crearUnidadMedida (UnidadMedidaRequestDTO unidadMedidaRequestDTO) {
        // Verificamos si ya existe una unidad de medida con el mismo nombre
        if (unidadMedidaRepository.existsByNombreUnidadMedida(unidadMedidaRequestDTO.getNombreUnidadMedida())) {
            throw new IllegalArgumentException("La unidad de medida con nombre " + unidadMedidaRequestDTO.getNombreUnidadMedida() + " ya existe");
        }

        // Mappeamos el DTO a la entidad
        UnidadMedida unidad = UnidadMedida.builder()
                .nombreUnidadMedida(unidadMedidaRequestDTO.getNombreUnidadMedida())
                .build();
        // Guardamos la entidad en la base de datos
        return unidadMedidaRepository.save(unidad);
    }

    public List<UnidadMedidaResponseDTO> buscarTodasUnidadesMedida() {
        return unidadMedidaRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public UnidadMedidaResponseDTO buscarUnidadMedidaPorId(Integer id) {
        UnidadMedida unidad = unidadMedidaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró la unidad de medida con id: " + id));
        return toResponse(unidad);
    }

    public UnidadMedida actualizarUnidadMedida(Integer id, UnidadMedidaRequestDTO unidadMedidaRequestDTO) {
        UnidadMedida unidadExistente = unidadMedidaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró la unidad de medida con id: " + id));

        if (unidadMedidaRepository.existsByNombreUnidadMedida(unidadMedidaRequestDTO.getNombreUnidadMedida())
                && !unidadExistente.getNombreUnidadMedida().equalsIgnoreCase(unidadMedidaRequestDTO.getNombreUnidadMedida())) {
            throw new IllegalArgumentException("La unidad de medida con nombre " + unidadMedidaRequestDTO.getNombreUnidadMedida() + " ya existe");
        }

        unidadExistente.setNombreUnidadMedida(unidadMedidaRequestDTO.getNombreUnidadMedida());
        return unidadMedidaRepository.save(unidadExistente);
    }

    public void eliminarUnidadMedida(Integer id) {
        if (!unidadMedidaRepository.existsById(id)) {
            throw new IllegalArgumentException("No se encontró la unidad de medida con id: " + id);
        }
        unidadMedidaRepository.deleteById(id);
    }

    private UnidadMedidaResponseDTO toResponse(UnidadMedida unidad) {
        return UnidadMedidaResponseDTO.builder()
                .idUnidadMedida(unidad.getIdUnidadMedida())
                .nombreUnidadMedida(unidad.getNombreUnidadMedida())
                .build();
    }
}
