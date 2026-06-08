package ferrefix.ms_compras.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockIncrementDTO {
    private Long idProducto;
    private Integer cantidadASumar;
}
