package ferrefix.ms_sugerencia.service;

import ferrefix.ms_sugerencia.dto.SugerenciaRequestDTO;
import ferrefix.ms_sugerencia.dto.SugerenciaResponseDTO;
import ferrefix.ms_sugerencia.exception.ResourceNotFoundException;
import ferrefix.ms_sugerencia.mapper.SugerenciaMapper;
import ferrefix.ms_sugerencia.model.Sugerencia;
import ferrefix.ms_sugerencia.repository.SugerenciaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SugerenciaService {

    private final SugerenciaRepository sugerenciaRepository;
    private final SugerenciaMapper sugerenciaMapper;

    @Transactional(readOnly = true)
    public List<SugerenciaResponseDTO> listarTodas() {
        log.info("Listando todas las sugerencias por orden cronológico");
        return sugerenciaRepository.findAll(Sort.by(Sort.Direction.DESC, "fechaIngreso"))
                .stream()
                .map(sugerenciaMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public SugerenciaResponseDTO crear(SugerenciaRequestDTO dto) {
        log.info("Ingresando nueva sugerencia/reclamo");
        Sugerencia sugerencia = sugerenciaMapper.toEntity(dto);
        Sugerencia guardada = sugerenciaRepository.save(sugerencia);
        log.info("Sugerencia guardada con éxito. ID: {}", guardada.getIdSugerencia());
        return sugerenciaMapper.toResponseDTO(guardada);
    }

    @Transactional
    public void eliminar(Long id) {
        if (!sugerenciaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Sugerencia no encontrada con ID: " + id);
        }
        sugerenciaRepository.deleteById(id);
        log.info("Sugerencia ID {} eliminada", id);
    }
}
