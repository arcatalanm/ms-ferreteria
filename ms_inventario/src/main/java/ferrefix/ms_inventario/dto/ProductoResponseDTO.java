package ferrefix.ms_inventario.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ProductoResponseDTO {
    private Long id;
    private String codigoBarras;
    private String nombre;
    private Integer stock;
    private Integer precioVenta;
    private String unidadMedida;
    private String categoria;
}