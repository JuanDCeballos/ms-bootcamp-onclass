package co.onclass.exceptions;

import co.onclass.enums.ExceptionMessages;
import lombok.Getter;

@Getter
public class TechnicalException extends RuntimeException {

    private final ExceptionMessages exceptionMessages;

    public TechnicalException(ExceptionMessages exceptionMessages) {
        super(exceptionMessages.getMessage());
        this.exceptionMessages = exceptionMessages;
    }
}
