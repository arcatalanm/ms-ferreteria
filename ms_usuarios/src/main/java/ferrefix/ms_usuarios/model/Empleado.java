package ferrefix.ms_usuarios.model;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "empleado")
public class Empleado {
    @Id
    @NotNull(message = "El run del empleado no puede ser nulo")
    @Min(value = 1, message = "El run debe ser un número positivo")
    @Max(value = 99999999, message = "El run no puede exceder los 8 dígitos")
    @Column(name = "run_empleado", nullable = false, unique = true)
    private Integer runEmpleado;

    @NotNull(message = "El dv del empleado no puede ser nulo")
    @Column(name = "dv_empleado", length = 1, nullable = false)
    private Character dvEmpleado;

    @NotBlank(message = "El primer nombre es obligatorio")
    @Column(name = "pnombre_empleado", length = 50, nullable = false)
    private String pnombreEmpleado;

    @Column(name = "snombre_empleado", length = 50)
    private String snombreEmpleado;

    @NotBlank(message = "El apellido paterno es obligatorio")
    @Column(name = "appaterno_empleado", length = 50, nullable = false)
    private String appaternoEmpleado;

    @NotBlank(message = "El apellido materno es obligatorio")
    @Column(name = "apmaterno_empleado", length = 50, nullable = false)
    private String apmaternoEmpleado;

    @Email(message = "Formato de correo corporativo inválido")
    @NotBlank(message = "El email es obligatorio")
    @Column(name = "email_empleado", nullable = false, unique = true)
    private String emailEmpleado;

    @NotBlank(message = "La contraseña no puede estar vacía")
    @Column(name = "contrasena_empleado", nullable = false)
    private String contrasenaEmpleado;

    // --- Atributos específicos de Empleado  ---

    @NotNull(message = "El sueldo base es obligatorio")
    @Min(value = 0, message = "El sueldo no puede ser negativo")
    @Column(name = "sueldo_base_empleado", nullable = false)
    private Integer sueldoBaseEmpleado; // Usamos Integer por ser CLP (Pesos Chilenos)

    @NotNull(message = "La fecha de contratación es obligatoria")
    @JsonFormat(pattern = "dd-MM-yyyy")
    @Column(name = "fecha_contratacion_empleado", nullable = false)
    private LocalDate fechaContratacionEmpleado;

    @Column(name = "telefono_empleado", length = 9)
    private String telefonoEmpleado;

    @Builder.Default // Para que por defecto el empleado entre como activo
    @Column(name = "activo_empleado", nullable = false)
    private Boolean activoEmpleado = true;

    @ManyToOne
    @JoinColumn(name = "id_cargo_fk", nullable = false)
    private Cargo cargo;
}
