package ferrefix.ms_ventas.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import ferrefix.ms_ventas.dto.DetalleVentaResponseDTO;
import ferrefix.ms_ventas.dto.ProductoDTO;
import ferrefix.ms_ventas.dto.VentaRequestDTO;
import ferrefix.ms_ventas.dto.VentaResponseDTO;
import ferrefix.ms_ventas.model.DetalleVenta;
import ferrefix.ms_ventas.model.TipoPago;
import ferrefix.ms_ventas.model.Venta;
import ferrefix.ms_ventas.repository.DetalleVentaRepository;
import ferrefix.ms_ventas.repository.TipoPagoRepository;
import ferrefix.ms_ventas.repository.VentaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service

// Evitamos problemas en las reglas de negocio por si falla, podemos volver hacer un rollback
@Transactional
// Constructor para inyectar las dependencias de los Repositorios (Los necesarios)
// Ademas Evitamos Saturar la pantalla con constructores y Getters y Setters
@RequiredArgsConstructor
public class VentaService {

    // Llamada de los Repositorios
    private final RestTemplate restTemplate;
    private final VentaRepository ventaRepository;
    private final DetalleVentaRepository detalleVentaRepository;
    private final TipoPagoRepository tipoPagoRepository;

        @Value("${api.base-url}")
        private String baseUrl;
        @Value("${api.cliente.path}")
        private String clientePath;
        @Value("${api.producto.path}")
        private String productoPath;    

    @Transactional
    public VentaResponseDTO guardar(VentaRequestDTO request) {
        
        // Rescatamos con que va a pagar [Metodo de Pago]
        TipoPago tipoPago = tipoPagoRepository.findById(request.getIdTipoPago())
                .orElseThrow(() -> new RuntimeException("Tipo de pago no existe"));

        // Validar cliente al estilo del profe
        String urlCliente = baseUrl + String.format(clientePath, request.getRunCliente());
        Boolean existeCliente = restTemplate.getForObject(urlCliente, Boolean.class);
        
        if (Boolean.FALSE.equals(existeCliente)) {
            throw new RuntimeException("Cliente no existe");
        }

        // Crear la cabecera de la venta
        Venta venta = Venta.builder()
                .fechaVenta(LocalDateTime.now())
                .runCliente(request.getRunCliente())
                .runEmpleado(request.getRunEmpleado())
                .tipoPago(tipoPago)
                .totalVenta(0)
                .build();
        venta = ventaRepository.save(venta);

        int acumuladorTotal = 0;
        List<DetalleVentaResponseDTO> listaDetallesResponse = new ArrayList<>();

        // Validar productos y obtener sus precios
        for (var item : request.getDetalles()) {
            String urlProducto = baseUrl + String.format(productoPath, item.getIdProducto());
            
            // Usamos ProductoDTO.class en vez de Boolean.class porque necesitamos su precio
            ProductoDTO producto = restTemplate.getForObject(urlProducto, ProductoDTO.class);

            if (producto == null) {
                throw new RuntimeException("Producto no existe");
            }

            int subtotal = producto.getPrecioVenta() * item.getCantidad();
            acumuladorTotal += subtotal;

            DetalleVenta detalle = DetalleVenta.builder()
                    .venta(venta)
                    .idProducto(item.getIdProducto())
                    .cantidad(item.getCantidad())
                    .precioUnitario(producto.getPrecioVenta())
                    .build();
            detalleVentaRepository.save(detalle);

            listaDetallesResponse.add(DetalleVentaResponseDTO.builder()
                    .idProducto(item.getIdProducto())
                    .nombreProducto(producto.getNombre())
                    .cantidad(item.getCantidad())
                    .precioUnitario(producto.getPrecioVenta())
                    .subtotal(subtotal)
                    .build());
        }

        // Guardar el total calculado
        venta.setTotalVenta(acumuladorTotal);
        ventaRepository.save(venta);

        return VentaResponseDTO.builder()
                .idVenta(venta.getIdVenta())
                .fechaVenta(venta.getFechaVenta())
                .totalVenta(acumuladorTotal)
                .nombreTipoPago(tipoPago.getNombreTipoPago())
                .detalles(listaDetallesResponse)
                .mensaje("Venta registrada exitosamente")
                .build();
    }

    public List<Venta> listar() {
        return ventaRepository.findAll();
    }
}
