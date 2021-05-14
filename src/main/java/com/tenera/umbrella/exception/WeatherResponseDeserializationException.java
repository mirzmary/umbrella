package com.tenera.umbrella.exception;

public class WeatherResponseDeserializationException extends RuntimeException {

    public WeatherResponseDeserializationException(String message) {
        super(message);
    }
}
