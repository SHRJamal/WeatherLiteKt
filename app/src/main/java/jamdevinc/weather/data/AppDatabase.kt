package jamdevinc.weather.data

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context

@Database(entities = [(CurrentWeather::class),
    (HourlyForecast::class),
    (DailyForecast::class),
    (Location::class)],
        version = 1)

abstract class AppDatabase : RoomDatabase() {

    abstract fun currentWeatherDAO() : CurrentDao
    abstract fun hourlyWeatherDAO() : HourlyDao
    abstract fun dailyWeatherDAO() : DailyDao
    abstract fun locationDAO() : LocationDao


    companion object {
        private const val DATABASE_NAME: String = "weather_database.db"
        private var instance : AppDatabase? = null

        fun getInstance(context: Context): AppDatabase? {
            if (instance == null) {
                synchronized(AppDatabase::class.java) {
                    if (instance == null)
                    instance = Room.databaseBuilder(context.applicationContext,AppDatabase::class.java, DATABASE_NAME)
                            .build()
                }
            }
            return instance
        }
        fun destroyInstance() {
            instance = null
        }
    }
}