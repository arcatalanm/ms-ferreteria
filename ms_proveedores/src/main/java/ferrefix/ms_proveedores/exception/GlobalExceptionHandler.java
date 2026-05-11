package ferrefix.ms_proveedores.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.OffsetDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleResourceNotFound(ResourceNotFoundException ex,
                                                                  HttpServletRequest request) {
        logger.warn("Resource not found: {}", ex.getMessage());
        ApiErrorResponse error = buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiErrorResponse> handleBadRequest(BadRequestException ex,
                                                             HttpServletRequest request) {
        logger.warn("Bad request: {}", ex.getMessage());
        ApiErrorResponse error = buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<ApiErrorResponse> handleWebClientException(WebClientResponseException ex,
                                                                     HttpServletRequest request) {
        int statusCode = ex.getStatusCode().value();
        logger.warn("WebClient error: status={}, message={}", statusCode, ex.getMessage());
        ApiErrorResponse error = buildResponse(HttpStatus.valueOf(statusCode), ex.getMessage(), request);
        return ResponseEntity.status(HttpStatus.valueOf(statusCode)).body(error);
    }

    // Error inesperado en el servidor
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGenericException(Exception ex,
                                                                   HttpServletRequest request) {
        logger.error("Error inesperado", ex);
        ApiErrorResponse error = buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Ha ocurrido un error interno.", request);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
    
    private ApiErrorResponse buildResponse(HttpStatus status, String message, HttpServletRequest request) {
        return ApiErrorResponse.builder()
                .timestamp(OffsetDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(request.getRequestURI())
                .build();
    }
}
