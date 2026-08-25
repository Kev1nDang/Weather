# Weather App

![Java](https://img.shields.io/badge/backend-Java%2017-orange)
![React](https://img.shields.io/badge/frontend-React%2018-61DAFB)
![License](https://img.shields.io/badge/license-MIT-blue)

A simple full-stack weather app. Type in a city and state, get the current conditions back — built to practice tying a Java backend to a React frontend over a REST API.

<!-- Add a screenshot or GIF of the app here, e.g. ![Weather App](docs/screenshot.png) -->

## What it does

- Look up current weather by city and state
- Shows temperature, "feels like" temperature, conditions, humidity, and wind speed
- Handles typos/invalid city-state combos without crashing
- Backend and frontend are fully separate — the API can be used on its own, or swapped for a different UI

## Tech Stack

| Layer    | Tech                                                   |
| -------- | ------------------------------------------------------- |
| Backend  | Java 17, built-in `com.sun.net.httpserver` HTTP server, [org.json](https://github.com/stleary/JSON-java), Maven |
| Frontend | React 18, Vite                                          |
| Data     | [OpenWeatherMap API](https://openweathermap.org/api)     |

## Architecture

```
React frontend (Vite dev server)
        │  GET /api/weather?city=..&state=..
        ▼
Java backend (WeatherServer, port 8080)
        │
        ▼
OpenWeatherMap API (geocoding + current weather)
```

## Getting Started

### Prerequisites

- JDK 17+ and Maven
- Node.js 18+
- An [OpenWeatherMap API key](https://openweathermap.org/appid) (free tier works)

### 1. Backend

```bash
cd backend
export OPENWEATHER_API_KEY=your_api_key_here   # Windows: set OPENWEATHER_API_KEY=your_api_key_here
mvn compile exec:java
```

The API starts on `http://localhost:8080`. Try it directly:

```
GET http://localhost:8080/api/weather?city=New York&state=NY
```

### 2. Frontend

```bash
cd frontend
npm install
npm run dev
```

Open the printed local URL (defaults to `http://localhost:5173`). The frontend talks to the backend at `http://localhost:8080` by default — override this by copying `.env.example` to `.env` and setting `VITE_API_BASE_URL`.

## Testing

```bash
cd backend
mvn test
```

## License

This project is licensed under the MIT License - see the LICENSE file for details.
