package ferrefix.ms_usuarios.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// ... (Tus imports) ...

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ClienteResponseDTO {
    private String runClienteCompleto;
    private String nombreClienteCompleto;
    private String emailCliente;
    private String telefonoCliente;
    private String direccionCliente;
}