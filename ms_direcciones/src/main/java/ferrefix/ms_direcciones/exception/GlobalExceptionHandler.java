package ferrefix.ms_direcciones.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    //  404 Not Found
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleResourceNotFoundException(
            ResourceNotFoundException ex, HttpServletRequest request) {

        logger.warn("404 - Recurso no encontrado: {} | path: {}", ex.getMessage(), request.getRequestURI());
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI());
    }

    //  400 Bad Request
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiErrorResponse> handleBadRequestException(
            BadRequestException ex, HttpServletRequest request) {

        logger.warn("400 - Bad request: {} | path: {}", ex.getMessage(), request.getRequestURI());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI());
    }

    // 400 Validación de @Valid / @Validated
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        Map<String, String> fieldErrors = new LinkedHashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        }

        String mensaje = "Error de validación en los campos: " + fieldErrors;
        logger.warn("400 - Validación fallida: {} | path: {}", fieldErrors, request.getRequestURI());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, mensaje, request.getRequestURI());
    }

    // 400 JSONtipo incorrecto en el body
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, HttpServletRequest request) {

        logger.warn("400 - Cuerpo de la solicitud ilegible o malformado | path: {}", request.getRequestURI());
        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "El cuerpo de la solicitud es inválido o está malformado. Verifica el JSON enviado.",
                request.getRequestURI()
        );
    }

    //  400 Tipo incorrecto en @PathVariable o @RequestParam
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex, HttpServletRequest request) {

        String mensaje = String.format(
                "El parámetro '%s' recibió el valor '%s', que no es del tipo esperado: %s.",
                ex.getName(),
                ex.getValue(),
                ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "desconocido"
        );
        logger.warn("400 - Tipo de parámetro inválido: {} | path: {}", mensaje, request.getRequestURI());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, mensaje, request.getRequestURI());
    }

    //  500 Internal Server Error
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGlobalException(
            Exception ex, HttpServletRequest request) {

        logger.error("500 - Error interno inesperado | path: {} | causa: {}",
                request.getRequestURI(), ex.getMessage(), ex);
        return buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Ha ocurrido un error interno inesperado. Contacte al administrador.",
                request.getRequestURI()
        );
    }

    // Asistene privado para construir respuestas de error 
    private ResponseEntity<ApiErrorResponse> buildErrorResponse(
            HttpStatus status, String message, String path) {

        ApiErrorResponse errorResponse = ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(path)
                .build();

        return new ResponseEntity<>(errorResponse, status);
    }
}