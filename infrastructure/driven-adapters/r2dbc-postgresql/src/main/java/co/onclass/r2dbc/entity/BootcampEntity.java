package co.onclass.r2dbc.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;

@Table("bootcamps")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class BootcampEntity {

    @Id
    private Long id;

    private String nombre;
    private String descripcion;

    @Column("fecha_lanzamiento")
    private LocalDate fechaLanzamiento;

    private Integer duracion;
}
