package ferrefix.ms_ventas.mapper;

import org.springframework.stereotype.Component;

import ferrefix.ms_ventas.dto.TipoPagoRequestDTO;
import ferrefix.ms_ventas.dto.TipoPagoResponseDTO;
import ferrefix.ms_ventas.model.TipoPago;

@Component
public class TipoPagoMapper {

    public TipoPago toEntity(TipoPagoRequestDTO dto) {
        return TipoPago.builder()
                .nombreTipoPago(dto.getNombreTipoPago())
                .build();
    }

    public TipoPagoResponseDTO toResponseDTO(TipoPago tipoPago) {
        return TipoPagoResponseDTO.builder()
                .idTipoPago(tipoPago.getIdTipoPago())
                .nombreTipoPago(tipoPago.getNombreTipoPago())
                .build();
    }
}
