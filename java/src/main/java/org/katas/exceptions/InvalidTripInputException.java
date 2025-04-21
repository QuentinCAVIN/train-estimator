package org.katas.exceptions;

public class InvalidTripInputException extends RuntimeException {
    public InvalidTripInputException(String message) {
        super(message);
    }
}
