package com.example.weatherapp

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Card
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.example.weatherapp.api.ForcastModel
import com.example.weatherapp.api.NetworkResponse
import com.example.weatherapp.api.WeatherModel
import java.util.Calendar
import java.util.Date
import java.util.TimeZone
import java.text.SimpleDateFormat
import java.util.*
import com.google.android.gms.location.LocationServices
import android.location.Location
import android.widget.Toast
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext


@Composable
fun WeatherPage (viewModel: WeatherViewModel) {
    var city by remember {
        mutableStateOf("")
    }
    val weatherResult = viewModel.weatherResult.observeAsState()
    val forecastResult = viewModel.forecastResult.observeAsState()

    // State for managing favorites
    var favorites by remember { mutableStateOf<List<String>>(emptyList()) }

    // State to manage current selected city for weather details
    var selectedCityWeather by remember { mutableStateOf<WeatherModel?>(null) }

    // Observe the weather description
    val weatherDescription = weatherResult.value?.let {
        if (it is NetworkResponse.Success) it.data.weather.firstOrNull()?.main.orEmpty()
        else ""
    } ?: ""
    val isWeatherLoaded = weatherResult.value is NetworkResponse.Success

    // State for Celsius/Fahrenheit toggle
    var isCelsius by remember { mutableStateOf(true) }

    // Current Location Button
    val context = LocalContext.current
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    // Get Current Location on Button Press
    fun getCurrentLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                val latitude = it.latitude
                val longitude = it.longitude
                val city = "Latitude: $latitude, Longitude: $longitude"
                viewModel.getWeatherDataByCoordinates(latitude, longitude)  // Use the new function to fetch weather based on coordinates
                viewModel.getForecastDataByCoordinates(latitude, longitude)
            }
        }
    }

    // Function to toggle a city in the favorites list
    fun toggleFavorite(cityName: String) {
        if (favorites.contains(cityName)) {
            Toast.makeText(context, "$cityName is already in your favorites.", Toast.LENGTH_SHORT).show()
        } else {
            favorites = favorites + cityName  // Add the city to favorites
            Toast.makeText(context, "$cityName added to favorites.", Toast.LENGTH_SHORT).show()
        }
    }
    // Function to remove a city from the favorites list
    fun removeFavorite(cityName: String) {
        favorites = favorites.filterNot { it == cityName }
        Toast.makeText(context, "$cityName removed from favorites.", Toast.LENGTH_SHORT).show()
    }
    // Function to fetch the weather for a favorite city
    fun fetchWeatherForFavorite(cityName: String) {
        viewModel.getData(cityName) // Fetch the weather data for the clicked city
        viewModel.getForecastData(cityName)
    }


    Box(modifier = Modifier.fillMaxSize()) {
        // If no weather data is loaded, show the gradient background
        if (!isWeatherLoaded) {
            GradientBackground()
        } else {
            // If weather data is loaded, show the image background
            ImageBackground(weatherDescription)
        }



        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),

            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly,

                    ) {

                    OutlinedTextField(
                        modifier = Modifier.weight(1f),
                        value = city,
                        onValueChange = {
                            city = it
                        },
                        label = {
                            Text(text = "Search for Location")
                        },
                    )
                    IconButton(onClick = {
                        viewModel.getData(city)
                        viewModel.getForecastData(city)
                    }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search for Location"
                        )
                    }

                }

            }
            // Add a Button to get the current location
            item {
                Button(
                    onClick = { getCurrentLocation() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF548BB8), // Light blue color for the background
                        contentColor = Color.White // White text color
                    )
                ) {
                    Text("Use Current Location")
                }
            }
            item {

                when (val result = weatherResult.value) {
                    is NetworkResponse.Error -> {
                        Text(
                            text = result.message,
                            color = Color.Blue,
                            modifier = Modifier.padding(20.dp),
                            fontSize = 30.sp,

                        )
                    }

                    NetworkResponse.Loading -> {
                        CircularProgressIndicator()
                    }

                    is NetworkResponse.Success -> {
                        val cityName = result.data.name
                        // Add the favorite icon
                        FavoriteIcon(cityName = cityName, onClick = { toggleFavorite(cityName) }, isFavorite = favorites.contains(cityName))
                        WeatherDetails(data = result.data, isCelsius = isCelsius, onToggleChange = { isCelsius = it })


                    }

                    null -> {

                        WeatherDetailsPlaceholder()
                    }
                }
            }
            item {
                when (val result = forecastResult.value) {
                    is NetworkResponse.Error -> {
                        Text(text = result.message)
                    }

                    NetworkResponse.Loading -> {
                        CircularProgressIndicator()
                    }

                    is NetworkResponse.Success -> {

                        ForecastDetails(data = result.data, isCelsius= isCelsius)
                    }

                    null -> {

                        ForecastDetailsPlaceholder()
                    }
                }

            }
            item {
                if (favorites.isNotEmpty()) {
                    Text(
                        text = "Favorite Cities:",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(16.dp)
                    )
                    favorites.forEach { cityName ->
                        FavoriteCityItem(
                            cityName = cityName,
                            onRemove = { removeFavorite(cityName) },
                            onClick = { fetchWeatherForFavorite(cityName) } // Fetch weather when clicked
                        )
                    }
                }
            }

        }
    }
}

