package co.onclass.api.utils;

import co.onclass.api.dto.bootcamp.BootcampRequestDto;
import co.onclass.api.dto.bootcamp.BootcampResponseDto;
import co.onclass.api.dto.bootcamp.CapacidadesBootcampResponseDto;
import co.onclass.model.bootcamp.Bootcamp;
import co.onclass.model.bootcamp.BootcampDetallado;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BootcampMapper {

    Bootcamp toBootcamp(BootcampRequestDto bootcampRequestDto);

    BootcampResponseDto toBootcampResponseDto(Bootcamp bootcamp);

    CapacidadesBootcampResponseDto toCapacidadesBootcampResponse(BootcampDetallado bootcampDetallado);
}
