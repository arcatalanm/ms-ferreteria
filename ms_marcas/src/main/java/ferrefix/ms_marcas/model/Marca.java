package ferrefix.ms_marcas.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "marcas")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Marca {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_marca")
    private Integer idMarca;

    @NotBlank(message = "El nombre de la marca es obligatorio")
    @Column(name = "nombre_marca", length = 100, nullable = false)
    private String nombreMarca;
}
