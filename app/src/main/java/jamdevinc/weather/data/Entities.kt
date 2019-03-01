package jamdevinc.weather.data

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import org.jetbrains.annotations.NotNull

@Entity(tableName = "current_table")
data class CurrentWeather (
        @NotNull var cityId:Long,
        @NotNull var cityName:String,
        @NotNull var temp: Double,
        @NotNull var weatherID:Int,
        @NotNull var weatherDescription:Int,
        @NotNull var weatherIcon: Int,
        @NotNull var timeZone: String,
        @NotNull var sunrise: String,
        @NotNull var sunset: String,
        @NotNull var humidity:Double,
        @NotNull var pressure:Double,
        @NotNull var windSpeed:Double,
        @NotNull var windDirection:String,
        @PrimaryKey(autoGenerate = true) var id: Int = -1
        )

@Entity(tableName = "hourly_table")
data class HourlyForecast(
        @NotNull var cityId:Long,
        @NotNull var cityName:String,
        @NotNull var date: Long,
        @NotNull var weatherIcon: Int,
        @NotNull var temp: Double,
        @PrimaryKey(autoGenerate = true) var id: Int = -1
        )


@Entity(tableName = "daily_table")
data class DailyForecast(
        @NotNull var cityId:Long,
        @NotNull var cityName:String,
        @NotNull var date: Long,
        @NotNull var weatherIcon: Int,
        @NotNull var maxTemp: Double,
        @NotNull var minTemp: Double,
        @PrimaryKey(autoGenerate = true) var id: Int =-1
        )

@Entity(tableName = "locations_table")
data class Location(
        @PrimaryKey var cityId:Long,
        @NotNull var cityName:String,
        @NotNull var maxTemp: String,
        @NotNull var minTemp: String)