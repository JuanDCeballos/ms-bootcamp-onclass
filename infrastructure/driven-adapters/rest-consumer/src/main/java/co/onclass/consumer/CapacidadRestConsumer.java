package co.onclass.consumer;

import co.onclass.consumer.dto.ExternalApiErrorResponse;
import co.onclass.consumer.dto.capacidad.CapacidadBootcampRequestDto;
import co.onclass.enums.ExceptionMessages;
import co.onclass.exceptions.BusinessException;
import co.onclass.exceptions.TechnicalException;
import co.onclass.model.capacidad.CapacidadDetallada;
import co.onclass.model.capacidad.gateways.CapacidadGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CapacidadRestConsumer implements CapacidadGateway {

    private final WebClient client;

    private static final String URI_ASIGNAR_CAPACIDADES = "/api/v1/capacidad/asignar-capacidades";

    @Override
    public Mono<List<CapacidadDetallada>> asignarCapacidades(Long idBootcamp, List<Long> capacidadesIds) {
        CapacidadBootcampRequestDto req = CapacidadBootcampRequestDto.builder()
                .idBootcamp(idBootcamp)
                .capacidadesIds(capacidadesIds)
                .build();

        return client
                .post()
                .uri(URI_ASIGNAR_CAPACIDADES)
                .bodyValue(req)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, res ->
                        res.bodyToMono(ExternalApiErrorResponse.class)
                                .flatMap(errorBody -> {
                                    String errorMessage = String.join(", ", errorBody.getDetails());
                                    return Mono.error(new BusinessException(
                                            ExceptionMessages.ERROR_DE_NEGOCIO_EN_MICROSERVICIO_EXTERNO, errorMessage));
                                })
                )
                .onStatus(HttpStatusCode::is5xxServerError, res ->
                        res.bodyToMono(ExternalApiErrorResponse.class)
                                .flatMap(errorBody ->
                                        Mono.error(new TechnicalException(
                                                ExceptionMessages.ERROR_TECNICO_EN_MICROSERVICIO_EXTERNO))
                                )
                )
                .bodyToFlux(CapacidadDetallada.class)
                .collectList();
    }
}
