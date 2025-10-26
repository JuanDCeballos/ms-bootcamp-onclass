package co.onclass.usecase.bootcamp;

import co.onclass.model.bootcamp.Bootcamp;
import co.onclass.model.bootcamp.gateways.BootcampRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BootcampUseCaseTest {

    @InjectMocks
    BootcampUseCase bootcampUseCase;

    @Mock
    BootcampRepository bootcampRepository;

    private Bootcamp bootcamp;

    @BeforeEach
    void initMocks() {
        bootcamp = new Bootcamp();
        bootcamp.setId(1L);
        bootcamp.setNombre("Bootcamp Uno");
        bootcamp.setDescripcion("Bootcamp de desarrollo");
        bootcamp.setFechaLanzamiento(LocalDate.of(2025, 1, 1));
        bootcamp.setDuracion(2);
    }

    @Test
    void guardarBootcamp() {
        when(bootcampRepository.guardarBootcamp(any(Bootcamp.class))).thenReturn(Mono.just(bootcamp));

        Mono<Bootcamp> respuesta = bootcampUseCase.guardarBootcamp(bootcamp);

        StepVerifier.create(respuesta)
                .assertNext(dto -> {
                    assertNotNull(dto);
                    assertEquals(1L, dto.getId());
                    assertEquals("Bootcamp Uno", dto.getNombre());
                })
                .verifyComplete();

        verify(bootcampRepository, times(1)).guardarBootcamp(any(Bootcamp.class));
    }
}
