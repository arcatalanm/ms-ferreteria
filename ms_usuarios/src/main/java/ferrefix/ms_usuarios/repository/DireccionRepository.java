package ferrefix.ms_usuarios.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ferrefix.ms_usuarios.model.Direccion;

@Repository
public interface DireccionRepository extends JpaRepository<Direccion, Long> {
    Optional<Direccion> findByIdDireccion(Long idDireccion);
    List<Direccion> findByCliente_RunCliente(Integer runCliente);
}
