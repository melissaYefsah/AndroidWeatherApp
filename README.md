# Weather App

## Introduction

Welcome to **Weather App**! This is a mobile application built using **Jetpack Compose** for Android.
It provides real-time weather data, including current weather, forecasts, and more.
Users can search for cities or use their current location to get accurate weather information.
The app also includes a **Favorites** feature, where users can save their favorite locations and quickly view weather updates for those locations.

## Features

- **Current Weather**: Displays the current weather of a location, including temperature, humidity, wind speed, and more.
- **Forecast**: Shows the weather forecast for the next few days, including temperature and conditions.
- **Search Functionality**: Allows users to search for weather data based on city names.
- **Current Location**: Fetches the current location using GPS and displays weather data based on it.
- **Favorites**: Users can add cities to their favorites for quick access to their weather details. If a city is already in the favorites list, the app shows a toast notification.

## Technologies Used

- **Jetpack Compose**: For UI building in a declarative way.
- **Kotlin**: The primary programming language.
- **Coil**: For loading images from URLs (for weather icons).
- **Retrofit**: For making network calls to the weather API.
- **Google Play Services**: To get the user's current location.

## Setup Instructions

### Prerequisites

- **Android Studio**: Ensure you have the latest version of Android Studio installed.
- **Android SDK**: Make sure your environment is set up to compile Android projects.

### 1. Clone the Repository

Clone this repository to your local machine:

bash
git clone https://github.com/melissaYefsah/AndroidWeatherApp

### 2. Set Up Dependencies
  ./gradlew build
### 3. Set Up Google Play Services for Location
To use the Current Location feature (for GPS-based weather fetching), you need to have Google Play Services integrated into the project.

Open the build.gradle (Project-level) file and ensure the following line is added to the dependencies block:
classpath 'com.google.gms:google-services:4.3.15'
Then, in your build.gradle (App-level) file, add the required dependency:
implementation 'com.google.android.gms:play-services-location:18.0.0'
Make sure your app has the necessary permissions for accessing location data. Add the following permissions in the AndroidManifest.xml:
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
Once these steps are complete, you're ready to run the app.
### 4. Run the Application
Android Studio Emulator: Click the Run button in Android Studio and select an emulator to run the app.

## Usage

1. Searching for a City
Open the app and type a city name into the search bar.
Tap the Search button to fetch the weather data for the entered city.
The app will display the current weather along with the forecast for the next few days.
2. Using Current Location
Tap the Use Current Location button, and the app will automatically detect your current location using GPS.
The weather details and forecast for your location will be displayed.
3. Managing Favorite Cities
While viewing the weather for a city, you can add it to your Favorites by tapping the heart icon next to the city name.
To remove a city from your favorites, simply tap the remove icon next to the city in your favorites list.
If a city is already in your favorites list and you try to add it again, a toast notification will inform you that the city is already a favorite.
4. Toggle Between Celsius and Fahrenheit
The app allows you to toggle between Celsius and Fahrenheit for temperature readings.
Tap the temperature unit to switch between the two units.

## Contact

For any questions or feedback, feel free to reach out to the maintainer:

Author: Melissa Yefsah
GitHub: https://github.com/melissaYefsah/AndroidWeatherApp
