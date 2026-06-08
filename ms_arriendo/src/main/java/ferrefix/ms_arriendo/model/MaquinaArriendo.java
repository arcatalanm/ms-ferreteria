package ferrefix.ms_arriendo.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "maquinas_arriendo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaquinaArriendo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_equipo")
    private Integer idEquipo;

    @Column(name = "codigo_interno", length = 50, nullable = false, unique = true)
    private String codigoInterno;

    @Column(name = "nombre_maquina", length = 100, nullable = false)
    private String nombreMaquina;

    @Column(name = "precio_por_dia", nullable = false)
    private Integer precioPorDia;

    @Column(name = "estado", length = 30, nullable = false)
    private String estado;

    @Column(name = "run_cliente")
    private Integer runCliente;

    @Column(name = "fecha_devolucion_pactada")
    private LocalDate fechaDevolucionPactada;
}
