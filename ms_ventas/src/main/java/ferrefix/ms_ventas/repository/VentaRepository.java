package ferrefix.ms_ventas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ferrefix.ms_ventas.model.Venta;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {
    // Método personalizado para buscar ventas por runCliente
    List<Venta> findByRunCliente(Integer runCliente);

    // Método personalizado para buscar ventas por runEmpleado
    List<Venta> findByRunEmpleado(Integer runEmpleado);
}
