package ferrefix.ms_marcas.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MarcaResponseDTO {
    private Integer idMarca;
    private String nombreMarca;
}
