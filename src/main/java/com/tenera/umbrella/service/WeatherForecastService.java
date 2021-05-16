package com.tenera.umbrella.service;

import com.tenera.umbrella.model.CurrentWeatherResponseModel;
import com.tenera.umbrella.model.HistoricalWeatherResponseModel;

import java.util.Optional;

public interface WeatherForecastService {

    CurrentWeatherResponseModel getCurrentWeatherForCity(final String city);

    Optional<HistoricalWeatherResponseModel> getHistoricalWeatherForCity(final String city);
}
