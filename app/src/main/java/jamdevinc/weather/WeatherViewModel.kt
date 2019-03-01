package jamdevinc.weather

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import jamdevinc.weather.data.*

class WeatherViewModel(private val app: Application, location: String) : AndroidViewModel(app) {

    val dataRepository : DataRepository

    private val mCurrentLiveData : LiveData<CurrentWeather>?

    private val mHourlyLiveData : LiveData<List<HourlyForecast>>?

    private val mDailyLiveData : LiveData<List<DailyForecast>>?

    private val mLocationsLiveData : LiveData<List<Location>>?

    init {
        dataRepository = DataRepository.getInstance(app)

        mCurrentLiveData = dataRepository.getCurrentData(location)
        mHourlyLiveData = dataRepository.getHourlyData(location)
        mDailyLiveData = dataRepository.getDailyData(location)
        mLocationsLiveData = dataRepository.getAllLocations()
    }

    val currentWeather : LiveData<CurrentWeather>?
        get() = mCurrentLiveData
    val hourlyForecast : LiveData<List<HourlyForecast>>?
        get() = mHourlyLiveData
    val dailyForecast : LiveData<List<DailyForecast>>?
        get() = mDailyLiveData


}