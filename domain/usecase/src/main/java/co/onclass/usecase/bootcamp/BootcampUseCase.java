package co.onclass.usecase.bootcamp;

import co.onclass.enums.ExceptionMessages;
import co.onclass.exceptions.BusinessException;
import co.onclass.model.bootcamp.Bootcamp;
import co.onclass.model.bootcamp.BootcampDetallado;
import co.onclass.model.bootcamp.gateways.BootcampRepository;
import co.onclass.model.capacidad.CapacidadDetallada;
import co.onclass.model.capacidad.gateways.CapacidadGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
}
