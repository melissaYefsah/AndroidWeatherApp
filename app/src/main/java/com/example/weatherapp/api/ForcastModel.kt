package com.example.weatherapp.api

data class ForcastModel(
    val city: City,
    val cnt: Int,
    val cod: String,
    val list: List<Item0>,
    val message: Int
)