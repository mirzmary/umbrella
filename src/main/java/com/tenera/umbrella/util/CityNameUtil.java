package com.tenera.umbrella.util;

public class CityNameUtil {

    public static String normalizeCityName(final String city) {
        return city.contains(",") ? city.split(",")[0] : city;
    }
}
