package com.example.weatherapp.api

data class MainX(
    val feels_like: Double,
    val grnd_level: Int,
    val humidity: Int,
    val pressure: Int,
    val sea_level: Int,
    val temp: String,
    val temp_kf: Double,
    val temp_max: String,
    val temp_min: String
)