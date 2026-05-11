package ferrefix.ms_usuarios.service;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ferrefix.ms_usuarios.dto.ClienteRequestDTO;
import ferrefix.ms_usuarios.dto.ClienteResponseDTO;
import ferrefix.ms_usuarios.exception.BadRequestException;
import ferrefix.ms_usuarios.exception.ResourceNotFoundException;
import ferrefix.ms_usuarios.mapper.ClienteMapper;
import ferrefix.ms_usuarios.model.Cliente;
import ferrefix.ms_usuarios.repository.ClienteRepository;
import ferrefix.ms_usuarios.util.RutUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ClienteService {

    private static final Logger logger = LoggerFactory.getLogger(ClienteService.class);
    
    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper; // Inyectamos nuestro nuevo traductor

    public Cliente crearCliente(ClienteRequestDTO dto) {
        logger.info("Iniciando creación de cliente con RUN: {}-{}", dto.getRunCliente(), dto.getDvCliente());

        if (clienteRepository.existsById(dto.getRunCliente())) {
            logger.warn("Conflicto al crear: El cliente con RUN {} ya existe", dto.getRunCliente());
            throw new BadRequestException("El cliente con run " + dto.getRunCliente() + " ya existe");
        }

        Character dvChar = dto.getDvCliente().toUpperCase().charAt(0);
        if (!RutUtil.isRutValido(dto.getRunCliente(), dvChar)) {
            logger.warn("Validación fallida: El RUN {}-{} es inválido", dto.getRunCliente(), dvChar);
            throw new BadRequestException("El run del cliente no es válido");
        }

        // DELEGACIÓN: El Mapper construye la entidad
        Cliente cliente = clienteMapper.toEntity(dto, dvChar);
                
        Cliente clienteGuardado = clienteRepository.save(cliente);
        logger.info("Cliente creado exitosamente con RUN: {}", clienteGuardado.getRunCliente());
        return clienteGuardado;
    }

    public Cliente actualizarCliente(Integer runCliente, ClienteRequestDTO dto) {
        logger.info("Iniciando actualización de cliente con RUN: {}", runCliente);

        Cliente clienteExistente = clienteRepository.findById(runCliente)
                .orElseThrow(() -> {
                    logger.warn("Fallo al actualizar: No se encontró un cliente con RUN {}", runCliente);
                    return new ResourceNotFoundException("No se encontró un cliente con run " + runCliente);
                });
        
        if (!runCliente.equals(dto.getRunCliente())) {
            logger.warn("Conflicto de integridad: El RUN URL ({}) no coincide con Body ({})", runCliente, dto.getRunCliente());
            throw new BadRequestException("El run de la ruta debe coincidir con el run del cuerpo de la solicitud");
        }

        Character dvChar = dto.getDvCliente().toUpperCase().charAt(0);
        if (!RutUtil.isRutValido(dto.getRunCliente(), dvChar)) {
            logger.warn("Validación fallida al actualizar: El RUN {}-{} es inválido", dto.getRunCliente(), dvChar);
            throw new BadRequestException("El run del cliente no es válido");
        }

        validarEmailUnico(dto.getEmailCliente(), runCliente);

        // DELEGACIÓN: El Mapper actualiza los campos
        clienteMapper.updateEntity(clienteExistente, dto, dvChar);

        Cliente clienteActualizado = clienteRepository.save(clienteExistente);
        logger.info("Cliente con RUN {} actualizado exitosamente", runCliente);
        return clienteActualizado;
    }

    public List<ClienteResponseDTO> buscarTodosClientes() {
        logger.info("Iniciando búsqueda de todos los clientes");
        return clienteRepository.findAll().stream()
                .map(clienteMapper::toResponseDTO) // Delegación limpia
                .toList();
    }

    public ClienteResponseDTO buscarClientePorRun(Integer runCliente) {
        logger.info("Buscando cliente por RUN: {}", runCliente);
        return clienteRepository.findById(runCliente)
                .map(clienteMapper::toResponseDTO) // Delegación limpia
                .orElseThrow(() -> {
                    logger.warn("Búsqueda fallida: No se encontró cliente con RUN {}", runCliente);
                    return new ResourceNotFoundException("No se encontró un cliente con run " + runCliente);
                });
    }

    public void eliminarClientePorRun(Integer runCliente) {
        logger.info("Iniciando eliminación de cliente con RUN: {}", runCliente);
        if (!clienteRepository.existsById(runCliente)) {
            logger.warn("Fallo al eliminar: No se encontró cliente con RUN {}", runCliente);
            throw new ResourceNotFoundException("No se encontró un cliente con run " + runCliente);
        }
        clienteRepository.deleteAllByRunCliente(runCliente);
        logger.info("Cliente con RUN {} eliminado exitosamente", runCliente);
    }

    /**
     * Refactorización adicional para separar lógica de validación de email
     */
    private void validarEmailUnico(String email, Integer runActual) {
        Cliente clientePorEmail = clienteRepository.findByEmailCliente(email);
        if (clientePorEmail != null && !clientePorEmail.getRunCliente().equals(runActual)) {
            logger.warn("Conflicto: El email {} ya pertenece a otro cliente", email);
            throw new BadRequestException("El email ya está registrado por otro cliente");
        }
    }
}