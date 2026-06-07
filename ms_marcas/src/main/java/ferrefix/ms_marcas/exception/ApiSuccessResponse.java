package ferrefix.ms_marcas.exception;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ApiSuccessResponse<T> {
    private LocalDateTime timestamp;
    private int status;
    private String message;
    private T data;
}
