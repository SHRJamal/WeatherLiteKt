package jamdevinc.weather.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TableRow
import android.widget.TextView
import com.google.android.gms.ads.AdRequest
import jamdevinc.weather.R
import jamdevinc.weather.WeatherViewModel
import jamdevinc.weather.data.CurrentWeather
import jamdevinc.weather.data.DailyForecast
import jamdevinc.weather.utilities.LiteUtilities
import kotlinx.android.synthetic.main.fragment_hourly_forecast.*
import kotlinx.android.synthetic.main.fragment_main.*
import java.util.*
import java.util.concurrent.TimeUnit


class MainFragment : Fragment() {

    private lateinit var viewModel: WeatherViewModel

    private lateinit var location: String

    private lateinit var preferences: SharedPreferences

    private var hardColors = arrayOf("#D32F2F", "#C2185B", "#7B1FA2", "#1976D2", "#0097A7", "#0288D1", "#388E3C", "#FBC02D", "#F57C00", "#455A64", "#E64A19", "#FFA000")
    private var softColors = arrayOf("#F44336", "#E91E63", "#9C27B0", "#2196F3", "#00BCD4", "#03A9F4", "#4CAF50", "#FFEB3B", "#FF9800", "#607D8B", "#FF5722", "#FFC107")

    interface OnSwipeToRefreshListener {
        fun onSwipeToRefresh(refreshLayout: SwipeRefreshLayout)
    }

    private lateinit var _onSwipeToRefreshListener : OnSwipeToRefreshListener


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        location = arguments?.getString(ARG_OBJECT) ?: ""
        if (location == "") return null

        val view = inflater?.inflate(R.layout.fragment_main, container, false)

        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        recycler.layoutManager = linearLayoutManager
        recycler.setHasFixedSize(true)
        recycler.setItemViewCacheSize(48)

        preferences = PreferenceManager.getDefaultSharedPreferences(context)

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(WeatherViewModel::class.java)
        viewModel.currentWeather?.observe(this, Observer<CurrentWeather> {
            displayCurrentWeather(it)
        })
        viewModel.dailyForecast?.observe(this,Observer<List<DailyForecast>> {
            displayDailyForecast(it)
        })

    }
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (view == null) return

        refreshLayout.setOnRefreshListener {
            val randi = Random().nextInt(softColors.size)
            preferences.edit().putInt(LiteUtilities.SOFT_COLOR_PREF_KEY, Color.parseColor(softColors[randi])).apply()
            preferences.edit().putInt(LiteUtilities.HARD_COLOR_PREF_KEY, Color.parseColor(hardColors[randi])).apply()

            _onSwipeToRefreshListener.onSwipeToRefresh(refreshLayout)
        }

        adView2.loadAd(AdRequest.Builder().build())
        setupComponentColor(preferences.getInt(LiteUtilities.HARD_COLOR_PREF_KEY,
                resources.getColor(R.color.blueDarkBackground)))

    }

    private fun setupComponentColor(hardColor: Int) {
        recycler.setBackgroundColor(hardColor)
        sunriseCard.setBackgroundColor(hardColor)
        pressureCard.setBackgroundColor(hardColor)
        humidityCard.setBackgroundColor(hardColor)
        windCard.setBackgroundColor(hardColor)
    }
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        _onSwipeToRefreshListener = context as OnSwipeToRefreshListener

    }


    private fun displayCurrentWeather(currentWeather: CurrentWeather?) {
        if (currentWeather == null) return
        cityTv.text = location
        tempTv.text = LiteUtilities.formatTemperature(preferences, currentWeather.temp)

        descriptionTv.text = context.getString(currentWeather.weatherDescription)

        val tz = TimeZone.getTimeZone(currentWeather.timeZone)
        val offset = TimeUnit.MILLISECONDS.toSeconds(tz.getOffset(System.currentTimeMillis()).toLong())

        sunriseTv.text = LiteUtilities.formatHour(offset + LiteUtilities.getDateFromHourlyString(currentWeather.sunrise))
        sunsetTv.text = LiteUtilities.formatHour(offset + LiteUtilities.getDateFromHourlyString(currentWeather.sunset))

        windTv.text = LiteUtilities.formatWind(context,currentWeather.windDirection, currentWeather.windSpeed)

        pressureTv.text = LiteUtilities.formatPressure(context, preferences, currentWeather.pressure)

        humidityTv.text = LiteUtilities.formatHumidity(currentWeather.humidity)
    }
    private fun displayDailyForecast(dailyForecastList: List<DailyForecast>?) {
        if (dailyForecastList == null) return
        for (i in 0 until dailyTable.childCount-1) {
            val tableRow = dailyTable.getChildAt(i) as TableRow

            val dayTv = tableRow.getChildAt(0) as TextView
            val date = LiteUtilities.getLocalDateFromUTC(dailyForecastList[i].date)
            dayTv.text = LiteUtilities.getDayName(date)

            val weatherIcon = tableRow.getChildAt(1) as ImageView
            weatherIcon.setImageResource(dailyForecastList[i].weatherIcon)

            val maxTempTv = tableRow.getChildAt(2) as TextView
            maxTempTv.text = LiteUtilities.formatTemperature(preferences, dailyForecastList[i].maxTemp)

            val minTempTv = tableRow.getChildAt(3) as TextView
            minTempTv.text = LiteUtilities.formatTemperature(preferences, dailyForecastList[i].minTemp)
        }
    }

    companion object {
        private val LOG_TAG = MainFragment::class.java.simpleName
        const val ARG_OBJECT = "object"
        fun instantiate(location: String): MainFragment {
            val mainFragment = MainFragment()
            val args = Bundle()
            args.putString(ARG_OBJECT, location)
            mainFragment.arguments = args
            return mainFragment
        }
    }

}

