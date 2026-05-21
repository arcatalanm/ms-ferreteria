package ferrefix.ms_usuarios.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClienteRequestDTO {

    @NotBlank(message = "El RUT del cliente es obligatorio (ej: 12345678-9)")
    private String runCliente;

    @NotBlank(message = "El primer nombre es obligatorio")
    private String pnombreCliente;

    private String snombreCliente;

    @NotBlank(message = "El apellido paterno es obligatorio")
    private String appaternoCliente;

    @NotBlank(message = "El apellido materno es obligatorio")
    private String apmaternoCliente;

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate fechaNacimientoCliente;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe tener un formato válido")
    private String emailCliente;

    @NotBlank(message = "La contraseña es obligatoria")
    private String contrasenaCliente;

    @Size(min = 9, max = 9, message = "El teléfono debe tener exactamente 9 dígitos")
    private String telefonoCliente;

    @NotNull(message = "La dirección es obligatoria")
    private Long idDireccion;
}