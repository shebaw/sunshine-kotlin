package com.example.shebaw.sunshinekotlin.data

class WeatherContract {
    class  WeatherEntry  {
        companion object {
            const val TABLE_NAME = "weather"
            const val COLUMN_DATE = "date"
            const val COLUMN_WEATHER_ID = "weather_id"
            const val COLUMN_MIN_TEMP = "min"
            const val COLUMN_MAX_TEMP = "max"
            const val COLUMN_PRESSURE = "humidity"
            const val COLUMN_WIND_SPEED = "wind"
            const val COLUMN_DEGREES = "degrees"
        }
    }
}
