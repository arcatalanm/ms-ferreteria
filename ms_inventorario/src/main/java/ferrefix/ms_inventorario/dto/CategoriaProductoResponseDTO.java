package ferrefix.ms_inventorario.dto;

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

public class CategoriaProductoResponseDTO {
    // No son necesarias las validades de validación en el DTO de respuesta
    //  ya que se asume que los datos ya han sido validados al ser procesados por el servicio.
    private Integer idCategoria;
    private String nombreCategoria;
}
