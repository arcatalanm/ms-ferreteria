package ferrefix.ms_inventario.dto;

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

public class CategoriaProductoRequestDTO {
    
    @NotBlank(message = "El nombre de la categoría del producto es obligatorio")
    private String nombreCategoria;
}
