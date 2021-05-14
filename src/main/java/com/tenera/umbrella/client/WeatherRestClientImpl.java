package com.tenera.umbrella.client;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tenera.umbrella.dto.CurrentWeatherPojo;
import com.tenera.umbrella.exception.WeatherApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.util.concurrent.CompletableFuture.completedFuture;
import static java.util.concurrent.CompletableFuture.failedFuture;

@Slf4j
@Service
public class WeatherRestClientImpl implements WeatherRestClient, AutoCloseable {

    public static final ObjectMapper MAPPER = new ObjectMapper()
            .setSerializationInclusion(JsonInclude.Include.NON_DEFAULT)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private final static Duration TIMEOUT_DURATION = Duration.ofSeconds(10);
    private final static ExecutorService executor = Executors.newCachedThreadPool();

    private static URI baseURL = URI.create("https://api.openweathermap.org/data/2.5/");

    private static URI currentWeatherURL = URI.create("weather");

    private static String PARAM_SEARCH_APP_ID_KEY = "appid";

    @Value("${weather.apiKey}")
    private String PARAM_SEARCH_APP_ID_VALUE;

    private static String PARAM_QUERY_KEY = "q";

    private static String PARAM_QUERY_TEMPERATURE_UNIT_KEY = "units";
    private static String PARAM_QUERY_TEMPERATURE_UNIT_VALUE = "metric";

    private final HttpClient httpClient;

    public WeatherRestClientImpl() {
        httpClient = HttpClient.newBuilder()
                .connectTimeout(TIMEOUT_DURATION)
                .version(HttpClient.Version.HTTP_1_1)
                .executor(executor)
                .build();
    }

    @Override
    public CompletableFuture<CurrentWeatherPojo> getWeatherForecastForCity(final String city) {
        return createGetRequest(city)
                .thenApply(WeatherRestClientImpl::deserializeWCurrentWeatherResponse)
                .thenCompose(weatherResponseOpt ->
                        weatherResponseOpt.map(CompletableFuture::completedFuture).orElseGet(() -> failedFuture(new WeatherApiException("Failed to deserialize weather API response to CurrentWeatherPojo")))
                );
    }


    private CompletableFuture<HttpResponse<String>> createGetRequest(final String city) {
        final URI searchURI = prepareURI(city);
        final var request = HttpRequest.newBuilder()
                .uri(searchURI)
                .header("content-type", "application/json")
                .header("accept", "application/json")
                .GET()
                .build();
        return httpClient
                .sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenCompose(this::auditErrorsAndFail);
    }

    private URI prepareURI(final String city) {
        final URI weatherURL = baseURL.resolve(currentWeatherURL);

        return UriBuilder.fromUri(weatherURL)
                .queryParam(PARAM_SEARCH_APP_ID_KEY, PARAM_SEARCH_APP_ID_VALUE)
                .queryParam(PARAM_QUERY_TEMPERATURE_UNIT_KEY, PARAM_QUERY_TEMPERATURE_UNIT_VALUE)
                .queryParam(PARAM_QUERY_KEY, city)
                .build();
    }


    public static Optional<CurrentWeatherPojo> deserializeWCurrentWeatherResponse(HttpResponse<String> response) {
        try {
            final var pojo = MAPPER.readValue(response.body(), CurrentWeatherPojo.class);
            return Optional.of(pojo);
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize JSON to required type", e);
            return Optional.empty();
        }
    }

    private CompletableFuture<HttpResponse<String>> auditErrorsAndFail(final HttpResponse<String> httpResponse) {
        if (httpResponse.statusCode() >= HttpStatus.MULTIPLE_CHOICES.value()) {
            final String errorMessage = String.format("Weather API responded with an exception for weather per city search, statusCode - %s", httpResponse.statusCode());
            log.error(errorMessage);
            return failedFuture(new WeatherApiException(errorMessage));
        }
        return completedFuture(httpResponse);
    }

    @Override
    public void close() {
        executor.shutdown();
    }
}
