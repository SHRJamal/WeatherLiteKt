package jamdevinc.weather.data

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query


@Dao
interface CurrentDao {

    @Query("SELECT * FROM current_table WHERE cityName LIKE :city" )
    fun queryCityCurrentWeather(city: String) : LiveData<CurrentWeather>

    @Insert(onConflict = REPLACE)
    fun insertCityCurrentWeather(currentWeather: CurrentWeather)

    @Delete
    fun deleteCityCurrentWeather(currentWeather: CurrentWeather)
}

@Dao
interface HourlyDao {

    @Query("SELECT * FROM hourly_table WHERE cityName LIKE :city" )
    fun queryCityHourlyWeather(city: String) : LiveData<List<HourlyForecast>>

    @Insert(onConflict = REPLACE)
    fun insertCityHourlyWeather(hourlyForecast: HourlyForecast)

    @Delete
    fun deleteCityHourlyWeather(hourlyForecast: HourlyForecast)
}
@Dao
interface DailyDao {

    @Query("SELECT * FROM daily_table WHERE cityName LIKE :city" )
    fun queryCityDailyWeather(city: String) : LiveData<List<DailyForecast>>

    @Insert(onConflict = REPLACE)
    fun insertCityDailyWeather(dailyForecast: DailyForecast)

    @Delete
    fun deleteCityDailyWeather(dailyForecast: DailyForecast)
}

@Dao
interface LocationDao {

    @Query("SELECT * FROM locations_table" )
    fun queryAllLocationsExist() : LiveData<List<Location>>

    @Query("SELECT * FROM locations_table WHERE cityName LIKE :city" )
    fun queryLocationInfo(city: String) : Location

    @Insert(onConflict = REPLACE)
    fun insertLocation(location: Location)

    @Delete
    fun deleteLocation(location: Location)
}