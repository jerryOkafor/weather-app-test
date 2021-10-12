package com.jerryokafor.weatherapp.api

import com.jerryokafor.weatherapp.BuildConfig
import com.jerryokafor.weatherapp.model.City
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * @Author Jerry Okafor
 * @Project WeatherApp
 * @Date 10/10/2021 20:13
 */
interface WeatherService {
    @GET("data/2.5/weather")
    suspend fun getCity(
        @Query("q") query: String,
        @Query("appid") appId: String = BuildConfig.APP_ID
    ): City
}