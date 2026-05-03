package ferrefix.ms_ventas.dto;

import jakarta.validation.constraints.NotBlank;
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
public class TipoPagoRequestDTO {
    @NotBlank(message = "El nombre del tipo de pago es obligatorio")
    private String nombreTipoPago;
}
