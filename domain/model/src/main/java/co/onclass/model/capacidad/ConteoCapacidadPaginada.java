package co.onclass.model.capacidad;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConteoCapacidadPaginada {

    private long totalElementos;
    private List<Long> ids;
}
