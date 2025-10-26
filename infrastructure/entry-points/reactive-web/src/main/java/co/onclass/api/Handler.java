package co.onclass.api;

import co.onclass.api.dto.ApiSuccessResponse;
import co.onclass.api.dto.bootcamp.BootcampRequestDto;
import co.onclass.api.utils.BootcampMapper;
import co.onclass.api.validation.ValidationService;
import co.onclass.usecase.bootcamp.BootcampUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.ServerResponse.status;

@Component
@RequiredArgsConstructor
public class Handler {

    private final BootcampUseCase bootcampUseCase;
    private final ValidationService validationService;
    private final BootcampMapper bootcampMapper;

    public Mono<ServerResponse> listenPOSTGuardarBootcamp(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(BootcampRequestDto.class)
                .flatMap(validationService::validateObject)
                .map(bootcampMapper::toBootcamp)
                .flatMap(bootcampUseCase::guardarBootcamp)
                .map(bootcampMapper::toBootcampResponseDto)
                .flatMap(bootcampGuardado ->
                        status(201)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(new ApiSuccessResponse<>(bootcampGuardado))
                );
    }
}
