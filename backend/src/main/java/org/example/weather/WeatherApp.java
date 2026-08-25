package org.example.weather;

import java.io.IOException;

/**
 * Entry point for the Weather App backend. Starts the HTTP API that the
 * React frontend calls to fetch weather data.
 */
public class WeatherApp {
    public static void main(String[] args) throws IOException {
        String portEnv = System.getenv("PORT");
        int port = portEnv != null ? Integer.parseInt(portEnv) : 8080;
        new WeatherServer(port).start();
    }
}
