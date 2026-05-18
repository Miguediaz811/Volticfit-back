package com.proyecto.volticfit.exception;

public class NotificationException
        extends RuntimeException {

    public NotificationException(
            String message
    ) {

        super(message);
    }
}