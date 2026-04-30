package ferrefix.ms_ventas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ferrefix.ms_ventas.model.DetalleVenta;

@Repository
public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Integer> {
    // Obtener Detalles de una venta por su ID
    List<DetalleVenta> findVentaByIdVenta(Integer idVenta);
}
