package co.onclass.model.bootcamp.gateways;

import co.onclass.model.bootcamp.Bootcamp;
import co.onclass.model.paging.PageableQuery;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface BootcampRepository {

    Mono<Bootcamp> guardarBootcamp(Bootcamp bootcamp);

    Mono<Bootcamp> buscarPorId(Long idBootcamp);

    Mono<Long> contarTodos();

    Flux<Bootcamp> buscarTodosPaginados(PageableQuery query);

    Flux<Bootcamp> buscarTodasPorIdEnOrden(List<Long> ids);
}
