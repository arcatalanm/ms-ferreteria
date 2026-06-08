package ferrefix.ms_sugerencia.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "sugerencias")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sugerencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_sugerencia")
    private Long idSugerencia;

    @Column(name = "contenido_mensaje", length = 1000, nullable = false)
    private String contenidoMensaje;

    @Column(name = "fecha_ingreso", nullable = false)
    private LocalDateTime fechaIngreso;
}
