package ferrefix.ms_arriendo.config;

import ferrefix.ms_arriendo.model.MaquinaArriendo;
import ferrefix.ms_arriendo.repository.MaquinaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("dev")
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final MaquinaRepository maquinaRepository;

    @Override
    public void run(String... args) throws Exception {
        log.info("Cargando datos de prueba para ms-arriendo...");

        if (maquinaRepository.count() == 0) {
            // 1. Insertar datos iniciales fijos
            maquinaRepository.save(MaquinaArriendo.builder()
                    .codigoInterno("BET-001")
                    .nombreMaquina("Betonera 130 Lts")
                    .precioPorDia(15000)
                    .estado("DISPONIBLE")
                    .build());

            maquinaRepository.save(MaquinaArriendo.builder()
                    .codigoInterno("ROT-001")
                    .nombreMaquina("Rotomartillo Bosch")
                    .precioPorDia(8000)
                    .estado("DISPONIBLE")
                    .build());

            maquinaRepository.save(MaquinaArriendo.builder()
                    .codigoInterno("GEN-001")
                    .nombreMaquina("Generador 2500W")
                    .precioPorDia(25000)
                    .estado("DISPONIBLE")
                    .build());

            // 2. Generar datos adicionales con DataFaker
            Faker faker = new Faker();
            for (int i = 1; i <= 5; i++) {
                String codigo = "MAQ-" + faker.number().digits(3);
                String nombre = faker.commerce().productName();
                int precio = faker.number().numberBetween(5000, 30000);
                
                maquinaRepository.save(MaquinaArriendo.builder()
                        .codigoInterno(codigo)
                        .nombreMaquina(nombre)
                        .precioPorDia(precio)
                        .estado("DISPONIBLE")
                        .build());
            }

            log.info("Datos de prueba cargados en ms-arriendo. Total máquinas: {}", maquinaRepository.count());
        } else {
            log.info("La base de datos de arriendo ya contiene datos. Omitiendo.");
        }
    }
}
