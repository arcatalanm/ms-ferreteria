package ferrefix.ms_compras.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompraRequestDTO {

    @NotNull(message = "El ID del proveedor es obligatorio")
    private Integer idProveedor;

    @NotEmpty(message = "La orden debe tener al menos un detalle")
    private List<DetalleCompraRequestDTO> detalles;
}
