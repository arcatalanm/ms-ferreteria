package ferrefix.ms_direcciones.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "direccion")
@AllArgsConstructor @NoArgsConstructor
@Getter @Setter @Builder
public class Direccion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_direccion")
    private Long idDireccion;

    @NotBlank(message = "La calle no puede estar vacía")
    @Column(length = 100, nullable = false)
    private String calle;

    @NotNull(message = "El número no puede ser nulo")
    @Column(nullable = false)
    private Integer numero;

    @Column(length = 20)
    private String departamento; // Opcional

    @NotBlank(message = "La comuna no puede estar vacía")
    @Column(length = 50, nullable = false)
    private String comuna;

    @NotBlank(message = "La ciudad no puede estar vacía")
    @Column(length = 50, nullable = false)
    private String ciudad;
}