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

public class DetalleVentaResponseDTO {
    private Long idProducto;

    private String nombreProducto;
    
    private Integer cantidad;

    // Precio del moment para el producto, no el actual del inventario
    private Integer precioUnitario;

    // cantidad * precioUnitario = subtotal
    private Integer subtotal;   
}
