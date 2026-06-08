package ferrefix.ms_sugerencia.repository;

import ferrefix.ms_sugerencia.model.Sugerencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SugerenciaRepository extends JpaRepository<Sugerencia, Long> {
}
