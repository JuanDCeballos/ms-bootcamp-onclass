package co.onclass.usecase.bootcamp;

import co.onclass.enums.SortDirection;
import co.onclass.exceptions.BusinessException;
import co.onclass.model.bootcamp.Bootcamp;
import co.onclass.model.bootcamp.BootcampDetallado;
import co.onclass.model.bootcamp.gateways.BootcampRepository;
import co.onclass.model.capacidad.CapacidadDetallada;
import co.onclass.model.capacidad.ConteoCapacidadPaginada;
import co.onclass.model.capacidad.gateways.CapacidadGateway;
import co.onclass.model.paging.PageableQuery;
import co.onclass.model.paging.Pagina;
import co.onclass.model.tecnologia.TecnologiaInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private PageableQuery query;
    private ConteoCapacidadPaginada conteoCapacidadPaginada;
    private Map<Long, List<CapacidadDetallada>> capacidadDetalladaMap;
    private List<CapacidadDetallada> capacidadDetalladaList;

    private final Long idBootcamp = 1L;
    private List<Long> capacidadesIds = List.of(1L, 2L, 3L);

    @BeforeEach
    void initMocks() {
        bootcamp = new Bootcamp();
        bootcamp.setId(1L);
        bootcamp.setNombre("Bootcamp Uno");
        bootcamp.setDescripcion("Bootcamp de desarrollo");
        bootcamp.setFechaLanzamiento(LocalDate.of(2025, 1, 1));
        bootcamp.setDuracion(2);

        query = new PageableQuery();
        query.setPage(0);
        query.setSize(2);
        query.setSortBy("capacityCount");
        query.setDirection(SortDirection.ASC);

        conteoCapacidadPaginada = new ConteoCapacidadPaginada();
        conteoCapacidadPaginada.setTotalElementos(2);
        conteoCapacidadPaginada.setIds(capacidadesIds);

        List<TecnologiaInfo> tecnologiaInfoList = new ArrayList<>();
        TecnologiaInfo tecnologiaInfo = new TecnologiaInfo();
        tecnologiaInfo.setId(1L);
        tecnologiaInfo.setNombre("Java");
        tecnologiaInfoList.add(tecnologiaInfo);

        capacidadDetalladaList = new ArrayList<>();
        CapacidadDetallada capacidadDetallada = new CapacidadDetallada();
        capacidadDetallada.setId(1L);
        capacidadDetallada.setNombre("Bootcamp Uno");
        capacidadDetallada.setTecnologias(tecnologiaInfoList);
        capacidadDetalladaList.add(capacidadDetallada);

        capacidadDetalladaMap = new HashMap<>();
        capacidadDetalladaMap.put(1L, capacidadDetalladaList);
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

    @Test
    void listarBootcampsPaginadosListarPorCantidadCapacidades() {
        when(capacidadGateway
                .getBootcampsIdsOrdenadasPorConteo(anyInt(), anyInt(), any(SortDirection.class)))
                .thenReturn(Mono.just(conteoCapacidadPaginada));
        when(bootcampRepository.buscarTodasPorIdEnOrden(anyList())).thenReturn(Flux.just(bootcamp));
        when(capacidadGateway
                .getCapacidadesTecnologiasPorBootcamp(anyList())).thenReturn(Mono.just(capacidadDetalladaMap));

        Mono<Pagina<BootcampDetallado>> respuesta = bootcampUseCase.listarBootcampsPaginados(query);

        StepVerifier.create(respuesta)
                .assertNext(res -> {
                    assertNotNull(res);
                    assertEquals(0, res.getPaginaActual());
                    assertEquals(2, res.getTamanoPagina());
                    assertEquals(2L, res.getTotalElementos());
                })
                .verifyComplete();

        verify(capacidadGateway, times(1))
                .getBootcampsIdsOrdenadasPorConteo(anyInt(), anyInt(), any(SortDirection.class));
        verify(bootcampRepository, times(1)).buscarTodasPorIdEnOrden(anyList());
        verify(capacidadGateway, times(1)).getCapacidadesTecnologiasPorBootcamp(anyList());
    }

    @Test
    void listarBootcampsPaginadosListarPorCantidadCapacidadesRetornaVacio() {
        conteoCapacidadPaginada.setIds(List.of());

        when(capacidadGateway
                .getBootcampsIdsOrdenadasPorConteo(anyInt(), anyInt(), any(SortDirection.class)))
                .thenReturn(Mono.just(conteoCapacidadPaginada));

        Mono<Pagina<BootcampDetallado>> respuesta = bootcampUseCase.listarBootcampsPaginados(query);

        StepVerifier.create(respuesta)
                .assertNext(res -> {
                    assertNotNull(res);
                    assertEquals(0, res.getPaginaActual());
                    assertEquals(2, res.getTamanoPagina());
                    assertEquals(2L, res.getTotalElementos());
                })
                .verifyComplete();

        verify(capacidadGateway, times(1))
                .getBootcampsIdsOrdenadasPorConteo(anyInt(), anyInt(), any(SortDirection.class));
        verify(bootcampRepository, times(0)).buscarTodasPorIdEnOrden(anyList());
        verify(capacidadGateway, times(0)).getCapacidadesTecnologiasPorBootcamp(anyList());
    }

    @Test
    void listarBootcampsPaginadosListarPorNombre() {
        query.setSortBy("nombre");

        when(bootcampRepository.contarTodos()).thenReturn(Mono.just(3L));
        when(bootcampRepository.buscarTodosPaginados(any(PageableQuery.class))).thenReturn(Flux.just(bootcamp));
        when(capacidadGateway
                .getCapacidadesTecnologiasPorBootcamp(anyList())).thenReturn(Mono.just(capacidadDetalladaMap));

        Mono<Pagina<BootcampDetallado>> respuesta = bootcampUseCase.listarBootcampsPaginados(query);

        StepVerifier.create(respuesta)
                .assertNext(res -> {
                    assertNotNull(res);
                    assertEquals(0, res.getPaginaActual());
                    assertEquals(2, res.getTamanoPagina());
                    assertEquals(3L, res.getTotalElementos());
                })
                .verifyComplete();

        verify(bootcampRepository, times(1)).contarTodos();
        verify(bootcampRepository, times(1)).buscarTodosPaginados(any(PageableQuery.class));
        verify(capacidadGateway, times(1)).getCapacidadesTecnologiasPorBootcamp(anyList());
    }

    @Test
    void listarBootcampsPaginadosListarPorNombreRetornaVacijo() {
        query.setSortBy("nombre");

        when(bootcampRepository.contarTodos()).thenReturn(Mono.just(3L));
        when(bootcampRepository.buscarTodosPaginados(any(PageableQuery.class))).thenReturn(Flux.empty());

        Mono<Pagina<BootcampDetallado>> respuesta = bootcampUseCase.listarBootcampsPaginados(query);

        StepVerifier.create(respuesta)
                .assertNext(Assertions::assertNotNull)
                .verifyComplete();

        verify(bootcampRepository, times(1)).contarTodos();
        verify(bootcampRepository, times(1)).buscarTodosPaginados(any(PageableQuery.class));
        verify(capacidadGateway, times(0)).getCapacidadesTecnologiasPorBootcamp(anyList());
    }
}
