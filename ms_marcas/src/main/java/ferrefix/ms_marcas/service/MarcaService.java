package ferrefix.ms_marcas.service;

import ferrefix.ms_marcas.dto.MarcaRequestDTO;
import ferrefix.ms_marcas.dto.MarcaResponseDTO;
import ferrefix.ms_marcas.exception.ResourceNotFoundException;
import ferrefix.ms_marcas.mapper.MarcaMapper;
import ferrefix.ms_marcas.model.Marca;
import ferrefix.ms_marcas.repository.MarcaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MarcaService {

    private final MarcaRepository marcaRepository;
    private final MarcaMapper marcaMapper;

    @Transactional(readOnly = true)
    public List<MarcaResponseDTO> listarTodas() {
        return marcaRepository.findAll().stream()
                .map(marcaMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MarcaResponseDTO obtenerPorId(Integer id) {
        Marca marca = marcaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Marca no encontrada con ID: " + id));
        return marcaMapper.toResponseDTO(marca);
    }

    @Transactional
    public MarcaResponseDTO crear(MarcaRequestDTO dto) {
        Marca marca = marcaMapper.toEntity(dto);
        Marca guardada = marcaRepository.save(marca);
        return marcaMapper.toResponseDTO(guardada);
    }

    @Transactional
    public MarcaResponseDTO actualizar(Integer id, MarcaRequestDTO dto) {
        Marca marca = marcaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Marca no encontrada con ID: " + id));
        
        marcaMapper.updateEntity(marca, dto);
        Marca actualizada = marcaRepository.save(marca);
        return marcaMapper.toResponseDTO(actualizada);
    }

    @Transactional
    public void eliminar(Integer id) {
        if (!marcaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Marca no encontrada con ID: " + id);
        }
        marcaRepository.deleteById(id);
    }
}
