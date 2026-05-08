package ferrefix.ms_proveedores.service;

import ferrefix.ms_proveedores.dto.ProveedorRequestDTO;
import ferrefix.ms_proveedores.dto.ProveedorResponseDTO;
import ferrefix.ms_proveedores.exception.ProveedorNotFoundException;
import ferrefix.ms_proveedores.mapper.ProveedorMapper;
import ferrefix.ms_proveedores.model.Proveedor;
import ferrefix.ms_proveedores.repository.ProveedorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
// Inyeccion del Args Necesarios
@RequiredArgsConstructor
public class ProveedorService {

    // Inyección del Repository
    private final ProveedorRepository proveedorRepository;
    private final ProveedorMapper proveedorMapper;

    @Transactional
    public ProveedorResponseDTO guardar(ProveedorRequestDTO requestDTO) {
        Proveedor proveedor = proveedorMapper.toEntity(requestDTO);
        Proveedor savedProveedor = proveedorRepository.save(proveedor);
        return proveedorMapper.toResponseDTO(savedProveedor);
    }

    public List<ProveedorResponseDTO> listarTodos() {
        List<Proveedor> proveedores = proveedorRepository.findAll();
        return proveedores.stream()
                .map(proveedorMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public ProveedorResponseDTO buscarPorId(Integer id) {
        Optional<Proveedor> proveedorOpt = proveedorRepository.findById(id);
        Proveedor proveedor = proveedorOpt.orElseThrow(() ->
                new ProveedorNotFoundException("Proveedor con ID " + id + " no encontrado"));
        return proveedorMapper.toResponseDTO(proveedor);
    }

    @Transactional
    public ProveedorResponseDTO actualizar(Integer id, ProveedorRequestDTO requestDTO) {
        Optional<Proveedor> proveedorOpt = proveedorRepository.findById(id);
        Proveedor proveedor = proveedorOpt.orElseThrow(() ->
                new ProveedorNotFoundException("Proveedor con ID " + id + " no encontrado"));
        Proveedor updatedProveedor = proveedorMapper.toEntity(requestDTO, id);
        Proveedor savedProveedor = proveedorRepository.save(updatedProveedor);
        return proveedorMapper.toResponseDTO(savedProveedor);
    }

    @Transactional
    public void eliminar(Integer id) {
        if (!proveedorRepository.existsById(id)) {
            throw new ProveedorNotFoundException("Proveedor con ID " + id + " no encontrado");
        }
        proveedorRepository.deleteById(id);
    }
}
