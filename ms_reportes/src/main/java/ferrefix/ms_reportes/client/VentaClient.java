package ferrefix.ms_reportes.client;

import ferrefix.ms_reportes.dto.VentaDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.hateoas.CollectionModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "ms-ventas", url = "${api.gateway.url}", path = "/api/ventas")
public interface VentaClient {

    @GetMapping("/run/{runCliente}")
    CollectionModel<VentaDTO> obtenerVentasPorRun(@PathVariable("runCliente") String runCliente);

    @GetMapping("/buscar")
    CollectionModel<VentaDTO> buscarVentas(
            @RequestParam(name = "runCliente", required = false) String runCliente,
            @RequestParam(name = "fechaInicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(name = "fechaFin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin
    );
}
