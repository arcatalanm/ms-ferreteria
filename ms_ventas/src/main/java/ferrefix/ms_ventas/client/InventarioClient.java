package ferrefix.ms_ventas.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ferrefix.ms_ventas.dto.ProductoDTO;

/**
 * Cliente Feign para comunicarse con el microservicio de Inventario (ms_inventario).
 * Actúa como un "puente" para preguntar si un producto existe y obtener su precio actual.
 */
@FeignClient(
            name = "ms-inventario-client",
            url = "${api.gateway.url}",
            path = "/api/inventario")
            
public interface InventarioClient {

    @GetMapping("/productos/{idProducto}")
    ProductoDTO obtenerProductoPorId(@PathVariable("idProducto") Long idProducto);
}