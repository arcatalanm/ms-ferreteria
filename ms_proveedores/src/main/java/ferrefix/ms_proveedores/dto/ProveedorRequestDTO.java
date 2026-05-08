package ferrefix.ms_proveedores.dto;

import jakarta.validation.constraints.*;
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
public class ProveedorRequestDTO {

    @NotNull(message = "El RUT del proveedor es obligatorio")
    @Min(value = 1000000, message = "El RUT debe ser mayor a 1.000.000")
    @Max(value = 99999999, message = "El RUT debe ser menor a 99.999.999")
    private Integer rutProveedor;

    @NotNull(message = "El dígito verificador es obligatorio")
    @Pattern(regexp = "[0-9Kk]", message = "El dígito verificador debe ser un número del 0-9 o 'K'")
    private String dvProveedor;

    @NotBlank(message = "El nombre del proveedor es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String nombreProveedor;

    @NotBlank(message = "El giro del proveedor es obligatorio")
    @Size(min = 2, max = 100, message = "El giro debe tener entre 2 y 100 caracteres")
    private String giroProveedor;

    @NotBlank(message = "La dirección del proveedor es obligatoria")
    @Size(min = 5, max = 200, message = "La dirección debe tener entre 5 y 200 caracteres")
    private String direccionProveedor;

    @NotBlank(message = "El teléfono del proveedor es obligatorio")
    @Pattern(regexp = "\\+?\\d{8,15}") 
    private String telefonoProveedor;

    @NotBlank(message = "El correo del proveedor es obligatorio")
    @Email(message = "El correo debe tener un formato válido")
    private String correoProveedor;
}