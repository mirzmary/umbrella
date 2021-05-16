package com.tenera.umbrella.service;

import com.tenera.umbrella.model.CurrentWeatherResponseModel;
import com.tenera.umbrella.util.CacheHelper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.tenera.umbrella.util.CityNameUtil.normalizeCityName;

@Service
public class WeatherForecastHistoryHolderServiceImpl implements WeatherForecastHistoryHolderService {

    private static final int WEATHER_FORECAST_CACHE_SIZE = 5;

    private final CacheHelper cache = new CacheHelper();

    @Override
    public void addWeatherForecastToCache(final String city, final CurrentWeatherResponseModel currentWeatherResponseModel) {
        final var normalizedCity = normalizeCityName(city);
        final List<CurrentWeatherResponseModel> currentWeatherResponseModelListForCache = new ArrayList<>();
        currentWeatherResponseModelListForCache.add(currentWeatherResponseModel);
        if (cache.getWeatherCache().containsKey(normalizedCity)) {
            final var currentWeatherResponseModels = cache.getWeatherCache().get(normalizedCity);
            currentWeatherResponseModelListForCache.addAll(List.copyOf(
                    currentWeatherResponseModels.size() == WEATHER_FORECAST_CACHE_SIZE ?
                            currentWeatherResponseModels.subList(1, currentWeatherResponseModels.size()) :
                            currentWeatherResponseModels.subList(0, currentWeatherResponseModels.size())));
        }

        cache.getWeatherCache().put(normalizedCity, currentWeatherResponseModelListForCache);
    }

    @Override
    public Optional<List<CurrentWeatherResponseModel>> getWeatherForecasts(final String city) {
        final var normalizedCity = normalizeCityName(city);
        return Optional.ofNullable(cache.getWeatherCache().get(normalizedCity));
    }
}
