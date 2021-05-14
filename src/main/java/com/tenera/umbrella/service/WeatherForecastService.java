package com.tenera.umbrella.service;

import com.tenera.umbrella.model.CurrentWeatherResponseModel;

public interface WeatherForecastService {

    CurrentWeatherResponseModel getCurrentWeatherForCity(final String city);
}
