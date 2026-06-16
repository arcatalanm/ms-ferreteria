package ferrefix.ms_compras.service;

import feign.FeignException;
import ferrefix.ms_compras.client.InventarioClient;
import ferrefix.ms_compras.client.ProveedorClient;
import ferrefix.ms_compras.dto.CompraRequestDTO;
import ferrefix.ms_compras.dto.CompraResponseDTO;
import ferrefix.ms_compras.dto.StockIncrementDTO;
import ferrefix.ms_compras.exception.BadRequestException;
import ferrefix.ms_compras.exception.ResourceNotFoundException;
import ferrefix.ms_compras.mapper.CompraMapper;
import ferrefix.ms_compras.model.Compra;
import ferrefix.ms_compras.repository.CompraRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompraService {

    private final CompraRepository compraRepository;
    private final CompraMapper compraMapper;
    private final ProveedorClient proveedorClient;
    private final InventarioClient inventarioClient;

    public List<CompraResponseDTO> listarTodas() {
        return compraRepository.findAll().stream()
                .map(compraMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public CompraResponseDTO obtenerPorId(Long id) {
        return compraRepository.findById(id)
                .map(compraMapper::toResponseDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Orden de compra no encontrada con ID: " + id));
    }

    @Transactional
    public CompraResponseDTO crearOrdenCompra(CompraRequestDTO request) {
        log.info("Iniciando creación de orden de compra para proveedor ID: {}", request.getIdProveedor());

        try {
            Boolean existe = proveedorClient.existeProveedor(request.getIdProveedor());
            if (existe == null || !existe) {
                log.error("Proveedor con ID {} no existe.", request.getIdProveedor());
                throw new BadRequestException("El proveedor con ID " + request.getIdProveedor() + " no existe.");
            }
        } catch (FeignException e) {
            log.error("Error al validar proveedor vía Feign: {}", e.getMessage());
            throw new BadRequestException("Error de comunicación con el servicio de proveedores.");
        }

        Compra compra = compraMapper.toEntity(request);
        Compra guardada = compraRepository.save(compra);
        log.info("Orden de compra creada exitosamente con ID: {}", guardada.getIdCompra());

        return compraMapper.toResponseDTO(guardada);
    }

    @Transactional
    public CompraResponseDTO procesarRecepcionMercancia(Long idCompra) {
        log.info("Iniciando recepción de mercancía para orden ID: {}", idCompra);

        Compra compra = compraRepository.findById(idCompra)
                .orElseThrow(() -> new ResourceNotFoundException("Orden de compra no encontrada con ID: " + idCompra));

        if (!"SOLICITADO".equals(compra.getEstado())) {
            log.warn("Intento de procesar recepción para orden en estado: {}", compra.getEstado());
            throw new BadRequestException("Solo se pueden recibir órdenes en estado SOLICITADO.");
        }

        compra.setEstado("RECIBIDO");
        compraRepository.save(compra);

        try {
            log.info("Incrementando stock para los productos de la orden ID: {}", idCompra);
            compra.getDetalles().forEach(detalle -> {
                StockIncrementDTO dto = StockIncrementDTO.builder()
                        .idProducto(detalle.getIdProducto())
                        .cantidadASumar(detalle.getCantidad())
                        .build();
                inventarioClient.incrementarStock(dto);
            });
        } catch (FeignException e) {
            log.error("Error al incrementar stock vía Feign: {}. Aplicando Rollback.", e.getMessage());
            throw new BadRequestException("Falla en la comunicación con el inventario. La recepción no pudo ser procesada.");
        }

        log.info("Recepción de mercancía completada con éxito para orden ID: {}", idCompra);
        return compraMapper.toResponseDTO(compra);
    }
}
