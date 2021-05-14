package com.tenera.umbrella.client;

import com.tenera.umbrella.dto.CurrentWeatherPojo;

import java.util.concurrent.CompletableFuture;

public interface WeatherRestClient {

    CompletableFuture<CurrentWeatherPojo> getWeatherForecastForCity(final String city);
}
