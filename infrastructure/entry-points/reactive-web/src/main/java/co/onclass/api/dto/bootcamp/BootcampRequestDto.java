package co.onclass.api.dto.bootcamp;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BootcampRequestDto {

    @NotBlank(message = "El nombre del Bootcamp es obligatorio.")
    private String nombre;

    @NotBlank(message = "La descripción del Bootcamp es obligatoria.")
    private String descripcion;

    @NotNull(message = "La fecha de lanzamiento del Bootcamp es obligatoria.")
    private LocalDate fechaLanzamiento;

    @NotNull(message = "La duración del Bootcamp es obligatoria.")
    private Integer duracion;
}
