package ferrefix.ms_arriendo.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
    name = "ms-usuarios",
    url = "${api.gateway.url}",
    path = "/api/usuarios"
)
public interface UsuarioClient {

    @GetMapping("/clientes/{run}")
    void validarCliente(@PathVariable("run") Integer run);
}
