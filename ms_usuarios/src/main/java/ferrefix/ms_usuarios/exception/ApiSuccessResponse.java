package ferrefix.ms_usuarios.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
// Controlador de Respuestas exitosas 
public class ApiSuccessResponse {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    private Integer status;   // Ej: 200

    private String message;   // Ej: "Dirección eliminada correctamente."

    private String path;      // Ej: "/api/direcciones/5"
}
