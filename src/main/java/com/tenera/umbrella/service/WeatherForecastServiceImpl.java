package com.tenera.umbrella.service;

import com.tenera.umbrella.client.WeatherRestClient;
import com.tenera.umbrella.dto.CurrentWeatherPojo;
import com.tenera.umbrella.exception.WeatherApiException;
import com.tenera.umbrella.exception.WeatherApiTimeoutException;
import com.tenera.umbrella.exception.WeatherResponseDeserializationException;
import com.tenera.umbrella.model.CurrentWeatherResponseModel;
import com.tenera.umbrella.model.HistoricalWeatherResponseModel;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@Service
public class WeatherForecastServiceImpl implements WeatherForecastService {

    private static final Set<String> RAIN_LIST = Set.of("Thunderstorm", "Drizzle", "Rain");

    @Autowired
    private WeatherRestClient weatherRestClient;

    @Autowired
    private WeatherForecastHistoryHolderService weatherForecastHistoryHolderService;

    @Override
    public CurrentWeatherResponseModel getCurrentWeatherForCity(final String city) {
        try {
            final var weatherForecastForCity = weatherRestClient.getWeatherForecastForCity(city).get(10, TimeUnit.SECONDS);
            final var currentWeatherResponseModel = map(weatherForecastForCity);
            weatherForecastHistoryHolderService.addWeatherForecastToCache(city, currentWeatherResponseModel);
            return currentWeatherResponseModel;
        } catch (final InterruptedException | TimeoutException exception) {
            throw new WeatherApiTimeoutException("Could not get response from WeatherAPI within specified timeout");
        } catch (final ExecutionException exception) {
            throw new WeatherApiException(exception.getMessage());
        }
    }

    @Override
    public Optional<HistoricalWeatherResponseModel> getHistoricalWeatherForCity(final String city) {
        return weatherForecastHistoryHolderService.getWeatherForecasts(city)
                .map(weatherForecasts -> {
                            final int numberOfForecasts = weatherForecasts.size();
                            final Pair<BigDecimal, Integer> stats = weatherForecasts.stream()
                                    .collect(
                                            Collectors.teeing(
                                                    Collectors.reducing(BigDecimal.ZERO, CurrentWeatherResponseModel::getTemperature, BigDecimal::add),
                                                    Collectors.reducing(0, CurrentWeatherResponseModel::getAirPressure, Integer::sum),
                                                    Pair::of
                                            )
                                    );
                            return Optional.of(new HistoricalWeatherResponseModel(stats.getLeft().divide(BigDecimal.valueOf(numberOfForecasts), 2, RoundingMode.HALF_UP),
                                    BigDecimal.valueOf(stats.getRight()).divide(BigDecimal.valueOf(numberOfForecasts), 2, RoundingMode.HALF_UP),
                                    weatherForecasts));
                        }
                )
                .orElse(Optional.empty());
    }

    private CurrentWeatherResponseModel map(final CurrentWeatherPojo pojo) {
        if (pojo.weather.isEmpty()) {
            throw new WeatherResponseDeserializationException("Bad weather response got from the API with not enough data");
        }
        return CurrentWeatherResponseModel.builder()
                .umbrellaNeeded(isUmbrellaNeeded(pojo.weather.get(0).main))
                .temperature(BigDecimal.valueOf(pojo.main.temp))
                .airPressure(pojo.main.pressure)
                .build();
    }

    private boolean isUmbrellaNeeded(final String weatherDescription) {
        return RAIN_LIST.contains(weatherDescription);
    }
}
