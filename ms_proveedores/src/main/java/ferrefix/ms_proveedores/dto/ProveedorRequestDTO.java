package ferrefix.ms_proveedores.dto;


import jakarta.validation.constraints.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ProveedorRequestDTO {

    @NotBlank(message = "El RUT del proveedor es obligatorio (ej: 76354771-K)")
    private String rutProveedor;

    @NotBlank(message = "El nombre del proveedor es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String nombreProveedor;

    @NotBlank(message = "El giro del proveedor es obligatorio")
    @Size(min = 2, max = 100, message = "El giro debe tener entre 2 y 100 caracteres")
    private String giroProveedor;

    @NotNull(message = "La dirección del proveedor es obligatoria")
    private Long direccionProveedor;

    @NotBlank(message = "El teléfono del proveedor es obligatorio")
    @Pattern(regexp = "\\+?\\d{8,15}", message = "El teléfono debe tener entre 8 y 15 dígitos")
    private String telefonoProveedor;

    @NotBlank(message = "El correo del proveedor es obligatorio")
    @Email(message = "El correo debe tener un formato válido")
    private String correoProveedor;
}