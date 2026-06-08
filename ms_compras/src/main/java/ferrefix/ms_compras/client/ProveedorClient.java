package ferrefix.ms_compras.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-proveedores", url = "${api.gateway.url}", path = "/api/proveedores")
public interface ProveedorClient {

    @GetMapping("/existe/{id}")
    Boolean existeProveedor(@PathVariable("id") Integer id);
}
