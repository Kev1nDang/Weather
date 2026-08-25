package org.example.weather;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Minimal HTTP server exposing weather lookups as a JSON REST API for the
 * React frontend. Uses only the JDK's built-in HTTP server, no framework.
 */
public class WeatherServer {

    private final HttpServer server;

    public WeatherServer(int port) throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/api/weather", new WeatherHandler());
    }

    public void start() {
        server.start();
        System.out.println("Weather API listening on http://localhost:" + server.getAddress().getPort());
    }

    private static class WeatherHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Content-Type", "application/json");

            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, OPTIONS");
                exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            Map<String, String> params = parseQuery(exchange.getRequestURI().getRawQuery());
            String city = params.get("city");
            String state = params.get("state");

            if (city == null || city.isBlank() || state == null || state.isBlank()) {
                writeResponse(exchange, 400, new JSONObject().put("error", "city and state query parameters are required"));
                return;
            }

            try {
                JSONObject weather = WeatherFetch.fetchWeatherByCity(city, state);
                if (weather.isEmpty()) {
                    writeResponse(exchange, 404, new JSONObject().put("error", "Could not find weather for " + city + ", " + state));
                } else {
                    writeResponse(exchange, 200, weather);
                }
            } catch (Exception e) {
                writeResponse(exchange, 502, new JSONObject().put("error", "Failed to fetch weather: " + e.getMessage()));
            }
        }

        private Map<String, String> parseQuery(String rawQuery) {
            Map<String, String> params = new HashMap<>();
            if (rawQuery == null || rawQuery.isEmpty()) {
                return params;
            }
            for (String pair : rawQuery.split("&")) {
                String[] kv = pair.split("=", 2);
                String key = URLDecoder.decode(kv[0], StandardCharsets.UTF_8);
                String value = kv.length > 1 ? URLDecoder.decode(kv[1], StandardCharsets.UTF_8) : "";
                params.put(key, value);
            }
            return params;
        }

        private void writeResponse(HttpExchange exchange, int status, JSONObject body) throws IOException {
            byte[] bytes = body.toString().getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(status, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }
    }
}
