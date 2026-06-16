package ferrefix.ms_reportes.client;

import ferrefix.ms_reportes.dto.VentaDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.hateoas.CollectionModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Collection;

@FeignClient(name = "ms-ventas", url = "${api.gateway.url}", path = "/api/ventas")
public interface VentaClient {

    @GetMapping("/run/{runCliente}")
    CollectionModel<VentaDTO> obtenerVentasPorRun(@PathVariable("runCliente") String runCliente);
}
