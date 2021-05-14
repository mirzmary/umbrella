package com.tenera.umbrella.service;

import com.tenera.umbrella.client.WeatherRestClient;
import com.tenera.umbrella.dto.CurrentWeatherPojo;
import com.tenera.umbrella.exception.WeatherApiTimeoutException;
import com.tenera.umbrella.exception.WeatherResponseDeserializationException;
import com.tenera.umbrella.model.CurrentWeatherResponseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
public class WeatherForecastServiceImpl implements WeatherForecastService {

    private static final Set<String> RAIN_LIST = Set.of("Thunderstorm", "Drizzle", "Rain");

    @Autowired
    private WeatherRestClient weatherRestClient;

    @Override
    public CurrentWeatherResponseModel getCurrentWeatherForCity(final String city) {
        try {
            final CurrentWeatherPojo weatherForecastForCity = weatherRestClient.getWeatherForecastForCity(city).get(10, TimeUnit.SECONDS);
            return map(weatherForecastForCity);
        } catch (final InterruptedException | ExecutionException | TimeoutException exception) {
            throw new WeatherApiTimeoutException("Could not get response from WeatherAPI within specified timeout");
        }
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
