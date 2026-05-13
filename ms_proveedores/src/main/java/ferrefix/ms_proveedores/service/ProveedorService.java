package ferrefix.ms_proveedores.service;

import ferrefix.ms_proveedores.client.DireccionClient;
import ferrefix.ms_proveedores.dto.DireccionDTO;
import ferrefix.ms_proveedores.dto.ProveedorRequestDTO;
import ferrefix.ms_proveedores.dto.ProveedorResponseDTO;
import ferrefix.ms_proveedores.exception.BadRequestException;
import ferrefix.ms_proveedores.exception.ResourceNotFoundException;
import ferrefix.ms_proveedores.mapper.ProveedorMapper;
import ferrefix.ms_proveedores.model.Proveedor;
import ferrefix.ms_proveedores.repository.ProveedorRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import feign.FeignException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ProveedorService {

    private final ProveedorRepository proveedorRepository;
    private final ProveedorMapper proveedorMapper;
    private final DireccionClient direccionClient; // Cliente Feign
    private static final Logger logger = LoggerFactory.getLogger(ProveedorService.class);

    public ProveedorResponseDTO guardar(ProveedorRequestDTO requestDTO) {
        logger.info("Inicio de operación: crear proveedor - rut={}", requestDTO.getRutProveedor());

        proveedorRepository.findByRutProveedor(requestDTO.getRutProveedor())
                .ifPresent(existente -> {
                    logger.warn("Conflicto de negocio: el rut ya existe para proveedor id={}", existente.getIdProveedor());
                    throw new BadRequestException("El rut del proveedor ya está registrado.");
                });

        Proveedor proveedor = proveedorMapper.toEntity(requestDTO);
        Proveedor savedProveedor = proveedorRepository.save(proveedor);

        logger.info("Proveedor creado con éxito. id={}", savedProveedor.getIdProveedor());
        
        // ¡CAMBIO CLAVE! Usamos mapToDTO para incluir la dirección del otro microservicio
        return mapToDTO(savedProveedor);
    }

    public List<ProveedorResponseDTO> listarTodos() {
        logger.info("Inicio de operación: listar proveedores");
        List<Proveedor> proveedores = proveedorRepository.findAll();
        logger.info("Listado de proveedores devuelto con éxito. total={}", proveedores.size());
        
        // Transformamos cada entidad usando el método que llama a Direcciones
        return proveedores.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public ProveedorResponseDTO buscarPorId(Integer id) {
        logger.info("Inicio de operación: buscar proveedor por id={}", id);
        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Proveedor no encontrado. id={}", id);
                    return new ResourceNotFoundException("Proveedor con ID " + id + " no encontrado");
                });
        
        logger.info("Proveedor encontrado con éxito. id={}", id);
        return mapToDTO(proveedor);
    }

    public ProveedorResponseDTO actualizar(Integer id, ProveedorRequestDTO requestDTO) {
        logger.info("Inicio de operación: actualizar proveedor id={}", id);

        Proveedor proveedorExistente = proveedorRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Proveedor no encontrado para actualizar. id={}", id);
                    return new ResourceNotFoundException("Proveedor con ID " + id + " no encontrado");
                });

        proveedorRepository.findByRutProveedor(requestDTO.getRutProveedor())
                .filter(existente -> !existente.getIdProveedor().equals(id))
                .ifPresent(existente -> {
                    logger.warn("Conflicto de negocio: el rut ya está en uso por proveedor id={}", existente.getIdProveedor());
                    throw new BadRequestException("El rut del proveedor ya está registrado para otro proveedor.");
                });

        Proveedor updatedProveedor = proveedorMapper.toEntity(requestDTO, id);
        Proveedor savedProveedor = proveedorRepository.save(updatedProveedor);

        logger.info("Proveedor actualizado con éxito. id={}", id);
        return mapToDTO(savedProveedor);
    }

    public void eliminar(Integer id) {
        logger.info("Inicio de operación: eliminar proveedor id={}", id);
        if (!proveedorRepository.existsById(id)) {
            logger.warn("Proveedor no encontrado para eliminar. id={}", id);
            throw new ResourceNotFoundException("Proveedor con ID " + id + " no encontrado");
        }
        proveedorRepository.deleteById(id);
        logger.info("Proveedor eliminado con éxito. id={}", id);
    }

    /**
     * Método interno que orquesta la integración con ms_direcciones.
     * Si la dirección no existe o el microservicio falla, devuelve la data parcial.
     */
    private ProveedorResponseDTO mapToDTO(Proveedor proveedor) {
        DireccionDTO direccionDTO = null;
        try {
            // Verificamos que el proveedor tenga un ID de dirección asociado en la entidad
            if (proveedor.getDireccionProveedor() != null) {
                direccionDTO = direccionClient.obtenerDireccionPorId(proveedor.getDireccionProveedor());
            }
        } catch (FeignException.NotFound ex) {
            logger.warn("Direccion ID {} no encontrada para el proveedor ID {}", 
                        proveedor.getDireccionProveedor(), proveedor.getIdProveedor());
        } catch (FeignException ex) {
            logger.error("Fallo de comunicación con ms_direcciones: {}", ex.getMessage());
        }

        // Pasamos tanto la entidad como el DTO (que puede ser null) al Mapper
        return proveedorMapper.toResponseDTO(proveedor, direccionDTO);
    }
}