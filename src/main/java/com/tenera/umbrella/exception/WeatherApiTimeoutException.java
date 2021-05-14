package com.tenera.umbrella.exception;

public class WeatherApiTimeoutException extends RuntimeException {

    public WeatherApiTimeoutException(String message) {
        super(message);
    }
}