@Composable
fun WeatherDetails(data : WeatherModel, isCelsius: Boolean, onToggleChange: (Boolean) -> Unit){
    val tempInCelsius = kelvinToCelsius(data.main.temp)
    val iconUrl = getWeatherIconUrl(data.weather[0].icon)
    val tempFeelsLike = kelvinToCelsius(data.main.feels_like)
    val windSpeed = convertWindSpeedToKmh(data.wind.speed)
    val sunriseTime = convertUnixTimestampToTime(data.sys.sunrise,data.timezone)
    val sunsetTime = convertUnixTimestampToTime(data.sys.sunset,data.timezone)
    val minTemp = kelvinToCelsius(data.main.temp_min)
    val maxTemp = kelvinToCelsius(data.main.temp_max)

    // Convert the temperatures if not in Celsius
    val displayedTemp = if (isCelsius) {
        tempInCelsius
    } else {
        celsiusToFahrenheit(tempInCelsius.toDouble())
    }

    val displayedMaxTemp = if (isCelsius) {
        maxTemp
    } else {
        celsiusToFahrenheit(maxTemp.toDouble())
    }

    val displayedMinTemp = if (isCelsius) {
        minTemp
    } else {
        celsiusToFahrenheit(minTemp.toDouble())
    }


    Column (
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Temperature unit toggle switch
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = if (isCelsius) "Celsius" else "Fahrenheit", fontSize = 20.sp)
            Spacer(modifier = Modifier.width(16.dp))
            Switch(
                checked = isCelsius,
                onCheckedChange = { onToggleChange(it) }
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = "Location icon",
                modifier = Modifier.size(40.dp),

                )
            Text(
                text = "${data.name}", fontSize = 40.sp,

                )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Bottom
        ){

            Text(
                text = "$displayedTemp ${if (isCelsius) "°C" else "°F"}", fontSize = 40.sp,

            )
        }
        Spacer(modifier = Modifier.width(20.dp))

        Image(
            painter = rememberImagePainter(iconUrl),
            contentDescription = "Weather Icon",
            modifier = Modifier.size(180.dp)
        )
        Text(text = "${data.weather.firstOrNull()?.description ?: "No description"}",
            fontSize = 30.sp,
            textAlign = TextAlign.Center,
            //color= Color.White
        )
        Spacer(modifier = Modifier.height(40.dp))
        Row {
            Text(text = "High: $displayedMaxTemp ${if (isCelsius) "°C" else "°F"}", fontSize = 20.sp)
            Spacer(modifier = Modifier.width(40.dp))
            Text(text = "Low: $displayedMinTemp ${if (isCelsius) "°C" else "°F"}", fontSize = 20.sp)
        }

        Spacer(modifier = Modifier.height(10.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth()  // Make sure the Card fills the available width
                .padding(8.dp),   // Padding around the Card
            colors = CardDefaults.cardColors(containerColor = Color.LightGray.copy(alpha = 0.5f))
        ){
            Column (modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)){
                Row (modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 50.dp, max = 100.dp),
                    horizontalArrangement = Arrangement.SpaceAround){
                        WeatherKeyValue(key = "Humidity", value = "${data.main.humidity} %" )
                        WeatherKeyValue(key = "Wind Speed", value = "${"%.2f".format(windSpeed)} Km/h")
                }
                Row (modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround){
                    WeatherKeyValue(key = "Pressure", value = "${data.main.pressure} hPa")
                    WeatherKeyValue(key = "Feels Like", value = "$displayedTemp ${if (isCelsius) "°C" else "°F"}")

                }
                Row (modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround){
                    WeatherKeyValue(key = "Sunrise", value = "${sunriseTime}" )
                    WeatherKeyValue(key = "Sunset", value = "${sunsetTime}" )

                }

            }
        }

    }
}

