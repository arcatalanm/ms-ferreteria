package ferrefix.ms_direcciones.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ferrefix.ms_direcciones.model.Direccion;

@Repository
public interface DireccionRepository extends JpaRepository<Direccion, Long> {
    // JpaRepository ya de por si nos da todo lo necesario
}