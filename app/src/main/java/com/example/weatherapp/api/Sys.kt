package com.example.weatherapp.api

data class Sys(
    val country: String,
    val id: String,
    val sunrise: Long,
    val sunset: Long,
    val type: String
)