@Composable
fun kelvinToCelsius(kelvin: String): String {
    val kelvinValue = kelvin.toDoubleOrNull() ?: return "0.0"  // Ensure we handle invalid data gracefully
    val celsius = kelvinValue - 273.15
    return String.format("%.1f", celsius)  // Format to one decimal place
}
@Composable
fun getWeatherIconUrl(iconCode: String): String {
    return "https://openweathermap.org/img/wn/$iconCode@2x.png"
}

@Composable
fun WeatherKeyValue(key:String ,value: String){
    Column (modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text(text = key,fontWeight = FontWeight.SemiBold, color = Color.White)
    }
}


@Composable
fun convertWindSpeedToKmh(windSpeedInMps: Double): Double {
    // Conversion from m/s to km/h
    return windSpeedInMps * 3.6
}
@Composable
fun convertUnixTimestampToTime(timestamp: Long, timezoneOffset: Int): String {
    val date = Date(timestamp * 1000) // Convert seconds to milliseconds
    val calendar = Calendar.getInstance()
    calendar.time = date
    calendar.add(Calendar.SECOND, timezoneOffset) // Adjust for the timezone offset

    val format = SimpleDateFormat("HH:mm:ss")
    format.timeZone = TimeZone.getTimeZone("GMT")
    return format.format(calendar.time) // Return formatted time
}
@Composable
fun ForecastDetails(data: ForcastModel, isCelsius: Boolean) {

    // Use Box or ConstraintLayout to provide explicit height constraints
    Box(modifier = Modifier.fillMaxSize()) { // Box ensures the layout has a finite height

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Icon before the text in header
                Icon(imageVector = Icons.Default.List, contentDescription = "Date Icon")
                Spacer(modifier = Modifier.width(8.dp)) // Space between icon and text
                Text(
                    text = "Weather Forecast",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Forecast items
            data.list.take(5).forEach { forecastItem ->  // Show only first 5 forecast items
                val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                val time = try {
                    val date = dateFormat.parse(forecastItem.dt_txt)
                    timeFormat.format(date)  // Extract time in "HH:mm"
                } catch (e: Exception) {
                    "N/A"  // Fallback in case of an error
                }
                //get the weather condition
                val weather_condition = forecastItem.weather[0].description

                // Convert temperature from Kelvin to Celsius

                // Get the weather icon URL
                val iconCode = forecastItem.weather[0].icon
                val iconUrl = "https://openweathermap.org/img/wn/$iconCode@2x.png"
                // Convert temperature from Kelvin to Celsius
                val tempInCelsius = kelvinToCelsius(forecastItem.main.temp)

                // Convert temperature based on the toggle
                val displayedTemp = if (isCelsius) {
                    tempInCelsius
                } else {
                    celsiusToFahrenheit(tempInCelsius.toDouble())
                }

                // Display each forecast item vertically in a Card
                Card(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(), // Ensure the card takes up the full width
                    colors = CardDefaults.cardColors(containerColor = Color.LightGray.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly,

                    ) {
                        // Display time
                        Text(text = time, fontSize = 16.sp)

                        // Display weather icon
                        Image(
                            painter = rememberImagePainter(iconUrl),
                            contentDescription = "Weather Icon",
                            modifier = Modifier.size(40.dp)
                        )

                        // Display temperature
                        Text(text = "$displayedTemp ${if (isCelsius) "°C" else "°F"}", fontSize = 16.sp)

                        //Display the weather description

                        Text(text = "$weather_condition", fontSize = 16.sp)

                    }
                }
            }
        }
    }
}


