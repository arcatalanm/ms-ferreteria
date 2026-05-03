package ferrefix.ms_usuarios.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import ferrefix.ms_usuarios.dto.ClienteRequestDTO;
import ferrefix.ms_usuarios.dto.ClienteResponseDTO;
import ferrefix.ms_usuarios.model.Cliente;
import ferrefix.ms_usuarios.repository.ClienteRepository;
import ferrefix.ms_usuarios.util.RutUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service

@Transactional

@RequiredArgsConstructor
public class ClienteService {
    private final ClienteRepository clienteRepository;
    public RutUtil rutUtil;
    
    // Creacion de cliente con builder para un ClienteDTO
    public Cliente crearCliente(ClienteRequestDTO dto) {
        // Verificar si el cliente ya existe por su runCliente antes de crear uno nuevo
        if (clienteRepository.existsById(dto.getRunCliente())) {
            throw new IllegalArgumentException("El cliente con run " + dto.getRunCliente() + " ya existe");
        }

        if (!rutUtil.isRutValido(dto.getRunCliente(), dto.getDvCliente())) {
            throw new IllegalArgumentException("El run del cliente no es válido");
        }

        // El DTO de cliente tiene atributos similares al original Cliente.
        Cliente cliente = Cliente.builder()
                .runCliente(dto.getRunCliente())
                .dvCliente(dto.getDvCliente())
                .pnombreCliente(dto.getPnombreCliente())
                .snombreCliente(dto.getSnombreCliente()) 
                .appaternoCliente(dto.getAppaternoCliente())
                .apmaternoCliente(dto.getApmaternoCliente())
                .fechaNacimientoCliente(dto.getFechaNacimientoCliente())
                .emailCliente(dto.getEmailCliente())
                .contrasenaCliente(dto.getContrasenaCliente())
                .telefonoCliente(dto.getTelefonoCliente())
                // La fecha de registro se asigna automáticamente al momento de crear el cliente
                .fechaRegistroCliente(LocalDate.now()) 
                // Terminamos de construir el DTO con el builder y lo retornamos
                .build();
        return clienteRepository.save(cliente);
    }

    public Cliente actualizarCliente(Integer runCliente, ClienteRequestDTO dto) {
        Cliente clienteExistente = clienteRepository.findByRunCliente(runCliente);
        if (clienteExistente == null) {
            throw new IllegalArgumentException("No se encontró un cliente con run " + runCliente);
        }
        if (!runCliente.equals(dto.getRunCliente())) {
            throw new IllegalArgumentException("El run de la ruta debe coincidir con el run del cuerpo de la solicitud");
        }

        Cliente clientePorEmail = clienteRepository.findByEmailCliente(dto.getEmailCliente());
        if (clientePorEmail != null && !clientePorEmail.getRunCliente().equals(runCliente)) {
            throw new IllegalArgumentException("El email ya está registrado por otro cliente");
        }

        clienteExistente.setPnombreCliente(dto.getPnombreCliente());
        clienteExistente.setSnombreCliente(dto.getSnombreCliente());
        clienteExistente.setAppaternoCliente(dto.getAppaternoCliente());
        clienteExistente.setApmaternoCliente(dto.getApmaternoCliente());
        clienteExistente.setFechaNacimientoCliente(dto.getFechaNacimientoCliente());
        clienteExistente.setEmailCliente(dto.getEmailCliente());
        clienteExistente.setContrasenaCliente(dto.getContrasenaCliente());
        clienteExistente.setTelefonoCliente(dto.getTelefonoCliente());

        return clienteRepository.save(clienteExistente);
    }

    public List<ClienteResponseDTO> buscarTodosClientes() {
        return clienteRepository.findAll().stream()
                .map(this::clienteToDTO)
                .toList();
    }

    private ClienteResponseDTO clienteToDTO(Cliente cliente) {
        // Armar el runCompleto que contendrá el ClienteDTO
        String runCompleto = cliente.getRunCliente() + "-" + cliente.getDvCliente();

        String sNombre = (cliente.getSnombreCliente() != null && !cliente.getSnombreCliente().trim().isEmpty()) 
                 ? cliente.getSnombreCliente() + " " 
                 : "";

        // Armar el nombreCompleto que contendrá el clienteDTO
        String nombreCompleto = cliente.getPnombreCliente() + " " + sNombre + cliente.getAppaternoCliente() + " " + cliente.getApmaternoCliente();

        return ClienteResponseDTO.builder()
                .runClienteCompleto(runCompleto)
                .nombreClienteCompleto(nombreCompleto.trim())
                .emailCliente(cliente.getEmailCliente())
                .telefonoCliente(cliente.getTelefonoCliente())
                // Terminamos de construir el DTO con el builder y lo retornamos
                .build();
    

    }

    // Metodo que devueve un clienteDTO a partir de un runCliente, si no se encuentra el cliente se lanza una excepcion
    public ClienteResponseDTO buscarClientePorRun(Integer runCliente) {
        Cliente cliente = clienteRepository.findByRunCliente(runCliente);
        if (cliente == null) {
            throw new IllegalArgumentException("No se encontró un cliente con run " + runCliente);
        }
        return clienteToDTO(cliente);
    }

    // Metodo que devueve un clienteDTO a partir de un emailCliente, si no se encuentra el cliente se lanza una excepcion
    public ClienteResponseDTO buscarClienteEmail(String emailCliente) {
        Cliente cliente = clienteRepository.findByEmailCliente(emailCliente);
        if (cliente == null) {
            throw new IllegalArgumentException("No se encontró un cliente con email " + emailCliente);
        }
        return clienteToDTO(cliente);
    }

    // Metodo que elimina un cliente por su runCliente, si no se encuentra el cliente se lanza una excepcion
    public void eliminarClientePorRun(Integer runCliente) {
        // Verificar si el cliente existe antes de intentar eliminarlo
        if (!clienteRepository.existsById(runCliente)) {
            throw new IllegalArgumentException("No se encontró un cliente con run " + runCliente);
        }
        clienteRepository.deleteAllByRunCliente(runCliente);
    }

    
}
