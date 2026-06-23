package ferrefix.ms_marcas.config;

import ferrefix.ms_marcas.model.Marca;
import ferrefix.ms_marcas.repository.MarcaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@Profile("dev")
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final MarcaRepository marcaRepository;

    @Override
    public void run(String... args) throws Exception {
        log.info("Cargando datos de prueba para ms-marcas...");

        if (marcaRepository.count() == 0) {
            // 1. Insertar marcas fijas iniciales
            List<String> marcasFijas = List.of(
                    "Bosch", "Stanley", "Makita", "DeWalt", "Black+Decker",
                    "Milwaukee", "Hilti", "Truper", "Irwin", "Einhell"
            );

            for (String nombre : marcasFijas) {
                marcaRepository.save(Marca.builder().nombreMarca(nombre).build());
            }

            // 2. Generar con DataFaker
            Faker faker = new Faker();
            for (int i = 1; i <= 5; i++) {
                String randomBrand = faker.company().name();
                marcaRepository.save(Marca.builder().nombreMarca(randomBrand).build());
            }

            log.info("Datos de prueba cargados en ms-marcas. Total marcas: {}", marcaRepository.count());
        } else {
            log.info("La base de datos de marcas ya contiene datos. Omitiendo.");
        }
    }
}
