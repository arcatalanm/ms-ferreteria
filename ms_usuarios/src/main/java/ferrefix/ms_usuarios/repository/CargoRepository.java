package ferrefix.ms_usuarios.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ferrefix.ms_usuarios.model.Cargo;

@Repository
public interface CargoRepository extends JpaRepository<Cargo, Long> {

}
