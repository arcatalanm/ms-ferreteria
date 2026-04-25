package ferrefix.ms_inventorario.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ferrefix.ms_inventorario.model.UnidadMedida;

@Repository
public interface UnidadMedidaRepository extends JpaRepository<UnidadMedida, Byte> {

}
