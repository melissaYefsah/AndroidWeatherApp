package com.example.weatherapp.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("data/2.5/weather")
    suspend fun getWeather(
        @Query("q") city:String,
        @Query("appid") apiKey:String
    ): Response<WeatherModel>

    // Weather forecast endpoint
    @GET("data/2.5/forecast")
    suspend fun getWeatherForecast(
        @Query("q") city: String,
        @Query("appid") apiKey: String
    ): Response<ForcastModel>
    @GET("data/2.5/weather")
    suspend fun getWeatherByCoordinates(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("apiKey") apiKey: String
    ): Response<WeatherModel>

    @GET("data/2.5/forecast")
    suspend fun getWeatherForecastByCoordinates(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("apiKey") apiKey: String
    ): Response<ForcastModel>
}
