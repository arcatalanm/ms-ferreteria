package ferrefix.ms_usuarios.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
@Entity
@Table(name = "direccion")
public class Direccion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_direccion")
    private Long idDireccion;

    @NotBlank(message = "La calle no puede estar vacía")
    @Column(name = "calle", nullable = false, length = 100)
    private String calle;

    @NotNull(message = "El número de la dirección es obligatorio")
    @Column(name = "numero", nullable = false)
    private Integer numero;

    @Column(name = "departamento", length = 20)
    private String departamento;

    @NotBlank(message = "La comuna es obligatoria")
    @Column(name = "comuna", nullable = false, length = 50)
    private String comuna;

    @NotBlank(message = "La ciudad es obligatoria")
    @Column(name = "ciudad", nullable = false, length = 50)
    private String ciudad;

    // Relación simple ManyToOne
    @ManyToOne
    @JoinColumn(name = "run_cliente", nullable = false)
    private Cliente cliente;
}
