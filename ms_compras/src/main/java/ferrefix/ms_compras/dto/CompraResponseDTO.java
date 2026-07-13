package ferrefix.ms_compras.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompraResponseDTO {
    private Long idCompra;
    private Integer idProveedor;
    private LocalDateTime fechaCompra;
    private Integer totalCompra;
    private Integer neto;
    private Integer iva;
    private String estado;
    private List<DetalleCompraResponseDTO> detalles;
}
