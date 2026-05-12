package ferrefix.ms_proveedores.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

@Entity
@Table(name = "proveedor")
public class Proveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_proveedor")
    private Integer idProveedor;

    @Min(value = 1)
    @Max(value = 99999999)
    @NotNull(message = "El rut del proveedor no puede ser nulo")
    @Column(name = "rut_proveedor", unique = true, nullable = false)
    private Integer rutProveedor;

    @NotNull(message = "El dv del proveedor no puede ser nulo")
    @Column(name = "dv_proveedor", length = 1, nullable = false)
    private Character dvProveedor;

    @NotBlank(message = "El nombre del proveedor no puede estar vacío")
    @Column(name = "nombre_proveedor", length = 50, nullable = false)
    private String nombreProveedor;

    @NotBlank(message = "El giro no puede estar vacío")
    @Column(name = "giro_proveedor", length = 50, nullable = false)
    private String giroProveedor;

    @NotBlank(message = "La dirección no puede estar vacío")
    @Column(name = "direccion_proveedor", length = 100, nullable = false)
    private String direccionProveedor;

    @NotBlank(message = "El telefono no puede estar vacío")
    @Column(name = "telefono_proveedor", length = 15)
    private String telefonoProveedor;

    @Email
    @NotBlank(message = "El correo no puede estar vacío")
    @Column(name = "correo_proveedor", length = 50)
    private String correoProveedor;

}
