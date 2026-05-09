package ferrefix.ms_ventas.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ferrefix.ms_ventas.dto.ClienteInfoDTO;
import ferrefix.ms_ventas.dto.EmpleadoInfoDTO;

/**
 * Cliente Feign para comunicarse con el microservicio de Usuarios (ms_usuarios).
 * * ¿Cómo funciona con el API Gateway?
 * En lugar de apuntar a "localhost:8082" (puerto del ms_usuarios), apuntamos a la URL
 * del API Gateway usando la variable ${api.gateway.url}. 
 * El Gateway recibe la petición en "/api/usuarios/..." y él se encarga de enrutarla 
 * al microservicio correcto. ¡Esto es arquitectura de red Nivel Senior!
 */
@FeignClient(
    name = "ms-usuarios-client",
    url = "${api.gateway.url}",
    path = "/api/usuarios")
    
public interface UsuariosClient {

    // Hace un GET real a: http://[URL_GATEWAY]/api/usuarios/clientes/run/{runCliente}
    @GetMapping("/clientes/run/{runCliente}")
    ClienteInfoDTO obtenerClientePorRun(@PathVariable("runCliente") Integer runCliente);

    @GetMapping("/empleados/run/{runEmpleado}")
    EmpleadoInfoDTO obtenerEmpleadoPorRun(@PathVariable("runEmpleado") Integer runEmpleado);
}