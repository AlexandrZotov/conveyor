package com.example.conveyor.exception;

public class CreditValidationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public CreditValidationException(String message) { super(String.format("Отказ: %s", message)); }
}
