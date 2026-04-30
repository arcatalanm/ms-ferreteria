package ferrefix.ms_ventas.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tipo_pago")
@AllArgsConstructor @NoArgsConstructor
@Getter @Setter @Builder
public class TipoPago {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_pago")
    private Integer idTipoPago; // Usamos Integer por compatibilidad y escalabilidad 

    @NotBlank(message = "El nombre del tipo de pago no puede estar vacío")
    @Column(name = "nombre_tipo_pago", unique = true, length = 50)
    private String nombreTipoPago;
}