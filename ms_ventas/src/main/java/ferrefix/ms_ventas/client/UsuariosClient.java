package ferrefix.ms_ventas.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import ferrefix.ms_ventas.dto.ClienteInfoDTO;
import ferrefix.ms_ventas.dto.EmpleadoInfoDTO;

@FeignClient(name = "ms-usuarios-client", url = "${api.gateway.url}", path = "/api/usuarios")
public interface UsuariosClient {

    @GetMapping("/clientes/run/{runCliente}")
    ClienteInfoDTO obtenerClientePorRun(@PathVariable Integer runCliente);

    @GetMapping("/empleados/run/{runEmpleado}")
    EmpleadoInfoDTO obtenerEmpleadoPorRun(@PathVariable Integer runEmpleado);
}
