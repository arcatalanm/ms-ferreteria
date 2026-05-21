package ferrefix.ms_inventario.dto;

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
    private Integer idCategoria;
    private String nombreCategoria;
}
