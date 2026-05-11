package ferrefix.ms_ventas.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "venta")
@AllArgsConstructor 
@NoArgsConstructor
@Getter 
@Setter 
@Builder
public class Venta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idVenta;

    @Column(name = "fecha_venta", nullable = false)
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime fechaVenta;

    @NotNull
    private Integer totalVenta;

    // De UsuariosClient
    @NotNull
    @Column(name = "run_cliente_fk")
    private Integer runCliente;

    // De UsuariosClient
    @NotNull
    @Column(name = "run_empleado_fk")
    private Integer runEmpleado;

    // Relación simple ManyToOne (Sin listas en la otra entidad)
    @ManyToOne
    @JoinColumn(name = "id_tipo_pago_fk", nullable = false)
    private TipoPago tipoPago;
}