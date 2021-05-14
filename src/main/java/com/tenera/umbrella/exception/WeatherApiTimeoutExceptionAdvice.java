package com.tenera.umbrella.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class WeatherApiTimeoutExceptionAdvice {

    @ResponseBody
    @ExceptionHandler(WeatherApiTimeoutException.class)
    @ResponseStatus(HttpStatus.GATEWAY_TIMEOUT)
    String eventDeserializationHandler(final WeatherApiTimeoutException ex) {
        return ex.getMessage();
    }
}
