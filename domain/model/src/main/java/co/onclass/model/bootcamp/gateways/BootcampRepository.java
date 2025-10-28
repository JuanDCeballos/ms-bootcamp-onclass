package co.onclass.model.bootcamp.gateways;

import co.onclass.model.bootcamp.Bootcamp;
import reactor.core.publisher.Mono;

public interface BootcampRepository {

    Mono<Bootcamp> guardarBootcamp(Bootcamp bootcamp);

    Mono<Bootcamp> buscarPorId(Long idBootcamp);
}
