package com.tenera.umbrella.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CurrentWeatherPojo {

    public Coord coord;
    public List<Weather> weather;
    public String base;
    public Main main;
    public int visibility;
    public Wind wind;
    public Clouds clouds;
    public int dt;
    public Sys sys;
    public int timezone;
    public int id;
    public String name;
    public int cod;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Coord{
        public double lon;
        public double lat;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Weather{
        public int id;
        public String main;
        public String description;
        public String icon;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Main{
        public double temp;
        public double feels_like;
        public double temp_min;
        public double temp_max;
        public int pressure;
        public int humidity;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Wind{
        public double speed;
        public int deg;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Clouds{
        public int all;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Sys{
        public int type;
        public int id;
        public String country;
        public int sunrise;
        public int sunset;
    }


}
