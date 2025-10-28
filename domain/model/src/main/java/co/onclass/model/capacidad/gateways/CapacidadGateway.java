package co.onclass.model.capacidad.gateways;

import co.onclass.model.capacidad.CapacidadDetallada;
import reactor.core.publisher.Mono;

import java.util.List;

public interface CapacidadGateway {

    Mono<List<CapacidadDetallada>> asignarCapacidades(Long idBootcamp, List<Long> capacidadesIds);
}
