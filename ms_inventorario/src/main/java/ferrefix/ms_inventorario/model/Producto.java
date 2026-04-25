package ferrefix.ms_inventorario.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
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

@Entity
@Table(name = "producto")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto", nullable = false)
    private Long idProducto;

    @NotNull(message = "La categoría del producto es obligatoria")
    @ManyToOne
    @JoinColumn(name = "id_categoria", nullable = false)
    private CategoriaProducto categoriaProducto;

    @NotNull(message = "La unidad de medida del producto es obligatoria")
    @ManyToOne
    @JoinColumn(name = "id_unidad", nullable = false)
    private UnidadMedida unidadMedida;

    @Size(max = 50, message = "El código de barras del producto no puede exceder los 50 caracteres")
    @Column(name = "codigo_barras_producto", nullable = true, length = 50, unique = true)
    private String codigoBarrasProducto;

    @Size(max = 100, message = "El nombre del producto no puede exceder los 100 caracteres")
    @Column(name = "nombre_producto", nullable = false, length = 100)
    private String nombreProducto;
    
    @NotNull(message = "El stock del producto es obligatorio")
    @Min(value = 0, message = "El stock del producto no puede ser negativo")
    @Column(name = "stock_producto", nullable = false)
    private Integer stockProducto;

    @NotNull(message = "El precio de venta del producto es obligatorio")
    @Min(value = 0, message = "El precio de venta del producto no puede ser negativo")
    @Column(name = "precio_venta_producto", nullable = false)
    private Integer precioVentaProducto;
}
