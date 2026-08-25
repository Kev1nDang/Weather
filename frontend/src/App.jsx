import { useState } from 'react'

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'

export default function App() {
  const [city, setCity] = useState('')
  const [state, setState] = useState('')
  const [weather, setWeather] = useState(null)
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  async function handleSubmit(event) {
    event.preventDefault()
    if (!city.trim() || !state.trim()) {
      setError('Please enter both a city and a state.')
      return
    }

    setLoading(true)
    setError('')
    setWeather(null)

    try {
      const params = new URLSearchParams({ city, state })
      const response = await fetch(`${API_BASE_URL}/api/weather?${params}`)
      const data = await response.json()

      if (!response.ok) {
        throw new Error(data.error || 'Could not fetch weather for that location.')
      }

      setWeather(data)
    } catch (err) {
      setError(err.message || 'Something went wrong. Please try again.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="app">
      <div className="card">
        <h1>Weather App</h1>
        <p className="subtitle">Check the current weather by city and state</p>

        <form onSubmit={handleSubmit} className="form">
          <input
            type="text"
            placeholder="City (e.g. New York)"
            value={city}
            onChange={(e) => setCity(e.target.value)}
          />
          <input
            type="text"
            placeholder="State (e.g. NY)"
            value={state}
            onChange={(e) => setState(e.target.value)}
          />
          <button type="submit" disabled={loading}>
            {loading ? 'Fetching…' : 'Get Weather'}
          </button>
        </form>

        {error && <p className="error">{error}</p>}

        {weather && (
          <div className="result">
            <h2>{weather.name}</h2>
            {weather.weather?.[0] && (
              <p className="description">{weather.weather[0].description}</p>
            )}
            <p className="temp">{Math.round(weather.main.temp)}°C</p>
            <div className="details">
              <span>Feels like {Math.round(weather.main.feels_like)}°C</span>
              <span>Humidity {weather.main.humidity}%</span>
              <span>Wind {weather.wind.speed} m/s</span>
            </div>
          </div>
        )}
      </div>
    </div>
  )
}
