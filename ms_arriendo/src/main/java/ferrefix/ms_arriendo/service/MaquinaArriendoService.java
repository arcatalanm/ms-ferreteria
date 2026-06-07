package ferrefix.ms_arriendo.service;

import feign.FeignException;
import ferrefix.ms_arriendo.client.UsuarioClient;
import ferrefix.ms_arriendo.dto.MaquinaRequestDTO;
import ferrefix.ms_arriendo.dto.MaquinaResponseDTO;
import ferrefix.ms_arriendo.dto.ProcesarArriendoRequestDTO;
import ferrefix.ms_arriendo.exception.BadRequestException;
import ferrefix.ms_arriendo.exception.ResourceNotFoundException;
import ferrefix.ms_arriendo.model.MaquinaArriendo;
import ferrefix.ms_arriendo.repository.MaquinaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MaquinaArriendoService {

    private final MaquinaRepository maquinaRepository;
    private final UsuarioClient usuarioClient;

    @Transactional(readOnly = true)
    public List<MaquinaResponseDTO> listarTodas() {
        return maquinaRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public MaquinaResponseDTO registrar(MaquinaRequestDTO dto) {
        log.info("Registrando nueva máquina: {}", dto.getCodigoInterno());
        MaquinaArriendo maquina = MaquinaArriendo.builder()
                .codigoInterno(dto.getCodigoInterno())
                .nombreMaquina(dto.getNombreMaquina())
                .precioPorDia(dto.getPrecioPorDia())
                .estado("DISPONIBLE")
                .build();
        
        return mapToResponseDTO(maquinaRepository.save(maquina));
    }

    @Transactional
    public MaquinaResponseDTO procesarArriendo(Integer idEquipo, ProcesarArriendoRequestDTO dto) {
        log.info("Iniciando proceso de arriendo para equipo ID: {} y cliente RUN: {}", idEquipo, dto.getRunCliente());

        // 1. Validación Externa (ms-usuarios)
        try {
            usuarioClient.validarCliente(dto.getRunCliente());
            log.info("Validación de cliente exitosa en ms-usuarios para RUN: {}", dto.getRunCliente());
        } catch (FeignException e) {
            log.warn("Error validando cliente en ms-usuarios: {} | Status: {}", e.getMessage(), e.status());
            throw new BadRequestException("No se pudo validar el cliente. Verifique que el RUN sea correcto o que el servicio de usuarios esté disponible.");
        }

        // 2. Validación Local
        MaquinaArriendo maquina = maquinaRepository.findById(idEquipo)
                .orElseThrow(() -> new ResourceNotFoundException("Máquina no encontrada con ID: " + idEquipo));

        if (!"DISPONIBLE".equalsIgnoreCase(maquina.getEstado())) {
            log.warn("Intento de arriendo fallido: La máquina ID {} está en estado {}", idEquipo, maquina.getEstado());
            throw new BadRequestException("La máquina no está disponible para arriendo (Estado actual: " + maquina.getEstado() + ")");
        }

        // 3. Persistencia
        maquina.setEstado("ARRENDADO");
        maquina.setRunCliente(dto.getRunCliente());
        maquina.setFechaDevolucionPactada(LocalDate.now().plusDays(dto.getDiasArriendo()));

        MaquinaArriendo guardada = maquinaRepository.save(maquina);
        log.info("Arriendo procesado con éxito. Fecha pactada: {}", guardada.getFechaDevolucionPactada());

        return mapToResponseDTO(guardada);
    }

    @Transactional
    public MaquinaResponseDTO procesarDevolucion(Integer idEquipo) {
        log.info("Procesando devolución para equipo ID: {}", idEquipo);

        MaquinaArriendo maquina = maquinaRepository.findById(idEquipo)
                .orElseThrow(() -> new ResourceNotFoundException("Máquina no encontrada con ID: " + idEquipo));

        if (maquina.getFechaDevolucionPactada() != null) {
            LocalDate hoy = LocalDate.now();
            if (hoy.isAfter(maquina.getFechaDevolucionPactada())) {
                log.warn("DEVOLUCIÓN CON RETRASO: Equipo ID {} debía devolverse el {}. Hoy es {}", 
                        idEquipo, maquina.getFechaDevolucionPactada(), hoy);
            } else {
                log.info("Devolución a tiempo para equipo ID: {}", idEquipo);
            }
        }

        // Restaurar estado
        maquina.setEstado("DISPONIBLE");
        maquina.setRunCliente(null);
        maquina.setFechaDevolucionPactada(null);

        MaquinaArriendo actualizada = maquinaRepository.save(maquina);
        log.info("Devolución completada con éxito para equipo ID: {}", idEquipo);

        return mapToResponseDTO(actualizada);
    }

    private MaquinaResponseDTO mapToResponseDTO(MaquinaArriendo entity) {
        return MaquinaResponseDTO.builder()
                .idEquipo(entity.getIdEquipo())
                .codigoInterno(entity.getCodigoInterno())
                .nombreMaquina(entity.getNombreMaquina())
                .precioPorDia(entity.getPrecioPorDia())
                .estado(entity.getEstado())
                .runCliente(entity.getRunCliente())
                .fechaDevolucionPactada(entity.getFechaDevolucionPactada())
                .build();
    }
}
