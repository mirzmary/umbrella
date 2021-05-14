package com.tenera.umbrella.controller;

import com.tenera.umbrella.model.CurrentWeatherResponseModel;
import com.tenera.umbrella.service.WeatherForecastService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WeatherController {

    @Autowired
    private WeatherForecastService weatherForecastService;

    @GetMapping("/current")
    public ResponseEntity<CurrentWeatherResponseModel> getCurrentWeather(@RequestParam String location) {
        final var currentWeatherForCity = weatherForecastService.getCurrentWeatherForCity(location);

        return ResponseEntity.ok(currentWeatherForCity);
    }
}
