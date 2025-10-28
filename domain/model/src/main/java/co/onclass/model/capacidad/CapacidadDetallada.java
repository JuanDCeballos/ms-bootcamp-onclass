package co.onclass.model.capacidad;

import co.onclass.model.tecnologia.TecnologiaInfo;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class CapacidadDetallada {

    private Long id;
    private String nombre;
    private List<TecnologiaInfo> tecnologias;
}
