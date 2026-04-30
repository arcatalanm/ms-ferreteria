package ferrefix.ms_ventas.model;

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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "detalle_venta")
@AllArgsConstructor @NoArgsConstructor
@Getter @Setter @Builder
public class DetalleVenta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDetalle;

    @NotNull
    @Column(name = "id_producto_fk")
    private Long idProducto; // FK Lógica al MS-Inventario

    @NotNull
    @Min(1)
    private Integer cantidad;

    @NotNull
    @Column(name = "precio_unitario_historico")
    private Integer precioUnitario; // Guardamos el precio del momento

    @ManyToOne
    @JoinColumn(name = "id_venta", nullable = false)
    private Venta venta; // Relación simple hacia la cabecera
}