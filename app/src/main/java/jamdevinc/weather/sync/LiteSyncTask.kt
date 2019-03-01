package jamdevinc.weather.sync


import jamdevinc.weather.utilities.ExtractingDataFromJSON
import jamdevinc.weather.utilities.NetworkUtils
import org.json.JSONException
import java.io.IOException
import java.net.URL

object LiteSyncTask {

    /*fun syncWeatherData(context: Context): Boolean {
        var isSynced = false
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val defaultCity = preferences.getString(LiteUtilities.DEFAULT_CITY_NAME_KEY, "")
        if (defaultCity != "") {
            deleteWeatherData(context, defaultCity)
            val lat = preferences.getString(LiteUtilities.DEFAULT_CITY_LAT_KEY, "")
            val lon = preferences.getString(LiteUtilities.DEFAULT_CITY_LON_KEY, "")
            if(lat != "" && lon != "")
                isSynced = insertWeatherData(context, lat, lon)
        }

        val locationsExist = LiteUtilities.getLocationsExist(context)
        if (locationsExist != null && locationsExist.size > 0)
            for (locationObject in locationsExist) {
                deleteWeatherData(context, locationObject.cityName)
                isSynced = insertWeatherData(context, locationObject.cityID)
            }

        return isSynced
    }

    fun insertWeatherData(context: Context, cityID: String): Boolean {
        return try {
            insertData(context, getWeatherContentValues(buildURLs(cityID, NetworkUtils.APP_KEY1)), false)
        } catch (e: JSONException) {
            e.printStackTrace()
            false
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }

    }

    fun insertWeatherData(context: Context, lat: String, lon: String): Boolean {
        return try {
            insertData(context, getWeatherContentValues(buildURLs(lat, lon, NetworkUtils.APP_KEY1)), true)
        } catch (e: JSONException) {
            e.printStackTrace()
            false
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }

    }

    private fun deleteWeatherData(context: Context, location: String) {
        val currentUri = CurrentWeatherEntry.CURRENT_BASE_CONTENT_URI.buildUpon().appendPath(location).build()
        val hourlyForecastUri = HourlyForecastEntry.CITY_CONTENT_URI.buildUpon().appendPath(location).build()
        val dailyForecastUri = DailyForecastEntry.CITY_CONTENT_URI.buildUpon().appendPath(location).build()

        context.contentResolver.delete(currentUri, null, null)
        context.contentResolver.delete(hourlyForecastUri, null, null)
        context.contentResolver.delete(dailyForecastUri, null, null)
    }

    fun deleteLocationData(context: Context, location: String) {
        val resolver = context.contentResolver
        val currentUri = LiteContract.CurrentWeatherEntry.CURRENT_BASE_CONTENT_URI.buildUpon().appendPath(location).build()
        val hourlyForecastUri = LiteContract.HourlyForecastEntry.CITY_CONTENT_URI.buildUpon().appendPath(location).build()
        val dailyForecastUri = LiteContract.DailyForecastEntry.CITY_CONTENT_URI.buildUpon().appendPath(location).build()
        val locationUri = LiteContract.LocationsEntry.LOCATION_BASE_CONTENT_URI.buildUpon().appendPath(location).build()

        resolver.delete(currentUri, null, null)
        resolver.delete(hourlyForecastUri, null, null)
        resolver.delete(dailyForecastUri, null, null)
        resolver.delete(locationUri, null, null)
    }*/

    fun buildURLs(cityId: String, key: String): Array<URL> {
        val currentURL = NetworkUtils.buildCurrentURL(cityId, key)
        val hourlyForecastURL = NetworkUtils.buildHourlyForecastURL(cityId, key)
        val dailyForecastURL = NetworkUtils.buildDailyForecastURL(cityId, key)
        return arrayOf(currentURL, hourlyForecastURL, dailyForecastURL)
    }

    fun buildURLs(lat: String, lon: String, key: String): Array<URL> {
        val currentURL = NetworkUtils.buildCurrentURL(lat, lon, key)
        val hourlyForecastURL = NetworkUtils.buildHourlyForecastURL(lat, lon, key)
        val dailyForecastURL = NetworkUtils.buildDailyForecastURL(lat, lon, key)
        return arrayOf(currentURL, hourlyForecastURL, dailyForecastURL)
    }

    @Throws(IOException::class, JSONException::class)
    fun getWeatherContentValues(urls: Array<URL>): Array<Any> {
        val currentString = NetworkUtils.getResponseFromURl(urls[0])
        val hourlyForecastString = NetworkUtils.getResponseFromURl(urls[1])
        val dailyForecastString = NetworkUtils.getResponseFromURl(urls[2])

        val currentWeatherValues = ExtractingDataFromJSON.extractCurrentWeatherData(currentString)
        val hourlyForecastValues = ExtractingDataFromJSON.extractHourlyForecastWeatherData(hourlyForecastString)
        val dailyForecastValues = ExtractingDataFromJSON.extractDailyForecastWeatherData(dailyForecastString)
        return arrayOf(currentWeatherValues, hourlyForecastValues, dailyForecastValues)

    }


    /*private fun insertData(context: Context, dataObjects: Array<Any>, isByCoord: Boolean): Boolean {
        val preferences : SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val currentWeatherValues = dataObjects[0] as ContentValues
        val hourlyForecastValues = dataObjects[1] as ArrayList<*>
        val dailyForecastValues = dataObjects[2] as ArrayList<*>
        val resolver = context.contentResolver
        var isInserted = false

        val uri = resolver.insert(CurrentWeatherEntry.CURRENT_BASE_CONTENT_URI, currentWeatherValues)
        if (uri != null) {
            isInserted = true
            preferences.edit().putLong(LiteUtilities.LAST_SYNC_TIME, LiteUtilities.SYSTEM_CURRENT_TIME).apply()
            if (isByCoord)
                        preferences.edit().putString(LiteUtilities.DEFAULT_CITY_NAME_KEY,
                                currentWeatherValues.getAsString(LiteContract.CurrentWeatherEntry.COLUMN_CITY))
                        .apply()
        }

        for (i in hourlyForecastValues.indices) {
            val value = hourlyForecastValues[i] as? ContentValues
            if(value != null)resolver.insert(HourlyForecastEntry.DATE_CONTENT_URI,value)
        }
        for (i in dailyForecastValues.indices) {
            val value = dailyForecastValues[i] as? ContentValues
            if(value != null) resolver.insert(DailyForecastEntry.DATE_CONTENT_URI, value)
        }
        return isInserted
    }*/


}
