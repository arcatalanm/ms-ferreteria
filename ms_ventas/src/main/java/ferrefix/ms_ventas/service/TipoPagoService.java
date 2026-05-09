package ferrefix.ms_ventas.service;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import ferrefix.ms_ventas.dto.TipoPagoRequestDTO;
import ferrefix.ms_ventas.dto.TipoPagoResponseDTO;
import ferrefix.ms_ventas.exception.BadRequestException;
import ferrefix.ms_ventas.exception.ResourceNotFoundException;
import ferrefix.ms_ventas.model.TipoPago;
import ferrefix.ms_ventas.repository.TipoPagoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

/**
 * Servicio de negocio para la entidad TipoPago.
 * Gestiona la lógica de validación, persistencia y transformación de datos.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class TipoPagoService {
    
    private static final Logger logger = LoggerFactory.getLogger(TipoPagoService.class);
    
    private final TipoPagoRepository tipoPagoRepository;
    
    /**
     * Obtiene todos los tipos de pago.
     */
    public List<TipoPagoResponseDTO> obtenerTodos() {
        logger.info("Iniciando búsqueda de todos los tipos de pago");
        
        List<TipoPagoResponseDTO> tiposPago = tipoPagoRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
        
        logger.info("Búsqueda de tipos de pago completada. Total encontrado: {}", tiposPago.size());
        return tiposPago;
    }
    
    /**
     * Obtiene un tipo de pago por su ID.
     */
    public TipoPagoResponseDTO obtenerPorId(Integer id) {
        logger.info("Iniciando búsqueda de tipo de pago con ID: {}", id);
        
        if (id == null || id <= 0) {
            logger.warn("ID inválido para tipo de pago: {}", id);
            throw new BadRequestException("El ID del tipo de pago debe ser mayor a 0");
        }
        
        TipoPago tipoPago = tipoPagoRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Tipo de pago no encontrado con ID: {}", id);
                    return new ResourceNotFoundException("Tipo de pago no encontrado con ID: " + id);
                });
        
        TipoPagoResponseDTO tipoPagoDTO = mapToResponseDTO(tipoPago);
        logger.info("Tipo de pago obtenido exitosamente con ID: {}", id);
        
        return tipoPagoDTO;
    }
    
    /**
     * Crea un nuevo tipo de pago.
     */
    public TipoPagoResponseDTO crear(TipoPagoRequestDTO tipoPagoDTO) {
        logger.info("Iniciando creación de nuevo tipo de pago: {}", tipoPagoDTO.getNombreTipoPago());
        
        // Validar que el nombre no sea nulo o vacío
        if (tipoPagoDTO.getNombreTipoPago() == null || tipoPagoDTO.getNombreTipoPago().trim().isEmpty()) {
            logger.warn("Intento de crear tipo de pago con nombre vacío");
            throw new BadRequestException("El nombre del tipo de pago no puede estar vacío");
        }
        
        // Validar que no exista un tipo de pago con el mismo nombre
        if (tipoPagoRepository.existsByNombreTipoPago(tipoPagoDTO.getNombreTipoPago())) {
            logger.warn("Intento de crear tipo de pago con nombre duplicado: {}", tipoPagoDTO.getNombreTipoPago());
            throw new BadRequestException("Ya existe un tipo de pago con el nombre: " + tipoPagoDTO.getNombreTipoPago());
        }
        
        TipoPago tipoPago = TipoPago.builder()
                .nombreTipoPago(tipoPagoDTO.getNombreTipoPago())
                .build();
        
        TipoPago tipoPagoGuardado = tipoPagoRepository.save(tipoPago);
        
        TipoPagoResponseDTO resultado = mapToResponseDTO(tipoPagoGuardado);
        logger.info("Tipo de pago creado exitosamente con ID: {}", resultado.getIdTipoPago());
        
        return resultado;
    }
    
    /**
     * Actualiza un tipo de pago existente.
     */
    public TipoPagoResponseDTO actualizar(Integer id, TipoPagoRequestDTO tipoPagoDTO) {
        logger.info("Iniciando actualización de tipo de pago con ID: {}", id);
        
        if (id == null || id <= 0) {
            logger.warn("ID inválido para actualizar tipo de pago: {}", id);
            throw new BadRequestException("El ID del tipo de pago debe ser mayor a 0");
        }
        
        TipoPago tipoPago = tipoPagoRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Tipo de pago no encontrado para actualizar con ID: {}", id);
                    return new ResourceNotFoundException("Tipo de pago no encontrado con ID: " + id);
                });
        
        // Validar que el nombre no sea nulo o vacío
        if (tipoPagoDTO.getNombreTipoPago() != null && !tipoPagoDTO.getNombreTipoPago().trim().isEmpty()) {
            // Validar que no exista otro tipo de pago con el mismo nombre
            tipoPagoRepository.findByNombreTipoPago(tipoPagoDTO.getNombreTipoPago()).ifPresent(existente -> {
                if (!existente.getIdTipoPago().equals(id)) {
                    logger.warn("Intento de actualizar tipo de pago con nombre duplicado: {}", tipoPagoDTO.getNombreTipoPago());
                    throw new BadRequestException("Ya existe otro tipo de pago con el nombre: " + tipoPagoDTO.getNombreTipoPago());
                }
            });
            
            tipoPago.setNombreTipoPago(tipoPagoDTO.getNombreTipoPago());
        }
        
        TipoPago tipoPagoActualizado = tipoPagoRepository.save(tipoPago);
        
        TipoPagoResponseDTO resultado = mapToResponseDTO(tipoPagoActualizado);
        logger.info("Tipo de pago actualizado exitosamente con ID: {}", id);
        
        return resultado;
    }
    
    /**
     * Elimina un tipo de pago.
     */
    public void eliminar(Integer id) {
        logger.info("Iniciando eliminación de tipo de pago con ID: {}", id);
        
        if (id == null || id <= 0) {
            logger.warn("ID inválido para eliminar tipo de pago: {}", id);
            throw new BadRequestException("El ID del tipo de pago debe ser mayor a 0");
        }
        
        TipoPago tipoPago = tipoPagoRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Tipo de pago no encontrado para eliminar con ID: {}", id);
                    return new ResourceNotFoundException("Tipo de pago no encontrado con ID: " + id);
                });
        
        tipoPagoRepository.delete(tipoPago);
        logger.info("Tipo de pago eliminado exitosamente con ID: {}", id);
    }
    
    /**
     * Convierte una entidad TipoPago a TipoPagoResponseDTO.
     */
    private TipoPagoResponseDTO mapToResponseDTO(TipoPago tipoPago) {
        return TipoPagoResponseDTO.builder()
                .idTipoPago(tipoPago.getIdTipoPago())
                .nombreTipoPago(tipoPago.getNombreTipoPago())
                .build();
    }
}

