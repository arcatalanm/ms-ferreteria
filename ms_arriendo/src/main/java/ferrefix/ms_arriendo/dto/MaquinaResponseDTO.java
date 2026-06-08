package ferrefix.ms_arriendo.dto;

import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaquinaResponseDTO {
    private Integer idEquipo;
    private String codigoInterno;
    private String nombreMaquina;
    private Integer precioPorDia;
    private String estado;
    private Integer runCliente;
    private LocalDate fechaDevolucionPactada;
}
