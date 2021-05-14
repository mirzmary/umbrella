package com.tenera.umbrella.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class WeatherResponseDeserializationExceptionAdvice {

    @ResponseBody
    @ExceptionHandler(WeatherResponseDeserializationException.class)
    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    String eventDeserializationHandler(final WeatherResponseDeserializationException ex) {
        return ex.getMessage();
    }
}
