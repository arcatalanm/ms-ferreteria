package ferrefix.ms_usuarios.exception;


//Excepción personalizada para manejar errores de validación de negocio (400 Bad Request).


public class BadRequestException extends RuntimeException {
    
    public BadRequestException(String message) {
        super(message);
    }
    
    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}