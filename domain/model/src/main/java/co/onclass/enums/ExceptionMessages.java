package co.onclass.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ExceptionMessages {

    PRUEBA(0, "PRUEBA");

    private final int code;
    private final String message;
}
