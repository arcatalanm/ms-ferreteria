package ferrefix.ms_ventas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ferrefix.ms_ventas.model.Venta;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {
    List<Venta> findByRunCliente(Integer runCliente);

    // Método personalizado para buscar ventas por runEmpleado
    List<Venta> findByRunEmpleado(Integer runEmpleado);

    @Query("SELECT v FROM Venta v WHERE " +
           "(:runCliente IS NULL OR v.runCliente = :runCliente) AND " +
           "(v.fechaVenta BETWEEN :fechaInicio AND :fechaFin)")
    List<Venta> findByFilters(
        @Param("runCliente") Integer runCliente,
        @Param("fechaInicio") LocalDateTime fechaInicio,
        @Param("fechaFin") LocalDateTime fechaFin
    );
}
