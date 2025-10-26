package co.onclass.api.utils;

import co.onclass.api.dto.bootcamp.BootcampRequestDto;
import co.onclass.api.dto.bootcamp.BootcampResponseDto;
import co.onclass.model.bootcamp.Bootcamp;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BootcampMapper {

    Bootcamp toBootcamp(BootcampRequestDto bootcampRequestDto);

    BootcampResponseDto toBootcampResponseDto(Bootcamp bootcamp);
}
