package ferrefix.ms_usuarios.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
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

    // 400 Lógica de negocio 
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiErrorResponse> handleBadRequest(
            BadRequestException ex, HttpServletRequest request) {

        logger.warn("400 Bad Request: {} | path: {}", ex.getMessage(), request.getRequestURI());
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    // 400 Validación @Valid 
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        Map<String, String> errores = new LinkedHashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            errores.put(fe.getField(), fe.getDefaultMessage());
        }
        String mensaje = "Errores de validación: " + errores;
        logger.warn("400 Validation: {} | path: {}", errores, request.getRequestURI());
        return build(HttpStatus.BAD_REQUEST, mensaje, request);
    }

    // 400 JSON malformado 
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleUnreadable(
            HttpMessageNotReadableException ex, HttpServletRequest request) {

        logger.warn("400 JSON ilegible | path: {}", request.getRequestURI());
        return build(HttpStatus.BAD_REQUEST,
                "El cuerpo de la solicitud es inválido o está malformado.", request);
    }

    //  400 Tipo incorrecto en @PathVariable / @RequestParam
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex, HttpServletRequest request) {

        String tipo = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "desconocido";
        String mensaje = String.format(
                "El parámetro '%s' recibió '%s', se esperaba tipo: %s.",
                ex.getName(), ex.getValue(), tipo);

        logger.warn("400 Type Mismatch: {} | path: {}", mensaje, request.getRequestURI());
        return build(HttpStatus.BAD_REQUEST, mensaje, request);
    }

    //  404 Recurso no encontrado 
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(
            ResourceNotFoundException ex, HttpServletRequest request) {

        logger.warn("404 Not Found: {} | path: {}", ex.getMessage(), request.getRequestURI());
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    // 409 Violación de integridad referencial 
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleDataIntegrity(
            DataIntegrityViolationException ex, HttpServletRequest request) {

        logger.warn("409 Conflict (FK): {} | path: {}", ex.getMessage(), request.getRequestURI());
        return build(HttpStatus.CONFLICT,
                "No se puede completar la operación: el registro está referenciado por otros datos.", request);
    }

    //  500 Error interno como por ejemplo que no se pudo hacer la peticion a otro ms
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGeneric(
            Exception ex, HttpServletRequest request) {

        logger.error("500 Internal Error | path: {} | causa: {}", request.getRequestURI(), ex.getMessage(), ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR,
                "Error interno inesperado. Contacte al administrador.", request);
    }

    // Asistente de Error 
    private ResponseEntity<ApiErrorResponse> build(
            HttpStatus status, String message, HttpServletRequest request) {

        ApiErrorResponse body = ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(request.getRequestURI())
                .build();
        return new ResponseEntity<>(body, status);
    }
}