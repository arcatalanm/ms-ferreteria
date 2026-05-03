package ferrefix.ms_inventorario.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

public class ProductoRequestDTO {
    @NotNull(message = "La categoría del producto es obligatoria")
    private Integer categoria;

    @NotNull(message = "La unidad de medida es obligatoria")
    private Integer unidadMedida;

    @NotBlank(message = "El código de barras del producto es obligatorio")
    @Size(max = 50, message = "El código de barras del producto no puede exceder los 50 caracteres")
    private String codigoBarras;

    @NotBlank(message = "El nombre del producto es obligatorio")
    @Size(max = 100, message = "El nombre del producto no puede exceder los 100 caracteres")
    private String nombre;

    @NotNull(message = "El stock del producto es obligatorio")
    @Min(value = 0, message = "El stock del producto no puede ser negativo")
    private Integer stock;

    @NotNull(message = "El precio de venta del producto es obligatorio")
    @Min(value = 0, message = "El precio de venta del producto no puede ser negativo")
    private Integer precioVenta;
}
