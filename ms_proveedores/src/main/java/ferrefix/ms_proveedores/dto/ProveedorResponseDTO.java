package ferrefix.ms_proveedores.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class ProveedorResponseDTO {
    private Integer idProveedor;
    // Rut + Dv
    private String rutProveedor;
    private String nombreProveedor;
    private String giroProveedor;
    private String direccionProveedor;
    private String telefonoProveedor;
    private String correoProveedor;

}