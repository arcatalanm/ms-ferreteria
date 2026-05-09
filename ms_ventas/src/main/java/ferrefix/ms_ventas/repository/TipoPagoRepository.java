package ferrefix.ms_ventas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ferrefix.ms_ventas.model.TipoPago;
import java.util.Optional;

@Repository
public interface TipoPagoRepository extends JpaRepository<TipoPago, Integer> {
    boolean existsByNombreTipoPago(String nombreTipoPago);
    Optional<TipoPago> findByNombreTipoPago(String nombreTipoPago);
}
