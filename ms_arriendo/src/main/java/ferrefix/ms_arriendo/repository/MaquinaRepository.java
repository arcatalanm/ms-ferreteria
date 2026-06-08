package ferrefix.ms_arriendo.repository;

import ferrefix.ms_arriendo.model.MaquinaArriendo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MaquinaRepository extends JpaRepository<MaquinaArriendo, Integer> {
}
