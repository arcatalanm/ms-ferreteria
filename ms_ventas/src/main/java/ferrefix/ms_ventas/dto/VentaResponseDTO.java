package ferrefix.ms_ventas.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

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

public class VentaResponseDTO {
    private Long idVenta;
    
    private String runCliente;

    private String runEmpleado;
    // Obtenemos dia-mes-año hora:minuto:segundo
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime fechaVenta;


    private Integer totalVenta;

    private String nombreTipoPago; // Agregado para mostrar nombre del tipo de pago

    private List<DetalleVentaResponseDTO> detalles; // Lista de detalles de la venta - Va a tener el resumen de compra (despliege de boleta o factura)
}
