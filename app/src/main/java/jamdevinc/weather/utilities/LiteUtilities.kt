package jamdevinc.weather.utilities

import android.app.ProgressDialog
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import jamdevinc.weather.R
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

object LiteUtilities {

    const val LAST_SYNC_TIME = "last_sync_time"
    const val LOCATIONS_KEY = "locations"
    const val IS_TEMP_FAH_UNIT = "temp_unit"
    const val DEFAULT_CITY_NAME_KEY = "default_city_name"
    const val DEFAULT_CITY_LAT_KEY = "default_city_lat"
    const val DEFAULT_CITY_LON_KEY = "default_city_lon"
    const val SOFT_COLOR_PREF_KEY = "soft_color_key"
    const val HARD_COLOR_PREF_KEY = "hard_color_key"


    private val DAY_IN_SECONDS = TimeUnit.DAYS.toSeconds(1)
    val SYSTEM_CURRENT_TIME = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())



    fun formatTemperature(preferences: SharedPreferences, temperature: Double) :String{

        return if (preferences.getBoolean(LiteUtilities.IS_TEMP_FAH_UNIT, false))
                (temperature * 1.8 + 32).toInt().toString() + "°"
         else
                temperature.toInt().toString() + "°"
    }
    fun formatWind(context: Context, windDirc: String, speed: Double): String {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)

        return if (preferences.getString(context.getString(R.string.prefs_wind_key), "") ==
                context.getString(R.string.pref_mph_key)) {
            windDirc + "  " + (speed * 0.621371).toInt() + " Mph"
        }
        else windDirc + "  " + (speed * 3.6).toInt() + " Km/h"


    }
    fun formatPressure(context: Context, preferences: SharedPreferences, pressure: Double) :String{
        return if (preferences.getString(context.getString(R.string.prefs_pressure_key), "")
                == context.getString(R.string.pref_hpa_key))

            pressure.toInt().toString() + " HPa"

        else
            (pressure * 0.01).toInt().toString() + " Mb"

    }
    fun formatHumidity(humidity: Double) = humidity.toInt().toString() + "%"
    fun formatHour(date: Long) :String = SimpleDateFormat("HH:mm").format(TimeUnit.SECONDS.toMillis(date))

    fun getDayName(date: Long) :String = SimpleDateFormat("EEEE").format(TimeUnit.SECONDS.toMillis(date))

    fun getWeatherObject(weatherId: Int): Pair<Int,Int> {
        val stringId: Int
        val iconResource: Int
        when (weatherId) {
            in 200..232 -> {
                stringId = R.string.condition_2xx
                iconResource = R.drawable.t01d
            }
            in 300..321 -> {
                stringId = R.string.condition_3xx
                iconResource = R.drawable.d01d
            }
            500 -> {
                stringId = R.string.condition_500
                iconResource = R.drawable.r01d
            }
            501 -> {
                stringId = R.string.condition_501
                iconResource = R.drawable.r01d
            }
            502 -> {
                stringId = R.string.condition_502
                iconResource = R.drawable.r03d
            }
            503 -> {
                stringId = R.string.condition_503
                iconResource = R.drawable.r03d
            }
            504 -> {
                stringId = R.string.condition_504
                iconResource = R.drawable.r03d
            }
            511 -> {
                stringId = R.string.condition_511
                iconResource = R.drawable.f01d
            }
            520 -> {
                stringId = R.string.condition_520
                iconResource = R.drawable.r01d
            }
            521 -> {
                stringId = R.string.condition_521
                iconResource = R.drawable.r05d
            }
            522 -> {
                stringId = R.string.condition_522
                iconResource = R.drawable.r01d
            }
            531 -> {
                stringId = R.string.condition_531
                iconResource = R.drawable.r01d
            }
            600 -> {
                stringId = R.string.condition_600
                iconResource = R.drawable.s01d
            }
            601 -> {
                stringId = R.string.condition_601
                iconResource = R.drawable.s02d
            }
            602 -> {
                stringId = R.string.condition_602
                iconResource = R.drawable.s02d
            }
            610 -> {
                stringId = R.string.condition_615
                iconResource = R.drawable.s04d
            }
            611 -> {
                stringId = R.string.condition_611
                iconResource = R.drawable.s05d
            }
            612 -> {
                stringId = R.string.condition_612
                iconResource = R.drawable.s05d
            }
            615 -> {
                stringId = R.string.condition_615
                iconResource = R.drawable.s04d
            }
            616 -> {
                stringId = R.string.condition_616
                iconResource = R.drawable.s04d
            }
            620 -> {
                stringId = R.string.condition_620
                iconResource = R.drawable.s01d
            }
            621 -> {
                stringId = R.string.condition_621
                iconResource = R.drawable.s01d
            }
            622 -> {
                stringId = R.string.condition_622
                iconResource = R.drawable.s01d
            }
            623 -> {
                stringId = R.string.condition_622
                iconResource = R.drawable.s01d
            }
            700 -> {
                stringId = R.string.condition_701
                iconResource = R.drawable.a01d
            }
            701 -> {
                stringId = R.string.condition_701
                iconResource = R.drawable.a01d
            }
            711 -> {
                stringId = R.string.condition_711
                iconResource = R.drawable.a01d
            }
            721 -> {
                stringId = R.string.condition_721
                iconResource = R.drawable.a01d
            }
            731 -> {
                stringId = R.string.condition_731
                iconResource = R.drawable.a01d
            }
            741 -> {
                stringId = R.string.condition_741
                iconResource = R.drawable.a01d
            }
            751 -> {
                stringId = R.string.condition_751
                iconResource = R.drawable.a01d
            }
            761 -> {
                stringId = R.string.condition_761
                iconResource = R.drawable.a01d
            }
            762 -> {
                stringId = R.string.condition_762
                iconResource = R.drawable.a01d
            }
            771 -> {
                stringId = R.string.condition_771
                iconResource = R.drawable.a01d
            }
            781 -> {
                stringId = R.string.condition_781
                iconResource = R.drawable.a01d
            }
            800 -> {
                stringId = R.string.condition_800
                iconResource = R.drawable.c01d
            }
            801 -> {
                stringId = R.string.condition_801
                iconResource = R.drawable.c02d
            }
            802 -> {
                stringId = R.string.condition_802
                iconResource = R.drawable.c02d
            }
            803 -> {
                stringId = R.string.condition_803
                iconResource = R.drawable.c02d
            }
            804 -> {
                stringId = R.string.condition_804
                iconResource = R.drawable.c04d
            }
            else -> {
                stringId = R.string.condition_unknown
                iconResource = R.drawable.u00d
            }

        }
        return Pair(stringId, iconResource)
    }

    fun getLocalDateFromUTC(utcDate: Long): Long {
        val tz = TimeZone.getDefault()
        val gmtOffset = tz.getOffset(utcDate).toLong()
        return utcDate + TimeUnit.MILLISECONDS.toSeconds(gmtOffset)
    }

    @Throws(ParseException::class)
    fun getDateFromHourlyString(hourlyString: String): Long {
        val date = SimpleDateFormat("HH:mm").parse(hourlyString)
        return TimeUnit.MILLISECONDS.toSeconds(date.time)

    }

    fun buildProgressDialog(context: Context): ProgressDialog {
        val progressDialog = ProgressDialog(context)
        progressDialog.setMessage("Loading...")
        return progressDialog
    }

    /*
    private fun windDirection(deg: Double): String {
        var direction = "UNKNOWN"

        if (deg > 348.75 && deg > 11.25)
            direction = "N"
        else if (deg > 11.25 && deg < 33.75)
            direction = "NNE"
        else if (deg > 33.75 && deg < 56.25)
            direction = "NE"
        else if (deg > 56.25 && deg < 78.75)
            direction = "ENE"
        else if (deg > 78.75 && deg < 101.25)
            direction = "E"
        else if (deg > 101.25 && deg < 123.75)
            direction = "ESE"
        else if (deg > 123.75 && deg < 146.25)
            direction = "SE"
        else if (deg > 146.25 && deg < 168.75)
            direction = "SSE"
        else if (deg > 168.75 && deg < 191.25)
            direction = "S"
        else if (deg > 191.25 && deg < 213.75)
            direction = "SSW"
        else if (deg > 213.75 && deg < 236.25)
            direction = "SW"
        else if (deg > 236.25 && deg < 258.75)
            direction = "WSW"
        else if (deg > 258.75 && deg < 281.25)
            direction = "W"
        else if (deg > 281.25 && deg < 303.75)
            direction = "WNW"
        else if (deg > 303.75 && deg < 326.25)
            direction = "NW"
        else if (deg > 326.25 && deg < 348.75) direction = "NNW"
        return direction

    }
    fun normalizeDate(date: Long) = date / DAY_IN_SECONDS * DAY_IN_SECONDS
    private fun getUTCDateFromLocal(localDate: Long): Long {
        val tz = TimeZone.getDefault()
        val gmtOffset = tz.getOffset(localDate).toLong()
        return localDate - TimeUnit.MILLISECONDS.toSeconds(gmtOffset)
    }
    fun getAllCitiesExist(context: Context): ArrayList<String>? {

        val projection = arrayOf(LocationsEntry.COLUMN_CITY_NAME)
        val cursor = context.contentResolver.query(LocationsEntry.LOCATION_BASE_CONTENT_URI, projection, null, null, null)
        if (cursor == null || cursor.count == 0) return null
        val cities = ArrayList<String>()
        while (cursor.moveToNext()) {
            val city = cursor.getString(cursor.getColumnIndex(LocationsEntry.COLUMN_CITY_NAME))
            cities.add(city)
        }
        cursor.close()
        return cities
    }
    fun getLocationsExist(context: Context): ArrayList<LocationObject>? {

        val cursor = context.contentResolver.query(LocationsEntry.LOCATION_BASE_CONTENT_URI, null, null, null, null)
        if (cursor == null || cursor.count == 0) return null
        val locations = ArrayList<LocationObject>()
        while (cursor.moveToNext()) {
            val cityId = cursor.getString(cursor.getColumnIndex(LocationsEntry.COLUMN_CITY_ID))
            val cityName = cursor.getString(cursor.getColumnIndex(LocationsEntry.COLUMN_CITY_NAME))

            locations.add(LocationObject(cityName, cityId))
        }
        cursor.close()
        return locations
    }
*/
}
