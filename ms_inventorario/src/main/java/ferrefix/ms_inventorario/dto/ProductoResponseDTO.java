package ferrefix.ms_inventorario.dto;

import ferrefix.ms_inventorario.model.Producto;
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
    // Atributos del DTO de respuesta para Producto (Visualizacion para el Frontend)
    private Long id;
    private String categoria;
    private String unidadMedida;
    private String codigoBarras;
    private String nombre;
    private Integer stock;
    private Integer precio;

    public static ProductoResponseDTO fromModel(Producto producto) {
        if (producto == null) {
            return null;
        }
        return ProductoResponseDTO.builder()
                .id(producto.getIdProducto())
                .categoria(producto.getCategoriaProducto().getNombreCategoria())
                .unidadMedida(producto.getUnidadMedida().getNombreUnidad())
                .codigoBarras(producto.getCodigoBarrasProducto())
                .nombre(producto.getNombreProducto())
                .stock(producto.getStockProducto())
                .precio(producto.getPrecioVentaProducto())
                .build();
    }

}
