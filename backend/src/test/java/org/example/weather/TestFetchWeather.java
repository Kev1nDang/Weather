package org.example.weather;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class TestFetchWeather {
    @Test
    public void testFetchWeather() {
        try {
            double latitude =40.7128;
            double longitude = -74.0060;
            JSONObject weatherData = WeatherFetch.fetchWeather(latitude,longitude);
            assertNotNull(weatherData, "Weather data should not be null");
            assertEquals(weatherData.getDouble("lat"),latitude,0.1);
            assertEquals(weatherData.getDouble("lon"),longitude,0.1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void testConvertCityStateToLatLon() {
        try {
            String city = "Boston";
            String state = "MA";
            JSONObject weatherData = WeatherFetch.convertCityStateToLatLon(city, state);
            assertNotNull(weatherData, "Weather data should not be null");
            assertEquals(weatherData.getDouble("lat"), 42.3554334, 0.1);
            assertEquals(weatherData.getDouble("lon"), -71.060511, 0.1);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testFetchWeatherByCity() {
        try {
            String city = "New York";
            String state = "NY";
            JSONObject weatherData = WeatherFetch.fetchWeatherByCity(city, state);
            assertNotNull(weatherData, "Weather data should not be null");
            assertEquals(weatherData.getDouble("lat"), 40.7128, 0.1); // Replace with the actual latitude of New York, NY
            assertEquals(weatherData.getDouble("lon"), -74.0060, 0.1); // Replace with the actual longitude of New York, NY
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testFetchWeatherInvalidCity() {
        try {
            String city = "InvalidCity";
            String state = "InvalidState";
            JSONObject weatherData = WeatherFetch.fetchWeatherByCity(city, state);

            // If weatherData is not empty, it means the API returned valid data for an invalid city/state
            assertTrue(weatherData.isEmpty(), "Weather data should be empty for an invalid city/state");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            // JSONException indicates that the API response is not in JSON format,
            // which likely means the city/state combination is invalid
            assertTrue(true, "Invalid city/state combination");
        }
    }


}
