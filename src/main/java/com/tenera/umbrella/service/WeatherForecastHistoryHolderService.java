package com.tenera.umbrella.service;

import com.tenera.umbrella.model.CurrentWeatherResponseModel;

import java.util.List;
import java.util.Optional;

public interface WeatherForecastHistoryHolderService {

    void addWeatherForecastToCache(final String city, final CurrentWeatherResponseModel currentWeatherResponseModel);

    Optional<List<CurrentWeatherResponseModel>> getWeatherForecasts(final String city);
}
