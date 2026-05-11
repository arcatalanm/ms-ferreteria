package ferrefix.ms_usuarios.model;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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

@Entity
@Table(name = "cliente")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Cliente {
    @Id
    @NotNull(message = "El run del cliente no puede ser nulo")
    @Min(value = 1, message = "El run del cliente debe ser un número positivo")
    @Max(value = 99999999, message = "El run del cliente no puede tener más de 8 dígitos")
    @Column(name = "run_cliente", nullable = false, unique = true)
    private Integer runCliente;

    @NotNull(message = "El dv del cliente no puede ser nulo")
    @Column(name = "dv_cliente", length = 1, nullable = false)
    private Character dvCliente;

    @NotBlank(message = "El primer nombre del cliente no puede estar vacío")
    @Column(name = "pnombre_cliente", length = 50)
    private String pnombreCliente;

    @Column(name = "snombre_cliente", length = 50)
    private String snombreCliente;

    @NotBlank(message = "El apellido paterno del cliente no puede estar vacío")
    @Column(name = "appaterno_cliente", length = 50)
    private String appaternoCliente;

    @NotBlank(message = "El apellido materno del cliente no puede estar vacío")
    @Column(name = "apmaterno_cliente", length = 50)
    private String apmaternoCliente;

    @NotNull(message = "La fecha de nacimiento del cliente no puede ser nula")
    @JsonFormat(pattern = "dd-MM-yyyy")
    @Column(name = "fecha_nacimiento_cliente")
    private LocalDate fechaNacimientoCliente;

    @Email(message = "El email del cliente no tiene un formato válido")
    @NotBlank(message = "El email del cliente no puede estar vacío")
    @Column(name = "email_cliente", nullable = false, unique = true)
    private String emailCliente;

    @NotBlank(message = "La contraseña del cliente no puede estar vacía")
    @Column(name = "contrasena_cliente", nullable = false)
    private String contrasenaCliente;

    @Size(min = 9, max = 9, message = "El teléfono del cliente debe tener exactamente 9 dígitos")
    @Column(name = "telefono_cliente", length = 9)
    private String telefonoCliente;

    @NotNull(message = "La fecha de registro del cliente no puede ser nula")
    @JsonFormat(pattern = "dd-MM-yyyy")
    @Column(name = "fecha_registro_cliente")
    private LocalDate fechaRegistroCliente;

    // --- NUEVO CAMPO: FK Lógica al ms_direcciones ---
    @NotNull(message = "El ID de la dirección no puede ser nulo")
    @Column(name = "id_direccion_fk", nullable = false)
    private Long idDireccion;
}