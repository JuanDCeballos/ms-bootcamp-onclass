package co.onclass.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ExceptionMessages {

    BOOTCAMP_NO_ENCONTRADO(404, "El Bootcamp no fue encontrado."),
    BOOTCAMP_CON_CAPACIDADES_DUPLICADAS(400, "El Bootcamp no permite capacidades duplicadas."),
    MONTO_CAPACIDADES_A_ASIGNAR(400, "El Bootcamp permite entre 1 y 4 capacidades."),
    ERROR_DE_NEGOCIO_EN_MICROSERVICIO_EXTERNO(400, "Error de negocio en microservicio externo."),
    ERROR_TECNICO_EN_MICROSERVICIO_EXTERNO(500, "Error técnico en microservicio externo."),
    ;

    private final int code;
    private final String message;
}
