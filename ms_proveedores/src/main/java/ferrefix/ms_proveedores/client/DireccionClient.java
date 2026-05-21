package ferrefix.ms_proveedores.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import ferrefix.ms_proveedores.dto.DireccionDTO;

@FeignClient(
    name = "ms-direcciones-client",
    url = "${api.gateway.url}",
    path = "/api/direcciones")
    
public interface DireccionClient {
    @GetMapping("/{id}")
    DireccionDTO obtenerDireccionPorId(@PathVariable("id") Long id);
}
