package ferrefix.ms_usuarios.service;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import feign.FeignException;
import ferrefix.ms_usuarios.client.DireccionClient;
import ferrefix.ms_usuarios.dto.ClienteRequestDTO;
import ferrefix.ms_usuarios.dto.ClienteResponseDTO;
import ferrefix.ms_usuarios.dto.DireccionDTO;
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
    private final ClienteMapper clienteMapper;
    private final DireccionClient direccionClient;

    public ClienteResponseDTO crearCliente(ClienteRequestDTO dto) {
        logger.info("Iniciando creación de cliente RUT: {}", dto.getRunCliente());

        // 1. Validar formato y dígito verificador (una sola línea, tolerante a formato)
        if (!RutUtil.esValido(dto.getRunCliente())) {
            logger.warn("RUT inválido recibido: {}", dto.getRunCliente());
            throw new BadRequestException("El RUT ingresado no es válido: " + dto.getRunCliente());
        }

        // 2. Extraer partes ya validadas
        Integer run = RutUtil.extraerRun(dto.getRunCliente());
        Character dv = RutUtil.extraerDv(dto.getRunCliente());

        // 3. Verificar duplicado por RUN (la PK real)
        if (clienteRepository.existsById(run)) {
            logger.warn("Conflicto: el cliente RUN {} ya existe", run);
            throw new BadRequestException("El cliente con RUN " + run + " ya existe.");
        }

        validarEmailUnico(dto.getEmailCliente(), null);

        Cliente cliente = clienteMapper.toEntity(dto, run, dv);
        Cliente guardado = clienteRepository.save(cliente);

        logger.info("Cliente RUN {} creado exitosamente", guardado.getRunCliente());
        return mapToDTO(guardado);
    }

    public ClienteResponseDTO actualizarCliente(Integer runCliente, ClienteRequestDTO dto) {
        logger.info("Iniciando actualización de cliente con RUN: {}", runCliente);

        Cliente clienteExistente = clienteRepository.findById(runCliente)
                .orElseThrow(() -> {
                    logger.warn("Fallo al actualizar: No se encontró un cliente con RUN {}", runCliente);
                    return new ResourceNotFoundException("No se encontró un cliente con run " + runCliente);
                });

        // Validar y extraer el RUT del body con la API nueva
        if (!RutUtil.esValido(dto.getRunCliente())) {
            logger.warn("RUT inválido al actualizar: {}", dto.getRunCliente());
            throw new BadRequestException("El RUT ingresado no es válido: " + dto.getRunCliente());
        }

        Integer run = RutUtil.extraerRun(dto.getRunCliente());
        Character dv  = RutUtil.extraerDv(dto.getRunCliente());

        // El RUN del body debe coincidir con el de la URL
        if (!runCliente.equals(run)) {
            logger.warn("Conflicto de integridad: RUN URL ({}) no coincide con body ({})", runCliente, run);
            throw new BadRequestException("El RUT del cuerpo debe corresponder al mismo cliente de la URL.");
        }

        validarEmailUnico(dto.getEmailCliente(), runCliente);

        clienteMapper.updateEntity(clienteExistente, dto, run, dv); // ← firma nueva
        Cliente clienteActualizado = clienteRepository.save(clienteExistente);

        logger.info("Cliente con RUN {} actualizado exitosamente", runCliente);
        return mapToDTO(clienteActualizado);
    }

    public List<ClienteResponseDTO> buscarTodosClientes() {
        logger.info("Iniciando búsqueda de todos los clientes");
        return clienteRepository.findAll().stream()
                .map(this::mapToDTO) // Usamos mapToDTO para que traiga las direcciones
                .toList();
    }

    public ClienteResponseDTO buscarClientePorRun(Integer runCliente) {
        logger.info("Buscando cliente por RUN: {}", runCliente);
        return clienteRepository.findById(runCliente)
                .map(this::mapToDTO) // Usamos mapToDTO para que traiga la dirección
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

    // Método privado para validar la dirección proveniente del ms_direcciones
    private ClienteResponseDTO mapToDTO(Cliente cliente) {
        DireccionDTO direccionAsignada = null;

        try {
            if (cliente.getIdDireccion() != null) {
                direccionAsignada = direccionClient.obtenerDireccionPorId(cliente.getIdDireccion());
            }
        // Excepcion por si no encuentra la direccion por id
        } catch (FeignException.NotFound ex) {
            logger.warn("Direccion ID {} no fue encontrada para el cliente RUN {}", 
                cliente.getIdDireccion(),
                cliente.getRunCliente()
            );
        // Excepcion si no se pudo hacer la conexion (Error 500 o Gateway caido)
        } catch (FeignException ex) {
            logger.error("Error 500 o de conexión al ms_direcciones: {}", ex.getMessage());
        }

        // ¡IMPORTANTE! Pasamos la entidad Y la dirección encontrada
        return clienteMapper.toResponseDTO(cliente, direccionAsignada);
    }
}