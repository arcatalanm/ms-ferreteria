package ferrefix.ms_compras.mapper;

import ferrefix.ms_compras.dto.CompraRequestDTO;
import ferrefix.ms_compras.dto.CompraResponseDTO;
import ferrefix.ms_compras.dto.DetalleCompraResponseDTO;
import ferrefix.ms_compras.model.Compra;
import ferrefix.ms_compras.model.DetalleCompra;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CompraMapper {

    public Compra toEntity(CompraRequestDTO request) {
        Compra compra = Compra.builder()
                .idProveedor(request.getIdProveedor())
                .fechaCompra(LocalDateTime.now())
                .estado("SOLICITADO")
                .build();

        List<DetalleCompra> detalles = request.getDetalles().stream()
                .map(d -> DetalleCompra.builder()
                        .compra(compra)
                        .idProducto(d.getIdProducto())
                        .cantidad(d.getCantidad())
                        .precioCompraUnitario(d.getPrecioCompraUnitario())
                        .build())
                .collect(Collectors.toList());

        compra.setDetalles(detalles);

        // Calcular totalCompra
        int total = detalles.stream()
                .mapToInt(d -> d.getCantidad() * d.getPrecioCompraUnitario())
                .sum();
        compra.setTotalCompra(total);

        return compra;
    }

    public CompraResponseDTO toResponseDTO(Compra entity) {
        List<DetalleCompraResponseDTO> detallesDTO = entity.getDetalles().stream()
                .map(d -> DetalleCompraResponseDTO.builder()
                        .idDetalleCompra(d.getIdDetalleCompra())
                        .idProducto(d.getIdProducto())
                        .cantidad(d.getCantidad())
                        .precioCompraUnitario(d.getPrecioCompraUnitario())
                        .build())
                .collect(Collectors.toList());

        int total = entity.getTotalCompra() != null ? entity.getTotalCompra() : 0;
        int neto = (int) Math.round(total / 1.19);
        int iva = total - neto;

        return CompraResponseDTO.builder()
                .idCompra(entity.getIdCompra())
                .idProveedor(entity.getIdProveedor())
                .fechaCompra(entity.getFechaCompra())
                .totalCompra(total)
                .neto(neto)
                .iva(iva)
                .estado(entity.getEstado())
                .detalles(detallesDTO)
                .build();
    }
}
