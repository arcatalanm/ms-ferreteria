package ferrefix.ms_proveedores.service;


import feign.FeignException;
import ferrefix.ms_proveedores.client.DireccionClient;
import ferrefix.ms_proveedores.dto.DireccionDTO;
import ferrefix.ms_proveedores.dto.ProveedorRequestDTO;
import ferrefix.ms_proveedores.dto.ProveedorResponseDTO;
import ferrefix.ms_proveedores.exception.BadRequestException;
import ferrefix.ms_proveedores.exception.ResourceNotFoundException;
import ferrefix.ms_proveedores.mapper.ProveedorMapper;
import ferrefix.ms_proveedores.model.Proveedor;
import ferrefix.ms_proveedores.repository.ProveedorRepository;
import ferrefix.ms_proveedores.util.RutUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ProveedorService {

    private static final Logger logger = LoggerFactory.getLogger(ProveedorService.class);

    private final ProveedorRepository proveedorRepository;
    private final ProveedorMapper proveedorMapper;
    private final DireccionClient direccionClient;

    public ProveedorResponseDTO guardar(ProveedorRequestDTO dto) {
        logger.info("Iniciando creación de proveedor RUT: {}", dto.getRutProveedor());

        if (!RutUtil.esValido(dto.getRutProveedor())) {
            logger.warn("RUT inválido recibido: {}", dto.getRutProveedor());
            throw new BadRequestException("El RUT ingresado no es válido: " + dto.getRutProveedor());
        }

        Integer run = RutUtil.extraerRun(dto.getRutProveedor());
        Character dv  = RutUtil.extraerDv(dto.getRutProveedor());

        proveedorRepository.findByRutProveedor(run).ifPresent(existente -> {
            logger.warn("Conflicto: el RUT {} ya está registrado para el proveedor ID {}",
                    run, existente.getIdProveedor());
            throw new BadRequestException("El RUT del proveedor ya está registrado.");
        });

        Proveedor proveedor = proveedorMapper.toEntity(dto, run, dv);
        Proveedor guardado = proveedorRepository.save(proveedor);

        logger.info("Proveedor creado exitosamente. ID: {}", guardado.getIdProveedor());
        return mapToDTO(guardado);
    }

    public List<ProveedorResponseDTO> listarTodos() {
        logger.info("Iniciando listado de todos los proveedores");
        List<ProveedorResponseDTO> lista = proveedorRepository.findAll().stream()
                .map(this::mapToDTO)
                .toList();
        logger.info("Listado completado. Total: {}", lista.size());
        return lista;
    }

    public ProveedorResponseDTO buscarPorId(Integer id) {
        logger.info("Buscando proveedor por ID: {}", id);
        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("404 - Proveedor ID {} no encontrado", id);
                    return new ResourceNotFoundException("Proveedor con ID " + id + " no encontrado.");
                });
        logger.info("Proveedor ID {} encontrado exitosamente", id);
        return mapToDTO(proveedor);
    }

    public ProveedorResponseDTO actualizar(Integer id, ProveedorRequestDTO dto) {
        logger.info("Iniciando actualización de proveedor ID: {}", id);

        Proveedor existente = proveedorRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("404 - Proveedor ID {} no encontrado para actualizar", id);
                    return new ResourceNotFoundException("Proveedor con ID " + id + " no encontrado.");
                });

        if (!RutUtil.esValido(dto.getRutProveedor())) {
            logger.warn("RUT inválido al actualizar: {}", dto.getRutProveedor());
            throw new BadRequestException("El RUT ingresado no es válido: " + dto.getRutProveedor());
        }

        Integer run = RutUtil.extraerRun(dto.getRutProveedor());
        Character dv  = RutUtil.extraerDv(dto.getRutProveedor());

        // Verificar que el nuevo RUT no pertenezca a otro proveedor
        proveedorRepository.findByRutProveedor(run)
                .filter(otro -> !otro.getIdProveedor().equals(id))
                .ifPresent(otro -> {
                    logger.warn("Conflicto: el RUT {} ya pertenece al proveedor ID {}", run, otro.getIdProveedor());
                    throw new BadRequestException("El RUT ya está registrado para otro proveedor.");
                });

        // Mutar la entidad existente en vez de reconstruirla (patrón updateEntity)
        proveedorMapper.updateEntity(existente, dto, run, dv);
        Proveedor actualizado = proveedorRepository.save(existente);

        logger.info("Proveedor ID {} actualizado exitosamente", id);
        return mapToDTO(actualizado);
    }

    public void eliminar(Integer id) {
        logger.info("Iniciando eliminación de proveedor ID: {}", id);
        if (!proveedorRepository.existsById(id)) {
            logger.warn("404 - Proveedor ID {} no encontrado para eliminar", id);
            throw new ResourceNotFoundException("Proveedor con ID " + id + " no encontrado.");
        }
        proveedorRepository.deleteById(id);
        logger.info("Proveedor ID {} eliminado exitosamente", id);
    }

    // ─── Helper privado ───────────────────────────────────────────────────────
    private ProveedorResponseDTO mapToDTO(Proveedor proveedor) {
        DireccionDTO direccionDTO = null;
        try {
            if (proveedor.getDireccionProveedor() != null) {
                direccionDTO = direccionClient.obtenerDireccionPorId(proveedor.getDireccionProveedor());
            }
        } catch (FeignException.NotFound ex) {
            logger.warn("Dirección ID {} no encontrada para proveedor ID {}",
                    proveedor.getDireccionProveedor(), proveedor.getIdProveedor());
        } catch (FeignException ex) {
            logger.error("Fallo de comunicación con ms_direcciones: {}", ex.getMessage());
        }
        return proveedorMapper.toResponseDTO(proveedor, direccionDTO);
    }
}