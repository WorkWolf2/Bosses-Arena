package com.minegolem.bossesArena.exception;

public class ArenaAlreadyExistsException extends RuntimeException {
    public ArenaAlreadyExistsException(String errorMessage) {
        super(errorMessage);
    }

    public ArenaAlreadyExistsException(String errorMessage, Throwable cause) {
        super(errorMessage, cause);
    }

    public ArenaAlreadyExistsException(Throwable cause) {
        super(cause);
    }

    public ArenaAlreadyExistsException() {
        super();
    }
}
