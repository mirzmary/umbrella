package com.tenera.umbrella.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class CurrentWeatherResponseModel {
    @NonNull
    private BigDecimal temperature;
    @NonNull
    private Integer airPressure;
    @NonNull
    private boolean umbrellaNeeded;
}
