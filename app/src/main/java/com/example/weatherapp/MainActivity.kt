package com.example.weatherapp

import LocationHelper
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.ui.theme.WeatherAppTheme

class MainActivity : ComponentActivity() {
    private lateinit var locationHelper: LocationHelper

    // This will launch the permission request when called
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                // Permission granted, now we can get the location
                getLocation()
            } else {
                // Permission denied
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the LocationHelper
        locationHelper = LocationHelper(this)

        setContent {
            val weatherViewModel = ViewModelProvider(this)[WeatherViewModel::class.java]

            WeatherAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    WeatherPage(weatherViewModel)
                }
            }
        }

        // Check for location permissions on create
        checkLocationPermission()
    }

    // Function to check for location permission
    private fun checkLocationPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED -> {
                // Permission is already granted, so we can fetch the location
                getLocation()
            }
            else -> {
                // Request the location permission
                requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    // Function to get the current location
    private fun getLocation() {
        locationHelper.getCurrentLocation { location ->
            if (location != null) {
                val latitude = location.latitude
                val longitude = location.longitude
                // Now you can use the latitude and longitude for your API requests or UI
                Toast.makeText(this, "Location: $latitude, $longitude", Toast.LENGTH_SHORT).show()
            } else {
                // Handle the case where location is not available (null)
                Toast.makeText(this, "Location not available", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
