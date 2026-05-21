package ferrefix.ms_ventas.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import ferrefix.ms_ventas.dto.TipoPagoRequestDTO;
import ferrefix.ms_ventas.dto.TipoPagoResponseDTO;
import ferrefix.ms_ventas.exception.BadRequestException;
import ferrefix.ms_ventas.exception.ResourceNotFoundException;
import ferrefix.ms_ventas.mapper.TipoPagoMapper;
import ferrefix.ms_ventas.model.TipoPago;
import ferrefix.ms_ventas.repository.TipoPagoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class TipoPagoService {

    private static final Logger logger = LoggerFactory.getLogger(TipoPagoService.class);
    private final TipoPagoRepository tipoPagoRepository;
    private final TipoPagoMapper tipoPagoMapper;

    public List<TipoPagoResponseDTO> obtenerTodos() {
        logger.info("Listando todos los tipos de pago");
        List<TipoPagoResponseDTO> lista = tipoPagoRepository.findAll().stream()
                .map(tipoPagoMapper::toResponseDTO)
                .toList();
        logger.info("Listado completado. Total: {}", lista.size());
        return lista;
    }

    public TipoPagoResponseDTO obtenerPorId(Integer id) {
        logger.info("Buscando tipo de pago ID: {}", id);
        TipoPago tipoPago = tipoPagoRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("404 - Tipo de pago ID {} no encontrado", id);
                    return new ResourceNotFoundException("Tipo de pago no encontrado con ID: " + id);
                });
        logger.info("Tipo de pago ID {} encontrado: '{}'", id, tipoPago.getNombreTipoPago());
        return tipoPagoMapper.toResponseDTO(tipoPago);
    }

    public TipoPagoResponseDTO crear(TipoPagoRequestDTO dto) {
        logger.info("Iniciando creación de tipo de pago: '{}'", dto.getNombreTipoPago());

        // @Valid + @NotBlank en el DTO ya garantiza que el nombre no llegue vacío
        if (tipoPagoRepository.existsByNombreTipoPago(dto.getNombreTipoPago())) {
            logger.warn("Conflicto: el tipo de pago '{}' ya existe", dto.getNombreTipoPago());
            throw new BadRequestException("Ya existe un tipo de pago con el nombre: " + dto.getNombreTipoPago());
        }

        TipoPago guardado = tipoPagoRepository.save(tipoPagoMapper.toEntity(dto));
        logger.info("Tipo de pago '{}' creado exitosamente con ID: {}", guardado.getNombreTipoPago(), guardado.getIdTipoPago());
        return tipoPagoMapper.toResponseDTO(guardado);
    }

    public TipoPagoResponseDTO actualizar(Integer id, TipoPagoRequestDTO dto) {
        logger.info("Iniciando actualización de tipo de pago ID: {}", id);

        TipoPago existente = tipoPagoRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("404 - Tipo de pago ID {} no encontrado para actualizar", id);
                    return new ResourceNotFoundException("Tipo de pago no encontrado con ID: " + id);
                });

        tipoPagoRepository.findByNombreTipoPago(dto.getNombreTipoPago())
                .filter(otro -> !otro.getIdTipoPago().equals(id))
                .ifPresent(otro -> {
                    logger.warn("Conflicto: el nombre '{}' ya pertenece a otro tipo de pago", dto.getNombreTipoPago());
                    throw new BadRequestException("Ya existe otro tipo de pago con el nombre: " + dto.getNombreTipoPago());
                });

        existente.setNombreTipoPago(dto.getNombreTipoPago());
        TipoPago actualizado = tipoPagoRepository.save(existente);
        logger.info("Tipo de pago ID {} actualizado a '{}'", id, actualizado.getNombreTipoPago());
        return tipoPagoMapper.toResponseDTO(actualizado);
    }

    public void eliminar(Integer id) {
        logger.info("Iniciando eliminación de tipo de pago ID: {}", id);
        TipoPago existente = tipoPagoRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("404 - Tipo de pago ID {} no encontrado para eliminar", id);
                    return new ResourceNotFoundException("Tipo de pago no encontrado con ID: " + id);
                });
        tipoPagoRepository.delete(existente);
        logger.info("Tipo de pago ID {} eliminado exitosamente", id);
    }
}