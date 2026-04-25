package ferrefix.ms_inventorario.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
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
@Table(name = "unidad_medida")

public class UnidadMedida {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_unidad", nullable = false)
    private Byte idUnidad;

    @Size(max = 50, message = "El nombre de la unidad de medida no puede exceder los 50 caracteres")
    @NotBlank(message = "El nombre de la unidad de medida es obligatorio")
    @Column(name = "nombre_unidad", nullable = false, length = 50)
    private String nombreUnidad;

    @Size(max = 10, message = "La abreviatura de la unidad de medida no puede exceder los 10 caracteres")
    @NotBlank(message = "La abreviatura de la unidad de medida es obligatoria")
    @Column(name = "abreviatura_unidad", nullable = false, length = 10)
    private String abreviaturaUnidad;
}
