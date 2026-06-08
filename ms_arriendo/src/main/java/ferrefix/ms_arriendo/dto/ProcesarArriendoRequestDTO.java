package ferrefix.ms_arriendo.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcesarArriendoRequestDTO {

    @NotNull(message = "El RUN del cliente es obligatorio")
    private Integer runCliente;

    @NotNull(message = "Los días de arriendo son obligatorios")
    @Min(value = 1, message = "Los días de arriendo deben ser al menos 1")
    private Integer diasArriendo;
}
