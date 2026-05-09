package ferrefix.ms_inventario.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder

public class ProductoResponseDTO {
    private Integer id;
    private String codigoBarras;

    private String nombre;
    
    private Integer stock;
    private Integer precioVenta;

    private Integer unidadMedida;
    private String categoria;
}
