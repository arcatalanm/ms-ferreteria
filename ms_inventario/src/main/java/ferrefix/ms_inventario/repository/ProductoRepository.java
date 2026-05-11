package ferrefix.ms_inventario.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ferrefix.ms_inventario.model.Producto;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    Optional<Producto> findByCodigoBarrasProducto(String codigoBarrasProducto);
    boolean existsByCodigoBarrasProducto(String codigoBarrasProducto);
}
