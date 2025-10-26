package co.onclass.usecase.bootcamp;

import co.onclass.model.bootcamp.Bootcamp;
import co.onclass.model.bootcamp.gateways.BootcampRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class BootcampUseCase {

    private final BootcampRepository bootcampRepository;

    public Mono<Bootcamp> guardarBootcamp(Bootcamp bootcamp) {
        return bootcampRepository.guardarBootcamp(bootcamp);
    }
}
