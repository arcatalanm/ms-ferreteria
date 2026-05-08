package ferrefix.ms_proveedores.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ferrefix.ms_proveedores.model.Proveedor;
import java.util.Optional;

@Repository
public interface ProveedorRepository extends JpaRepository<Proveedor, Integer>{
    
    // Bsuquedas personalizadas que de verdad usaremos por el RUT
    Optional<Proveedor> findByRutProveedor(Integer rutProveedor);

    void deleteByRutProveedor(Integer rutProveedor);
}