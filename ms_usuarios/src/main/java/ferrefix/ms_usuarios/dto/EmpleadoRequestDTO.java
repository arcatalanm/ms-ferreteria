package ferrefix.ms_usuarios.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
public class EmpleadoRequestDTO {

    @NotNull(message = "El run del empleado no puede ser nulo")
    @Min(value = 1, message = "El run debe ser un número positivo")
    @Max(value = 99999999, message = "El run no puede exceder los 8 dígitos")
    private Integer runEmpleado;

    @NotBlank(message = "El dv del empleado no puede ser nulo o vacío")
    @Pattern(regexp = "^[0-9Kk]$", message = "El dígito verificador debe ser un número del 0-9 o la letra 'K'")
    private String dvEmpleado;

    @NotBlank(message = "El primer nombre es obligatorio")
    private String pnombreEmpleado;

    // No lleva @NotBlank porque un empleado podría no tener segundo nombre
    private String snombreEmpleado;

    @NotBlank(message = "El apellido paterno es obligatorio")
    private String appaternoEmpleado;

    @NotBlank(message = "El apellido materno es obligatorio")
    private String apmaternoEmpleado;

    @Email(message = "Formato de correo corporativo inválido")
    @NotBlank(message = "El email es obligatorio")
    private String emailEmpleado;

    @NotBlank(message = "La contraseña inicial no puede estar vacía")
    private String contrasenaEmpleado;

    @NotNull(message = "El sueldo base es obligatorio")
    @Min(value = 0, message = "El sueldo no puede ser negativo")
    private Integer sueldoBaseEmpleado;

    // Lo pedimos como String para facilitar el envío desde el frontend (Postman)
    @NotNull(message = "La fecha de contratación es obligatoria")
    @JsonFormat(pattern = "dd-MM-yyyy") // dd-MM-yyyy es más común en Chile, pero para APIs REST se suele usar el formato ISO (yyyy-MM-dd)
    private LocalDate fechaContratacionEmpleado; 

    @Size(min = 9, max = 9, message = "El teléfono del empleado debe tener exactamente 9 dígitos")
    @NotBlank(message = "El teléfono del empleado no puede estar vacío")
    private String telefonoEmpleado;

    @NotNull(message = "Debe asignar un cargo válido al empleado")
    private Integer idCargo;
}