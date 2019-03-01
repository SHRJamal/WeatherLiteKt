package jamdevinc.weather.data

import android.app.Application
import android.arch.lifecycle.LiveData
import android.os.Handler
import android.os.HandlerThread


class DataRepository private constructor(application: Application) {

    private var mDatabase: AppDatabase? = null

    private val mCurrentDAO: CurrentDao?
    private val mHourlyDAO: HourlyDao?
    private val mDailyDAO: DailyDao?
    private val mLocationDAO : LocationDao?

    fun getCurrentData(location: String): LiveData<CurrentWeather>?
            =mCurrentDAO?.queryCityCurrentWeather(location)


    fun getHourlyData(location: String): LiveData<List<HourlyForecast>>?
            =mHourlyDAO?.queryCityHourlyWeather(location)


    fun getDailyData(location: String): LiveData<List<DailyForecast>>?
            = mDailyDAO?.queryCityDailyWeather(location)

    fun getAllLocations(): LiveData<List<Location>>?
            =  mLocationDAO?.queryAllLocationsExist()

    companion object {
        fun getInstance(application: Application): DataRepository {
            var sInstance : DataRepository? = null
            if (sInstance == null) {
                synchronized(DataRepository::class.java) {
                    if (sInstance == null) {
                        sInstance = DataRepository(application)
                    }
                }
            }
            return sInstance!!
        }
    }

    init {
        mDatabase  = AppDatabase.getInstance(application)

        mCurrentDAO = mDatabase?.currentWeatherDAO()
        mHourlyDAO = mDatabase?.hourlyWeatherDAO()
        mDailyDAO = mDatabase?.dailyWeatherDAO()
        mLocationDAO = mDatabase?.locationDAO()
    }
}

class RepoHandlerThread (threadName : String) : HandlerThread(threadName){

    private lateinit var handler : Handler
    override fun onLooperPrepared() {
        super.onLooperPrepared()
        handler = Handler(looper)
    }

    fun postTask(runnable: Runnable){
        handler.post(runnable)
    }

}