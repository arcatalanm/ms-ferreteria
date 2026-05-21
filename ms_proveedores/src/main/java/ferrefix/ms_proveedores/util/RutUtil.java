package ferrefix.ms_proveedores.util;

/**
 * Utilitario estático para manejo de RUTs chilenos.
 * Responsabilidades:
 *   1. Limpiar formato (puntos, guiones, espacios).
 *   2. Parsear en partes (run numérico + dv).
 *   3. Validar mediante algoritmo Módulo 11.
 *
 * Entrada tolerante: "12.345.678-9", "12345678-9", "12345678K", "12 345 678-k"
 * Salida normalizada: run=12345678, dv='9'
 */
public class RutUtil {

    private RutUtil() {
        throw new IllegalStateException("Clase utilitaria, no instanciar.");
    }

    // ─── API pública ──────────────────────────────────────────────────────────

    /**
     * Limpia el string raw de un RUT quitando puntos, guiones y espacios.
     * Ejemplo: "12.345.678-9" → "123456789"
     */
    public static String limpiar(String rutRaw) {
        if (rutRaw == null) return "";
        return rutRaw.trim().replace(".", "").replace("-", "").replace(" ", "");
    }

    /**
     * Extrae el número (RUN) de un RUT limpio o con formato.
     * Ejemplo: "12.345.678-9" → 12345678
     *
     * @throws IllegalArgumentException si el formato es irreconocible.
     */
    public static Integer extraerRun(String rutRaw) {
        String limpio = limpiar(rutRaw);
        if (limpio.length() < 2) {
            throw new IllegalArgumentException("El RUT ingresado es demasiado corto: " + rutRaw);
        }
        // Todo menos el último char es el RUN
        String runStr = limpio.substring(0, limpio.length() - 1);
        try {
            return Integer.parseInt(runStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El RUT ingresado no tiene un formato numérico válido: " + rutRaw);
        }
    }

    /**
     * Extrae el dígito verificador (DV) de un RUT limpio o con formato.
     * Siempre retorna en mayúscula.
     * Ejemplo: "12.345.678-k" → 'K'
     */
    public static Character extraerDv(String rutRaw) {
        String limpio = limpiar(rutRaw);
        if (limpio.isEmpty()) {
            throw new IllegalArgumentException("El RUT ingresado está vacío.");
        }
        return Character.toUpperCase(limpio.charAt(limpio.length() - 1));
    }

    /**
     * Valida un RUT completo (formato libre) usando el algoritmo Módulo 11.
     * Es el método principal para usar en los Services.
     *
     * @param rutRaw RUT en cualquier formato: "12.345.678-9", "12345678K", etc.
     * @return true si run + dv son consistentes, false en caso contrario.
     */
    public static boolean esValido(String rutRaw) {
        try {
            Integer run = extraerRun(rutRaw);
            Character dv = extraerDv(rutRaw);
            return modulo11(run, dv);
        } catch (IllegalArgumentException e) {
            return false; // Formato irreconocible → inválido
        }
    }

    /**
     * Formatea un run + dv como string canónico: "12345678-9".
     * Útil para mostrarlo en los ResponseDTOs.
     */
    public static String formatear(Integer run, Character dv) {
        return run + "-" + Character.toUpperCase(dv);
    }

    // ─── Algoritmo Módulo 11 (privado) ───────────────────────────────────────

    private static boolean modulo11(Integer run, Character dv) {
        if (run == null || dv == null || run <= 0) return false;

        int rutAux = run;
        int suma = 0;
        int multiplicador = 2;

        while (rutAux > 0) {
            suma += (rutAux % 10) * multiplicador;
            rutAux /= 10;
            if (++multiplicador == 8) multiplicador = 2;
        }

        int digitoEsperado = 11 - (suma % 11);
        char dvEsperado = switch (digitoEsperado) {
            case 11 -> '0';
            case 10 -> 'K';
            default -> (char) (digitoEsperado + '0');
        };

        return Character.toUpperCase(dv) == dvEsperado;
    }
}