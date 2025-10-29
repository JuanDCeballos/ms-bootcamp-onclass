package co.onclass.r2dbc;

import co.onclass.enums.SortDirection;
import co.onclass.model.bootcamp.Bootcamp;
import co.onclass.model.bootcamp.gateways.BootcampRepository;
import co.onclass.model.paging.PageableQuery;
import co.onclass.r2dbc.entity.BootcampEntity;
import co.onclass.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
public class BootcampReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        Bootcamp,
        BootcampEntity,
        Long,
        BootcampReactiveRepository
        > implements BootcampRepository {

    private final R2dbcEntityTemplate template;

    public BootcampReactiveRepositoryAdapter(BootcampReactiveRepository repository, ObjectMapper mapper,
                                             R2dbcEntityTemplate template) {
        super(repository, mapper, d -> mapper.map(d, Bootcamp.class));
        this.template = template;
    }

    @Override
    public Mono<Bootcamp> guardarBootcamp(Bootcamp bootcamp) {
        return save(bootcamp);
    }

    @Override
    public Mono<Bootcamp> buscarPorId(Long idBootcamp) {
        return findById(idBootcamp);
    }

    @Override
    public Mono<Long> contarTodos() {
        return repository.countAll();
    }

    @Override
    public Flux<Bootcamp> buscarTodosPaginados(PageableQuery query) {
        Sort.Direction springDirection = query.getDirection() == SortDirection.DESC
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        Pageable springPageable = PageRequest.of(
                query.getPage(),
                query.getSize(),
                Sort.by(springDirection, query.getSortBy())
        );

        Query paginatedQuery = Query.empty().with(springPageable);

        return template.select(BootcampEntity.class)
                .matching(paginatedQuery)
                .all()
                .map(this::toEntity);
    }

    @Override
    public Flux<Bootcamp> buscarTodasPorIdEnOrden(List<Long> ids) {
        return repository.findAllById(ids)
                .map(this::toEntity)
                .collectList()
                .flatMapMany(bootcamps -> {
                    Map<Long, Bootcamp> mapaBootcamps = bootcamps.stream()
                            .collect(Collectors.toMap(Bootcamp::getId, Function.identity()));

                    return Flux.fromIterable(ids)
                            .map(mapaBootcamps::get)
                            .filter(Objects::nonNull);
                });
    }
}
