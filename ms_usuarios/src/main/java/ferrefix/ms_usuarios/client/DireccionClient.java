package ferrefix.ms_usuarios.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import ferrefix.ms_usuarios.dto.DireccionDTO;

@FeignClient(
    name = "ms-direcciones-client", // nombre
    url = "${api.gateway.url}", // Puerto 8085
    path = "/api/direcciones" // ruta deffault
)
public interface DireccionClient {
    @GetMapping("/{id}")
    DireccionDTO obtenerDireccionPorId(@PathVariable("id") Long id);
}
