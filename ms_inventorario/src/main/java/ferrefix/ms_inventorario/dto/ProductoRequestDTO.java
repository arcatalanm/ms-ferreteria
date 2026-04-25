package ferrefix.ms_inventorario.dto;

import ferrefix.ms_inventorario.model.CategoriaProducto;
import ferrefix.ms_inventorario.model.Producto;
import ferrefix.ms_inventorario.model.UnidadMedida;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    private Byte categoria;
    @NotNull(message = "La unidad de medida del producto es obligatoria")
    private Byte unidad;
    @NotBlank(message = "El código de barras del producto es obligatorio")
    private String codigoBarras;
    @NotBlank(message = "El nombre del producto es obligatorio")
    private String nombre;
    @NotNull(message = "El stock del producto es obligatorio")
    @Min(value = 0, message = "El stock del producto no puede ser negativo")
    private Integer stock;
    @NotNull(message = "El precio de venta del producto es obligatorio")
    @Min(value = 0, message = "El precio de venta del producto no puede ser negativo")
    private Integer precio;

    public Producto toModel(CategoriaProducto categoria, UnidadMedida unidad) {
        return Producto.builder()
                .categoriaProducto(categoria)
                .unidadMedida(unidad)
                .codigoBarrasProducto(this.codigoBarras)
                .nombreProducto(this.nombre)
                .stockProducto(this.stock)
                .precioVentaProducto(this.precio)
                .build();
    }
}
