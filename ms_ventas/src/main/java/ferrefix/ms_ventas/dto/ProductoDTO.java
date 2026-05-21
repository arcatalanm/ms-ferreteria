package ferrefix.ms_ventas.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductoDTO {
    private Long id;
    private String nombre;
    private Integer precioVenta;
    private Integer stock;
}