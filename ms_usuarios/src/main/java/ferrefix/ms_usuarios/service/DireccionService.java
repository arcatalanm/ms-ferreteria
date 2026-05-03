package ferrefix.ms_usuarios.service;

import java.util.List;

import org.springframework.stereotype.Service;

import ferrefix.ms_usuarios.dto.DireccionRequestDTO;
import ferrefix.ms_usuarios.dto.DireccionResponseDTO;
import ferrefix.ms_usuarios.model.Cliente;
import ferrefix.ms_usuarios.model.Direccion;
import ferrefix.ms_usuarios.repository.ClienteRepository;
import ferrefix.ms_usuarios.repository.DireccionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class DireccionService {
    private final DireccionRepository direccionRepository;
    private final ClienteRepository clienteRepository;

    public Direccion crearDireccion(DireccionRequestDTO dto) {
        Cliente cliente = clienteRepository.findByRunCliente(dto.getRunCliente());
        if (cliente == null) {
            throw new IllegalArgumentException("No se encontró un cliente con run " + dto.getRunCliente());
        }

        Direccion direccion = Direccion.builder()
                .calle(dto.getCalle())
                .numero(dto.getNumero())
                .departamento(dto.getDepartamento())
                .comuna(dto.getComuna())
                .ciudad(dto.getCiudad())
                .cliente(cliente)
                .build();

        return direccionRepository.save(direccion);
    }

    // Metodo privado para mapear una entidad Direccion a un DTO de respuesta, con formateo de la dirección completa
    private DireccionResponseDTO mapToDTO(Direccion direccion) {
        // Manejo del departamento para que no se muestre si es nulo o vacío, y formateo de la dirección completa
        String depto = (direccion.getDepartamento() != null && !direccion.getDepartamento().trim().isEmpty()) 
        ? ", Depto " + direccion.getDepartamento() : "";
        // Formateo de la dirección completa para mostrarla en el DTO de respuesta
        String direccionFormateada = direccion.getCalle() + " " + direccion.getNumero() + " " + depto + ", " + direccion.getComuna() + ", " + direccion.getCiudad();
        return DireccionResponseDTO.builder()
                .idDireccion(direccion.getIdDireccion())
                .calle(direccion.getCalle())
                .numero(direccion.getNumero())
                .departamento(direccion.getDepartamento())
                .comuna(direccion.getComuna())
                .ciudad(direccion.getCiudad())
                .direccionCompleta(direccionFormateada)
                .build();
    }

    public List<DireccionResponseDTO> buscarTodasDirecciones() {
        return direccionRepository.findAll().stream()
                .map(this::mapToDTO)
                .toList();
    }

    public List<DireccionResponseDTO> buscarDireccionesPorCliente(Integer runCliente) {
        return direccionRepository.findByCliente_RunCliente(runCliente).stream()
                .map(this::mapToDTO)
                .toList();
    }

    public Direccion actualizarDireccion(Long idDireccion, DireccionRequestDTO dto) {
        Direccion direccionExistente = direccionRepository.findByIdDireccion(idDireccion)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró la dirección con id " + idDireccion));

        Cliente cliente = clienteRepository.findByRunCliente(dto.getRunCliente());
        if (cliente == null) {
            throw new IllegalArgumentException("No se encontró un cliente con run " + dto.getRunCliente());
        }

        direccionExistente.setCalle(dto.getCalle());
        direccionExistente.setNumero(dto.getNumero());
        direccionExistente.setDepartamento(dto.getDepartamento());
        direccionExistente.setComuna(dto.getComuna());
        direccionExistente.setCiudad(dto.getCiudad());
        direccionExistente.setCliente(cliente);

        return direccionRepository.save(direccionExistente);
    }

    public void eliminarDireccion(Long idDireccion) {
        if (!direccionRepository.existsById(idDireccion)) {
            throw new IllegalArgumentException("No se encontró la dirección con id " + idDireccion);
        }
        direccionRepository.deleteById(idDireccion);
    }

    public DireccionResponseDTO buscarDireccionPorId(Long idDireccion) {
        Direccion direccion = direccionRepository.findByIdDireccion(idDireccion)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró la dirección con id " + idDireccion));
        return mapToDTO(direccion);
    }

}
