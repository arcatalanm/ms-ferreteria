package ferrefix.ms_usuarios.util;

public class RutUtil {
// Toda la Verificación del RUT se hace con el algoritmo Módulo 11, que es el estándar en Chile para validar RUTs.
// Constructor privado para que nadie pueda instanciar esta clase (buena práctica para utilitarios)
    private RutUtil() {
        throw new IllegalStateException("Clase Utilitaria");
    }

    /**
     * Valida si un RUN y su Dígito Verificador son correctos usando el algoritmo Módulo 11.
     * @param run El número de rut sin puntos ni guion (ej. 25000000)
     * @param dv El dígito verificador (ej. 'K' o '1')
     * @return true si es válido, false si no lo es.
     */
    public static boolean isRutValido(Integer run, Character dv) {
        if (run == null || dv == null || run <= 0) {
            return false;
        }

        int rutAux = run;
        int suma = 0;
        int multiplicador = 2;

        // Iteramos sobre cada dígito del RUN de derecha a izquierda
        while (rutAux > 0) {
            suma += (rutAux % 10) * multiplicador;
            rutAux = rutAux / 10;
            multiplicador++;
            if (multiplicador == 8) {
                multiplicador = 2; // El multiplicador se reinicia al llegar a 8
            }
        }

        // Calculamos el dígito verificador esperado
        int resto = suma % 11;
        int digitoEsperado = 11 - resto;

        char dvEsperado;
        if (digitoEsperado == 11) {
            dvEsperado = '0';
        } else if (digitoEsperado == 10) {
            dvEsperado = 'K';
        } else {
            // Convertimos el número a caracter
            dvEsperado = (char) (digitoEsperado + '0');
        }

        // Comparamos el dv calculado con el ingresado (convirtiéndolos a mayúscula por si envían una 'k' minúscula)
        return Character.toUpperCase(dv) == dvEsperado;
    }
}
