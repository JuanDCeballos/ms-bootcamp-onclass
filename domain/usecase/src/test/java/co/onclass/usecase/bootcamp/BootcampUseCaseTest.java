package co.onclass.usecase.bootcamp;

import co.onclass.exceptions.BusinessException;
import co.onclass.model.bootcamp.Bootcamp;
import co.onclass.model.bootcamp.BootcampDetallado;
import co.onclass.model.bootcamp.gateways.BootcampRepository;
import co.onclass.model.capacidad.CapacidadDetallada;
import co.onclass.model.capacidad.gateways.CapacidadGateway;
import co.onclass.model.tecnologia.TecnologiaInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

    @Mock
    CapacidadGateway capacidadGateway;

    private Bootcamp bootcamp;
    private CapacidadDetallada capacidadDetallada;
    private TecnologiaInfo tecnologiaInfo;
    private List<CapacidadDetallada> capacidadDetalladaList;
    private List<TecnologiaInfo> tecnologiaInfoList;

    private Long idBootcamp = 1L;
    private List<Long> capacidadesIds = List.of(1L, 2L, 3L);

    @BeforeEach
    void initMocks() {
        bootcamp = new Bootcamp();
        bootcamp.setId(1L);
        bootcamp.setNombre("Bootcamp Uno");
        bootcamp.setDescripcion("Bootcamp de desarrollo");
        bootcamp.setFechaLanzamiento(LocalDate.of(2025, 1, 1));
        bootcamp.setDuracion(2);

        tecnologiaInfoList = new ArrayList<>();
        tecnologiaInfo = new TecnologiaInfo();
        tecnologiaInfo.setId(1L);
        tecnologiaInfo.setNombre("Java");
        tecnologiaInfoList.add(tecnologiaInfo);

        capacidadDetalladaList = new ArrayList<>();
        capacidadDetallada = new CapacidadDetallada();
        capacidadDetallada.setId(1L);
        capacidadDetallada.setNombre("Bootcamp Uno");
        capacidadDetallada.setTecnologias(tecnologiaInfoList);
        capacidadDetalladaList.add(capacidadDetallada);
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

    @Test
    void asignarCapacidadesBootcamp() {
        when(bootcampRepository.buscarPorId(anyLong())).thenReturn(Mono.just(bootcamp));
        when(capacidadGateway.asignarCapacidades(anyLong(), anyList())).thenReturn(Mono.just(capacidadDetalladaList));

        Mono<BootcampDetallado> respuesta = bootcampUseCase.asignarCapacidadesBootcamp(idBootcamp, capacidadesIds);

        StepVerifier.create(respuesta)
                .assertNext(res -> {
                    assertNotNull(res);
                    assertEquals(1L, res.getId());
                    assertEquals("Bootcamp Uno", res.getNombre());
                    assertEquals("Java", res.getCapacidades().getFirst().getTecnologias().getFirst().getNombre());
                })
                .verifyComplete();

        verify(bootcampRepository, times(1)).buscarPorId(anyLong());
        verify(capacidadGateway, times(1)).asignarCapacidades(anyLong(), anyList());
    }

    @Test
    void asignarCapacidadesBootcampRetornaErrorCapacidadesRepetidas() {
        capacidadesIds = List.of(1L, 1L, 2L, 2L);

        Mono<BootcampDetallado> respuesta = bootcampUseCase.asignarCapacidadesBootcamp(idBootcamp, capacidadesIds);

        StepVerifier.create(respuesta)
                .expectError(BusinessException.class)
                .verify();

        verify(bootcampRepository, times(0)).buscarPorId(anyLong());
        verify(capacidadGateway, times(0)).asignarCapacidades(anyLong(), anyList());
    }

    @Test
    void asignarCapacidadesBootcampRetornaErrorCuandoListaVacia() {
        capacidadesIds = List.of();

        Mono<BootcampDetallado> respuesta = bootcampUseCase.asignarCapacidadesBootcamp(idBootcamp, capacidadesIds);

        StepVerifier.create(respuesta)
                .expectError(BusinessException.class)
                .verify();

        verify(bootcampRepository, times(0)).buscarPorId(anyLong());
        verify(capacidadGateway, times(0)).asignarCapacidades(anyLong(), anyList());
    }

    @Test
    void asignarCapacidadesBootcampRetornaErrorCuandoListaMayorCuatroElementos() {
        capacidadesIds = List.of(1L, 2L, 3L, 4L, 5L);

        Mono<BootcampDetallado> respuesta = bootcampUseCase.asignarCapacidadesBootcamp(idBootcamp, capacidadesIds);

        StepVerifier.create(respuesta)
                .expectError(BusinessException.class)
                .verify();

        verify(bootcampRepository, times(0)).buscarPorId(anyLong());
        verify(capacidadGateway, times(0)).asignarCapacidades(anyLong(), anyList());
    }
}
