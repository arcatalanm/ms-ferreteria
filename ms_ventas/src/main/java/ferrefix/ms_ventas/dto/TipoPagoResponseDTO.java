package ferrefix.ms_ventas.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TipoPagoResponseDTO {
    private Integer idTipoPago;
    private String nombreTipoPago;
}
