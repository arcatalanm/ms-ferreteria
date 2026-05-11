package ferrefix.ms_usuarios.exception;

/**
 * Excepción personalizada para manejar recursos no encontrados (404 Not Found).
 * Se lanza cuando un ID no existe en la base de datos.
 */
public class ResourceNotFoundException extends RuntimeException {
    
    public ResourceNotFoundException(String message) {
        super(message);
    }
    
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
