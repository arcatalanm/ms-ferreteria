package ferrefix.ms_ventas.service;

import java.util.List;

import org.springframework.stereotype.Service;

import ferrefix.ms_ventas.dto.TipoPagoRequestDTO;
import ferrefix.ms_ventas.dto.TipoPagoResponseDTO;
import ferrefix.ms_ventas.model.TipoPago;
import ferrefix.ms_ventas.repository.TipoPagoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class TipoPagoService {

    private final TipoPagoRepository tipoPagoRepository;

    public TipoPago crearTipoPago(TipoPagoRequestDTO dto) {
        if (tipoPagoRepository.existsByNombreTipoPago(dto.getNombreTipoPago())) {
            throw new IllegalArgumentException("El tipo de pago '" + dto.getNombreTipoPago() + "' ya existe");
        }

        TipoPago tipoPago = TipoPago.builder()
                .nombreTipoPago(dto.getNombreTipoPago())
                .build();

        return tipoPagoRepository.save(tipoPago);
    }

    public List<TipoPagoResponseDTO> listarTiposPago() {
        return tipoPagoRepository.findAll().stream()
                .map(this::mapToDTO)
                .toList();
    }

    public TipoPagoResponseDTO buscarTipoPagoPorId(Integer idTipoPago) {
        TipoPago tipoPago = tipoPagoRepository.findById(idTipoPago)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el tipo de pago con id: " + idTipoPago));
        return mapToDTO(tipoPago);
    }

    public TipoPago actualizarTipoPago(Integer idTipoPago, TipoPagoRequestDTO dto) {
        TipoPago tipoPagoExistente = tipoPagoRepository.findById(idTipoPago)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el tipo de pago con id: " + idTipoPago));

        if (tipoPagoRepository.existsByNombreTipoPago(dto.getNombreTipoPago())
                && !tipoPagoExistente.getNombreTipoPago().equalsIgnoreCase(dto.getNombreTipoPago())) {
            throw new IllegalArgumentException("El tipo de pago '" + dto.getNombreTipoPago() + "' ya existe");
        }

        tipoPagoExistente.setNombreTipoPago(dto.getNombreTipoPago());
        return tipoPagoRepository.save(tipoPagoExistente);
    }

    public void eliminarTipoPago(Integer idTipoPago) {
        if (!tipoPagoRepository.existsById(idTipoPago)) {
            throw new IllegalArgumentException("No se encontró el tipo de pago con id: " + idTipoPago);
        }
        tipoPagoRepository.deleteById(idTipoPago);
    }

    private TipoPagoResponseDTO mapToDTO(TipoPago tipoPago) {
        return TipoPagoResponseDTO.builder()
                .idTipoPago(tipoPago.getIdTipoPago())
                .nombreTipoPago(tipoPago.getNombreTipoPago())
                .build();
    }
}
