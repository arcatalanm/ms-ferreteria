package ferrefix.ms_compras.client;

import ferrefix.ms_compras.dto.StockIncrementDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "ms-inventario", url = "${api.gateway.url}", path = "/api/inventario")
public interface InventarioClient {

    @PutMapping("/productos/incrementar-stock")
    void incrementarStock(@RequestBody StockIncrementDTO dto);
}
