package ferrefix.ms_reportes.dto;

import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class VentaDTO {
    private Long idVenta;
    private String runCliente;
    private String runEmpleado;
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime fechaVenta;
    private Integer totalVenta;
    private Integer neto;
    private Integer iva;
    private String nombreTipoPago;
    private List<DetalleVentaDTO> detalles;
}
