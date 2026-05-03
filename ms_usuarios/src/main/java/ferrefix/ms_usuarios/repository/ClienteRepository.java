package ferrefix.ms_usuarios.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ferrefix.ms_usuarios.model.Cliente;


@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {
    // Metodo Personalizados

    // Metodo que Retorno un cliente por su run
    Cliente findByRunCliente(Integer runCliente);

    // Metodo que Retorno un cliente por su email
    Cliente findByEmailCliente(String emailCliente);

    // Metodo que elimina un cliente por su runCliente
    void deleteAllByRunCliente(Integer runCliente);
    

}
