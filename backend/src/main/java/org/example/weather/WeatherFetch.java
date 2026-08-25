package org.example.weather;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

/**
 * Fetches current weather data from the OpenWeatherMap API. Requires the
 * OPENWEATHER_API_KEY environment variable to be set.
 */
public class WeatherFetch {

    private static final String GEOCODE_URL = "https://api.openweathermap.org/geo/1.0/direct";
    private static final String WEATHER_URL = "https://api.openweathermap.org/data/2.5/weather";

    private static final HttpClient CLIENT = HttpClient.newHttpClient();

    private WeatherFetch() {
    }

    private static String apiKey() {
        String key = System.getenv("OPENWEATHER_API_KEY");
        if (key == null || key.isEmpty()) {
            throw new IllegalStateException("OPENWEATHER_API_KEY environment variable is not set");
        }
        return key;
    }

    private static JSONObject get(String url) throws IOException {
        try {
            HttpRequest request = HttpRequest.newBuilder(URI.create(url)).GET().build();
            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 404) {
                return new JSONObject();
            }
            String body = response.body();
            if (body != null && body.trim().startsWith("[")) {
                JSONArray array = new JSONArray(body);
                return array.isEmpty() ? new JSONObject() : array.getJSONObject(0);
            }
            return new JSONObject(body);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Weather request was interrupted", e);
        }
    }

    /** Fetches current weather for a coordinate pair, in metric units. */
    public static JSONObject fetchWeather(double lat, double lon) throws IOException {
        String url = String.format(
                "%s?lat=%s&lon=%s&units=metric&appid=%s", WEATHER_URL, lat, lon, apiKey());
        JSONObject data = get(url);
        if (data.isEmpty()) {
            return data;
        }
        // Normalize lat/lon onto the top level so callers don't need to know
        // about the upstream API's nested "coord" shape.
        data.put("lat", lat);
        data.put("lon", lon);
        return data;
    }

    /**
     * Resolves a city/state combination to a location JSON object containing
     * "lat" and "lon". Returns an empty JSONObject if the city/state can't be
     * resolved.
     */
    public static JSONObject convertCityStateToLatLon(String city, String state) throws IOException {
        String query = URLEncoder.encode(city + "," + state + ",US", StandardCharsets.UTF_8);
        String url = String.format("%s?q=%s&limit=1&appid=%s", GEOCODE_URL, query, apiKey());
        return get(url);
    }

    /**
     * Fetches current weather for a city/state combination. Returns an empty
     * JSONObject if the city/state can't be resolved to a location.
     */
    public static JSONObject fetchWeatherByCity(String city, String state) throws IOException {
        JSONObject location = convertCityStateToLatLon(city, state);
        if (location.isEmpty()) {
            return location;
        }
        return fetchWeather(location.getDouble("lat"), location.getDouble("lon"));
    }
}
