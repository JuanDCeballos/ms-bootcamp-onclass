package co.onclass.model.capacidad.gateways;

import co.onclass.enums.SortDirection;
import co.onclass.model.capacidad.CapacidadDetallada;
import co.onclass.model.capacidad.ConteoCapacidadPaginada;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

public interface CapacidadGateway {

    Mono<List<CapacidadDetallada>> asignarCapacidades(Long idBootcamp, List<Long> capacidadesIds);

    Mono<Map<Long, List<CapacidadDetallada>>> getCapacidadesTecnologiasPorBootcamp(List<Long> bootcampsIds);

    Mono<ConteoCapacidadPaginada> getBootcampsIdsOrdenadasPorConteo(int page, int size, SortDirection direction);
}
