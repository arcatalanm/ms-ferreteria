package ferrefix.ms_usuarios.dto;

import jakarta.validation.constraints.NotBlank;
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
public class CargoRequestDTO {

    //Enviamos solamente el nombre del cargo, el id se genera automáticamente en la base de datos
    @NotBlank(message = "El nombre del cargo no puede estar vacío")
    private String nombreCargo;
}
