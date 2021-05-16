package com.tenera.umbrella.util;

import com.tenera.umbrella.model.CurrentWeatherResponseModel;
import lombok.Getter;
import lombok.Setter;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;

import java.util.List;

public class CacheHelper {

    @Getter
    @Setter
    private CacheManager cacheManager;
    @Getter
    @Setter
    private Cache<String, List<CurrentWeatherResponseModel>> weatherCache;

    @SuppressWarnings("unchecked")
    private final Class<List<CurrentWeatherResponseModel>> type = (Class<List<CurrentWeatherResponseModel>>) (Object) List.class;

    public CacheHelper() {
        cacheManager = CacheManagerBuilder
                .newCacheManagerBuilder().build();
        cacheManager.init();

        weatherCache = cacheManager
                .createCache("weather", CacheConfigurationBuilder
                        .newCacheConfigurationBuilder(
                                String.class, type,
                                ResourcePoolsBuilder.heap(10)));
    }
}
