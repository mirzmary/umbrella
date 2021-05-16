package com.tenera.umbrella.model;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class HistoricalWeatherResponseModel {
    @NonNull
    private BigDecimal averageTemperature;
    @NonNull
    private BigDecimal averageAirPressure;
    @NonNull
    private List<CurrentWeatherResponseModel> history;
}
