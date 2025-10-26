package co.onclass.r2dbc;

import co.onclass.model.bootcamp.Bootcamp;
import co.onclass.model.bootcamp.gateways.BootcampRepository;
import co.onclass.r2dbc.entity.BootcampEntity;
import co.onclass.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class BootcampReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        Bootcamp,
        BootcampEntity,
        Long,
        BootcampReactiveRepository
        > implements BootcampRepository {
    public BootcampReactiveRepositoryAdapter(BootcampReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, Bootcamp.class));
    }

    @Override
    public Mono<Bootcamp> guardarBootcamp(Bootcamp bootcamp) {
        return save(bootcamp);
    }
}
