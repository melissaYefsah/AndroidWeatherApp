package com.example.weatherapp

import LocationHelper
import android.app.Application
import android.location.Address
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.api.Constant
import com.example.weatherapp.api.ForcastModel
import com.example.weatherapp.api.NetworkResponse
import com.example.weatherapp.api.RetrofitInstance
import com.example.weatherapp.api.WeatherModel
import kotlinx.coroutines.launch
import java.lang.Exception
import android.location.Geocoder
import java.util.Locale


class WeatherViewModel:ViewModel() {
    //we will create a method that will retrieve data from the retrofit every time we click the search icon
    private val weatherApi = RetrofitInstance.weatherApi
    private val _weatherResult = MutableLiveData<NetworkResponse<WeatherModel>>()
    val weatherResult: LiveData<NetworkResponse<WeatherModel>> = _weatherResult

    // LiveData for forecast data
    private val _forecastResult = MutableLiveData<NetworkResponse<ForcastModel>>()
    val forecastResult: LiveData<NetworkResponse<ForcastModel>> = _forecastResult

    fun getData(city: String) {
        _weatherResult.value = NetworkResponse.Loading
        viewModelScope.launch {
            try {
                val response = weatherApi.getWeather(city, Constant.apiKey)

                if (response.isSuccessful) {
                    response.body()?.let {
                        _weatherResult.value = NetworkResponse.Success(it)
                    }
                } else {
                    _weatherResult.value = NetworkResponse.Error("Location not found")
                }
            } catch (e: Exception) {
                _weatherResult.value = NetworkResponse.Error("An error occurred: ${e.message}a")
            }

        }

    }

    fun getForecastData(city: String) {
        _forecastResult.value = NetworkResponse.Loading
        viewModelScope.launch {
            try {
                val response = weatherApi.getWeatherForecast(city, Constant.apiKey)

                if (response.isSuccessful) {
                    response.body()?.let {
                        // Log the successful response
                        Log.d("WeatherViewModel", "Forecast Response: ${it}")
                        _forecastResult.value = NetworkResponse.Success(it)
                    }
                } else {
                    // Log failure response
                    Log.e("WeatherViewModel", "Failed to load forecast data: ${response.message()}")
                    _forecastResult.value = NetworkResponse.Error("Failed to load forecast data")
                }
            } catch (e: Exception) {
                // Log exception details
                Log.e("WeatherViewModel", "Error fetching forecast data: ${e.message}", e)
                _forecastResult.value = NetworkResponse.Error("Failed to load forecast data")
            }
        }
    }

    fun getWeatherDataByCoordinates(lat: Double, lon: Double) {
        _weatherResult.value = NetworkResponse.Loading
        viewModelScope.launch {
            try {
                val response = weatherApi.getWeatherByCoordinates(lat, lon, Constant.apiKey)

                if (response.isSuccessful) {
                    response.body()?.let {
                        _weatherResult.value = NetworkResponse.Success(it)
                    }
                } else {
                    _weatherResult.value = NetworkResponse.Error("Location not found")
                }
            } catch (e: Exception) {
                _weatherResult.value = NetworkResponse.Error("An error occurred: ${e.message}")
            }
        }
    }

    fun getForecastDataByCoordinates(lat: Double, lon: Double) {
        _forecastResult.value = NetworkResponse.Loading
        viewModelScope.launch {
            try {
                val response = weatherApi.getWeatherForecastByCoordinates(lat, lon, Constant.apiKey)

                if (response.isSuccessful) {
                    response.body()?.let {
                        _forecastResult.value = NetworkResponse.Success(it)
                    }
                } else {
                    _forecastResult.value = NetworkResponse.Error("Failed to load forecast data")
                }
            } catch (e: Exception) {
                _forecastResult.value = NetworkResponse.Error("Failed to load forecast data")
            }
        }
    }


}



