package ferrefix.ms_usuarios.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ferrefix.ms_usuarios.model.Empleado;

@Repository
public interface EmpleadoRepository extends JpaRepository<Empleado, Integer> {
    // Metodo Personalizados

    // Metodo que Retorno un cliente por su run y dv
    Empleado findByRunEmpleado(Integer runEmpleado);
    Empleado findByEmailEmpleado(String emailEmpleado);
    boolean existsByRunEmpleado(Integer runEmpleado);
    boolean existsByEmailEmpleado(String emailEmpleado);
}