@Composable
fun ImageBackground(weatherDescription: String) {
    val normalizedDescription = weatherDescription.trim()



    // Log to check the exact weather description
    Log.d("WeatherApp", "Normalized weather description: $normalizedDescription")

    val imageUrl = when {
        normalizedDescription.contains("Clear") -> "https://media.istockphoto.com/id/491701259/photo/blue-sky-with-sun.jpg?s=612x612&w=0&k=20&c=aB7c-e0YFezBb8cgSykiEcAh_2fXEie3inIudnsNa9g="
        normalizedDescription.contains("Clouds") -> "https://img.freepik.com/free-photo/black-rain-abstract-dark-power_1127-2380.jpg"
        normalizedDescription.contains("Drizzle") -> "https://thumbs.dreamstime.com/b/heavy-rain-outside-created-artificial-intelligence-320885240.jpg"
        normalizedDescription.contains("Rain") -> "https://static9.depositphotos.com/1408467/1141/v/450/depositphotos_11416086-stock-illustration-a-rainy-day.jpg"
        normalizedDescription.contains("Thunderstorm") -> "https://media.istockphoto.com/id/517643357/photo/thunderstorm-lightning-with-dark-cloudy-sky.jpg?s=612x612&w=0&k=20&c=x3G3UijRPVGFMFExnlYGbnQtnlH6-oUoMU48BTkc0Os="
        normalizedDescription.contains("Snow") -> "https://t3.ftcdn.net/jpg/02/99/62/78/360_F_299627872_qCUz3CSSoS7fjB8b6RLXYzX2YIj28qjo.jpg"
        normalizedDescription.contains("Mist") -> "https://media.istockphoto.com/id/1200158590/photo/majestic-countryside-at-sunrise-in-wintertime.jpg?s=612x612&w=0&k=20&c=iIEmwbu3q0d16VHYBz-JyZoJRkzPIctzeDgPPTUlaB0="
        normalizedDescription.contains("Fog") -> "https://images.pexels.com/photos/167699/pexels-photo-167699.jpeg?cs=srgb&dl=pexels-lum3n-44775-167699.jpg&fm=jpg"

        else -> "" // Default fallback if no match is found
    }

    // If the image URL is empty, it means no condition matched, so you can set a default image or color.
    if (imageUrl.isNotEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(-1f)
        ) {
            Image(
                painter = rememberAsyncImagePainter(imageUrl),
                contentDescription = "Weather Background",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop  // Ensure the image stretches across the screen
            )
        }
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        )
    }
}

@Composable
fun WeatherDetailsPlaceholder() {
    val weatherImage = "https://cdn-icons-png.flaticon.com/512/4716/4716524.png"
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "City: ---", fontSize = 30.sp)
        Spacer(modifier = Modifier.height(10.dp))
        Image(
            painter = rememberImagePainter(weatherImage), // Use a placeholder image
            contentDescription = "Weather Icon",
            modifier = Modifier.size(100.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = "Temperature: --- °C", fontSize = 30.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = "Weather: ---", fontSize = 30.sp)
        Spacer(modifier = Modifier.height(40.dp))
        Row {
            Text(text = "High: --- °C", fontSize = 20.sp)
            Spacer(modifier = Modifier.width(40.dp))
            Text(text = "Low: --- °C", fontSize = 20.sp)
        }
    }
}

@Composable
fun ForecastDetailsPlaceholder() {
    val weatherImage = "https://cdn-icons-png.flaticon.com/512/4716/4716524.png"
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Header Row
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(imageVector = Icons.Default.List, contentDescription = "Date Icon")
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Weather Forecast",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Placeholder for forecast items (dashes)
        repeat(5) {
            Card(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.Gray.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    Text(text = "---", fontSize = 16.sp)
                    Image(
                        painter = rememberImagePainter(weatherImage), // Use a placeholder image
                        contentDescription = "Weather Icon",
                        modifier = Modifier.size(40.dp)
                    )
                    Text(text = "--- °C", fontSize = 16.sp)
                    Text(text = "---", fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
fun GradientBackground() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF7D97E6), // Start color
                        Color(0xFFCEA58F)  // End color
                    )
                )
            )
    )
}

fun celsiusToFahrenheit(celsius: Double): Double {
    val fahrenheit = (celsius * 9 / 5) + 32
    return String.format("%.2f", fahrenheit).toDouble()
}

@Composable
fun FavoriteIcon(cityName: String, onClick: () -> Unit, isFavorite: Boolean) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
            contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
            tint = if (isFavorite) Color.Red else Color.White
        )
    }
}

// Favorite City Item
@Composable
fun FavoriteCityItem(cityName: String, onRemove: () -> Unit, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = cityName, fontSize = 18.sp, fontWeight = FontWeight.Medium)
        Row {
            IconButton(onClick = onClick) {
                Icon(imageVector = Icons.Default.LocationOn, contentDescription = "Fetch Weather for $cityName")
            }
            IconButton(onClick = onRemove) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Remove from favorites")
            }
        }
    }
}


