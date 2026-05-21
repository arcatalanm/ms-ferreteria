package ferrefix.ms_ventas.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
public class VentaRequestDTO {
    @NotBlank(message = "El runCliente es obligatorio y debe incluir DV")
    private String runCliente;

    @NotBlank(message = "El runEmpleado es obligatorio y debe incluir DV")
    private String runEmpleado;

    @NotNull(message = "El idTipoPago es obligatorio")
    private Integer idTipoPago;

    @NotEmpty(message = "La lista de detalles no puede estar vacía o no contener nada")
    @Valid
    private List<DetalleVentaRequestDTO> detalles;
}
