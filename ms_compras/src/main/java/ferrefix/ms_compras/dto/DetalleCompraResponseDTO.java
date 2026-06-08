package ferrefix.ms_compras.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetalleCompraResponseDTO {
    private Long idDetalleCompra;
    private Long idProducto;
    private Integer cantidad;
    private Integer precioCompraUnitario;
}
