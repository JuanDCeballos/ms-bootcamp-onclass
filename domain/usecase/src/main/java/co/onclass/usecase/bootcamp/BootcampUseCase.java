package co.onclass.usecase.bootcamp;

import co.onclass.enums.ExceptionMessages;
import co.onclass.exceptions.BusinessException;
import co.onclass.model.bootcamp.Bootcamp;
import co.onclass.model.bootcamp.BootcampDetallado;
import co.onclass.model.bootcamp.gateways.BootcampRepository;
import co.onclass.model.capacidad.CapacidadDetallada;
import co.onclass.model.capacidad.gateways.CapacidadGateway;
import co.onclass.model.paging.PageableQuery;
import co.onclass.model.paging.Pagina;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class BootcampUseCase {

    private final BootcampRepository bootcampRepository;
    private final CapacidadGateway capacidadGateway;

    public Mono<Bootcamp> guardarBootcamp(Bootcamp bootcamp) {
        return bootcampRepository.guardarBootcamp(bootcamp);
    }

    public Mono<BootcampDetallado> asignarCapacidadesBootcamp(Long idBootcamp, List<Long> capacidadesIds) {
        Mono<Set<Long>> capacidadesValidadas = validarListaTecnologias(capacidadesIds);

        return capacidadesValidadas.flatMap(capacidadesSet -> {
            Mono<Bootcamp> bootcampMono = bootcampRepository.buscarPorId(idBootcamp)
                    .switchIfEmpty(Mono.error(new BusinessException(ExceptionMessages.BOOTCAMP_NO_ENCONTRADO)));

            Mono<List<CapacidadDetallada>> capacidadesMono =
                    capacidadGateway.asignarCapacidades(idBootcamp, capacidadesIds);

            return Mono.zip(bootcampMono, capacidadesMono)
                    .map(tuple -> {
                        Bootcamp bootcamp = tuple.getT1();
                        List<CapacidadDetallada> capacidades = tuple.getT2();

                        return BootcampDetallado.builder()
                                .id(bootcamp.getId())
                                .nombre(bootcamp.getNombre())
                                .descripcion(bootcamp.getDescripcion())
                                .fechaLanzamiento(bootcamp.getFechaLanzamiento())
                                .duracion(bootcamp.getDuracion())
                                .capacidades(capacidades)
                                .build();
                    });
        });
    }

    public Mono<Pagina<BootcampDetallado>> listarBootcampsPaginados(PageableQuery query) {
        if ("capacityCount".equalsIgnoreCase(query.getSortBy())) {
            return listarBootcampsPorCantidadCapacidades(query);
        } else {
            return listarBootcampsPorNombre(query);
        }
    }

    private Mono<Pagina<BootcampDetallado>> listarBootcampsPorCantidadCapacidades(PageableQuery query) {
        return capacidadGateway.getBootcampsIdsOrdenadasPorConteo(
                        query.getPage(), query.getSize(), query.getDirection()
                )
                .flatMap(res -> {
                    long totalElementos = res.getTotalElementos();
                    List<Long> bootcampsIds = res.getIds();

                    if (bootcampsIds.isEmpty()) {
                        return Mono.just(crearPaginaVacia(query.getPage(), query.getSize(), totalElementos));
                    }

                    Mono<List<Bootcamp>> bootcampsMono = bootcampRepository.buscarTodasPorIdEnOrden(bootcampsIds)
                            .collectList();

                    Mono<Map<Long, List<CapacidadDetallada>>> capacidadesMono =
                            capacidadGateway.getCapacidadesTecnologiasPorBootcamp(bootcampsIds)
                                    .defaultIfEmpty(Collections.emptyMap());

                    return Mono.zip(bootcampsMono, capacidadesMono)
                            .map(tuple -> {
                                List<Bootcamp> bootcamps = tuple.getT1();
                                Map<Long, List<CapacidadDetallada>> mapCapacidadTecnologia = tuple.getT2();

                                Map<Long, Bootcamp> mapaBootcamps = bootcamps.stream()
                                        .collect(Collectors.toMap(Bootcamp::getId, Function.identity()));

                                List<BootcampDetallado> contenido = bootcampsIds.stream()
                                        .map(id -> {
                                            Bootcamp boot = mapaBootcamps.get(id);
                                            List<CapacidadDetallada> capacidadDetalladaList =
                                                    mapCapacidadTecnologia.getOrDefault(id, Collections.emptyList());

                                            return ensamblarDto(boot, capacidadDetalladaList);
                                        })
                                        .filter(Objects::nonNull)
                                        .toList();

                                return crearPagina(query.getPage(), query.getSize(), totalElementos, contenido);
                            });
                });
    }

    private Mono<Pagina<BootcampDetallado>> listarBootcampsPorNombre(PageableQuery query) {
        Mono<Long> totalElementosMono = bootcampRepository.contarTodos();
        Flux<Bootcamp> bootcampFlux = bootcampRepository.buscarTodosPaginados(query);

        return Mono.zip(totalElementosMono, bootcampFlux.collectList())
                .flatMap(tuple -> {
                    long totalElementos = tuple.getT1();
                    List<Bootcamp> bootcamps = tuple.getT2();

                    if (bootcamps.isEmpty()) {
                        return Mono.just(crearPaginaVacia(query.getPage(), query.getSize(), totalElementos));
                    }

                    List<Long> bootcampsIds = bootcamps.stream().map(Bootcamp::getId).toList();

                    return capacidadGateway.getCapacidadesTecnologiasPorBootcamp(bootcampsIds)
                            .defaultIfEmpty(Collections.emptyMap())
                            .map(mapCapacidadesTecnologias -> {
                                List<BootcampDetallado> contenido = bootcamps.stream()
                                        .map(bootcamp -> ensamblarDto(
                                                bootcamp,
                                                mapCapacidadesTecnologias.getOrDefault(bootcamp.getId(), Collections.emptyList())
                                        )).toList();

                                return crearPagina(query.getPage(), query.getSize(), totalElementos, contenido);
                            });
                });
    }

    private Mono<Set<Long>> validarListaTecnologias(List<Long> capacidades) {
        Set<Long> capacidadesIds = new HashSet<>(capacidades);

        if (capacidadesIds.size() != capacidades.size()) {
            return Mono.error(new BusinessException(ExceptionMessages.BOOTCAMP_CON_CAPACIDADES_DUPLICADAS));
        }

        if (capacidadesIds.isEmpty() || capacidades.size() > 4) {
            return Mono.error(new BusinessException(ExceptionMessages.MONTO_CAPACIDADES_A_ASIGNAR));
        }

        return Mono.just(capacidadesIds);
    }

    private BootcampDetallado ensamblarDto(Bootcamp bootcamp, List<CapacidadDetallada> capacidadDetalladas) {
        if (bootcamp == null) return null;
        return BootcampDetallado.builder()
                .id(bootcamp.getId())
                .nombre(bootcamp.getNombre())
                .descripcion(bootcamp.getDescripcion())
                .fechaLanzamiento(bootcamp.getFechaLanzamiento())
                .duracion(bootcamp.getDuracion())
                .capacidades(capacidadDetalladas)
                .build();
    }

    private Pagina<BootcampDetallado> crearPagina(
            int page, int size, long totalElementos, List<BootcampDetallado> contenido) {
        long totalPaginas = (size <= 0) ? 1 : (long) Math.ceil((double) totalElementos / (double) size);
        return Pagina.<BootcampDetallado>builder()
                .paginaActual(page)
                .tamanoPagina(size)
                .totalElementos(totalElementos)
                .totalPaginas((int) totalPaginas)
                .contenido(contenido)
                .build();
    }

    private Pagina<BootcampDetallado> crearPaginaVacia(int page, int size, long totalElementos) {
        return crearPagina(page, size, totalElementos, Collections.emptyList());
    }
}
