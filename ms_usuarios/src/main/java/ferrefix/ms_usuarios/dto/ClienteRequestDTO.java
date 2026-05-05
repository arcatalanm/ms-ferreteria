package ferrefix.ms_usuarios.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class ClienteRequestDTO {
    @Min(value = 1, message = "El run del cliente debe ser un número positivo")
    @Max(value = 99999999, message = "El run del cliente no puede tener más de 8 dígitos")
    @NotNull(message = "El run del cliente no puede ser nulo")
    private Integer runCliente;

    @NotNull(message = "El dv del cliente no puede ser nulo")
    private Character dvCliente;

    @NotBlank(message = "El primer nombre del cliente no puede estar vacío")
    private String pnombreCliente;

    private String snombreCliente;

    @NotBlank(message = "El apellido paterno del cliente no puede estar vacío")
    private String appaternoCliente;

    @NotBlank(message = "El apellido materno del cliente no puede estar vacío")
    private String apmaternoCliente;

    @NotNull(message = "La fecha de nacimiento del cliente no puede ser nula")
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate fechaNacimientoCliente;

    @NotBlank(message = "El email del cliente no puede estar vacío")
    @Email(message = "El email del cliente debe ser válido")
    private String emailCliente;
    
    @NotBlank(message = "La contraseña del cliente no puede estar vacía")
    private String contrasenaCliente;
    
    // El telefono es opcional, el usuario puede despues registrarlo, pero si se proporciona, debe tener exactamente 9 dígitos
    @Size(min = 9, max = 9, message = "El teléfono del cliente debe tener exactamente 9 dígitos")
    private String telefonoCliente;

    // La fecha registro es automaticamente asignada en el servicio, por lo que no se incluye en el DTO de solicitud

}
