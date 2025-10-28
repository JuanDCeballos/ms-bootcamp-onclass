package co.onclass.consumer.dto.capacidad;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CapacidadBootcampRequestDto {

    private Long idBootcamp;
    private List<Long> capacidadesIds;
}
