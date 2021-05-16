package com.tenera.umbrella.service;

import com.tenera.umbrella.client.WeatherRestClient;
import com.tenera.umbrella.client.WeatherRestClientImpl;
import com.tenera.umbrella.dto.CurrentWeatherPojo;
import com.tenera.umbrella.model.CurrentWeatherResponseModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.github.npathai.hamcrestopt.OptionalMatchers.isPresent;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.*;


@ExtendWith(MockitoExtension.class)
public class WeatherForecastServiceTest {

    private static final Set<String> RAIN_LIST = Set.of("Thunderstorm", "Drizzle", "Rain");

    @Mock
    private WeatherRestClient weatherRestClient = new WeatherRestClientImpl();

    @Mock
    private WeatherForecastHistoryHolderService weatherForecastHistoryHolderService = new WeatherForecastHistoryHolderServiceImpl();

    @InjectMocks
    private WeatherForecastService weatherForecastService = new WeatherForecastServiceImpl();

    @ParameterizedTest
    @ValueSource(strings = {"Thunderstorm", "Drizzle", "Rain", "Clouds", "Sunny"})
    public void test_getCurrentWeatherForCity_fetched_weather_forecast_per_condition(final String weatherCondition) {
        final String city = "Berlin";
        final double temperature = 12.8;
        final int pressure = 2009;
        final CurrentWeatherPojo currentWeatherPojo = constructCurrentWeatherPojo(weatherCondition, temperature, pressure);
        final CurrentWeatherResponseModel currentWeatherResponseModel = constructCurrentWeatherResponseModel(weatherCondition, temperature, pressure);
        when(weatherRestClient.getWeatherForecastForCity(city)).thenReturn(completedFuture(currentWeatherPojo));

        final var result = weatherForecastService.getCurrentWeatherForCity(city);

        verify(weatherRestClient, times(1)).getWeatherForecastForCity(city);
        verify(weatherForecastHistoryHolderService, times(1)).addWeatherForecastToCache(city, currentWeatherResponseModel);
        verifyNoMoreInteractions(weatherRestClient, weatherForecastHistoryHolderService);

        assertThat(result, equalTo(currentWeatherResponseModel));
    }

    @Test
    public void test_getHistoricalWeatherForCity_returns_history_and_correct_statistics() {
        final String city = "Berlin";
        final double temperature = 12.8;
        final int pressure = 2009;
        final var currentWeatherResponseModels = RAIN_LIST.stream()
                .map(weatherCondition -> constructCurrentWeatherResponseModel(weatherCondition, temperature, pressure)
                ).collect(toList());

        when(weatherForecastHistoryHolderService.getWeatherForecasts(city)).thenReturn(Optional.of(currentWeatherResponseModels));

        final var result = weatherForecastService.getHistoricalWeatherForCity(city);

        verify(weatherForecastHistoryHolderService, times(1)).getWeatherForecasts(city);
        verifyNoMoreInteractions(weatherForecastHistoryHolderService);

        assertThat(result, isPresent());
        assertThat(result.get().getHistory(), hasSize(3));
        assertThat(result.get().getAverageTemperature(), equalTo(new BigDecimal("12.80")));
        assertThat(result.get().getAverageAirPressure(), equalTo(new BigDecimal("2009.00")));
    }


    private CurrentWeatherResponseModel constructCurrentWeatherResponseModel(final String weatherCondition, final double temperature, final int pressure) {
        return CurrentWeatherResponseModel.builder()
                .temperature(BigDecimal.valueOf(temperature))
                .airPressure(pressure)
                .umbrellaNeeded(RAIN_LIST.contains(weatherCondition))
                .build();
    }

    private CurrentWeatherPojo constructCurrentWeatherPojo(final String weatherCondition, final double temperature, final int pressure) {
        return CurrentWeatherPojo.builder()
                .weather(List.of(CurrentWeatherPojo.Weather.builder()
                                .main(weatherCondition)
                                .build(),
                        CurrentWeatherPojo.Weather.builder()
                                .main("Random")
                                .build()))
                .main(CurrentWeatherPojo.Main.builder()
                        .temp(temperature)
                        .pressure(pressure)
                        .build())
                .build();
    }
}
