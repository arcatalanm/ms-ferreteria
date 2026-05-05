package ferrefix.ms_usuarios.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

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
public class EmpleadoResponseDTO {
    private String runEmpleadoCompleto;
    private String nombreEmpleadoCompleto;
    private String emailEmpleado;
    private String telefonoEmpleado;
    private String nombreCargo;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate fechaContratacionEmpleado;
}
