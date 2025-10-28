package co.onclass.api.dto.bootcamp;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CapacidadesBootcampRequestDto {

    @NotEmpty(message = "La lista de capacidades es obligatoria.")
    private List<Long> capacidades;
}
