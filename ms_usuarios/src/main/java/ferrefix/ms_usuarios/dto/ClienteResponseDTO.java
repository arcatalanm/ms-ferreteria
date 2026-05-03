package ferrefix.ms_usuarios.dto;

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
public class ClienteResponseDTO {
    // El run completo se muestra como un string concatenado con el dv, por ejemplo: "12345678-K"
    private String runClienteCompleto;
    
    // Tendra pnombre, snombre, appaterno y apmaterno concatenados para mostrar el nombre completo del cliente
    private String nombreClienteCompleto;

    private String emailCliente;
    private String telefonoCliente;
}
