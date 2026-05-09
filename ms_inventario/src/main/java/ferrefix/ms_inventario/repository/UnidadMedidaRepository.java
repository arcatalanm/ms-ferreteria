package ferrefix.ms_inventario.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ferrefix.ms_inventario.model.UnidadMedida;

@Repository
public interface UnidadMedidaRepository extends JpaRepository<UnidadMedida, Integer> {
    boolean existsByNombreUnidadMedida(String nombreUnidadMedida);

}
