package jamdevinc.weather.utilities

import android.util.Log
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

import javax.net.ssl.HttpsURLConnection

object NetworkUtils {


    var APP_KEY1 = "5404012ca09b40189a614368963ffe02"

    fun buildCurrentURL(cityId: String, key: String): URL {
        val stringURL = "https://api.weatherbit.io/v2.0/current?" +
                "city_id=" + cityId +
                "&key=" + key
        Log.e("Current Built URL:", stringURL)
        return try {
            URL(stringURL)
        } catch (e: MalformedURLException) {
            e.printStackTrace()
            throw IllegalArgumentException("Cannot Build URL $stringURL")
        }

    }

    fun buildCurrentURL(lat: String, lon: String, key: String): URL {
        val stringURL = "https://api.weatherbit.io/v2.0/current?" +
                "lat=" + lat +
                "&lon=" + lon +
                "&key=" + key
        Log.e("Current Built URL:", stringURL)

        return try {
             URL(stringURL)
        } catch (e: MalformedURLException) {
            e.printStackTrace()
            throw IllegalArgumentException("Cannot Build URL $stringURL")
        }

    }

    fun buildHourlyForecastURL(cityId: String, key: String): URL {
        val stringURL = "https://api.weatherbit.io/v2.0/forecast/hourly?" +
                "city_id=" + cityId +
                "&key=" + key
        Log.e("Forcast Built URL:", stringURL)
        Log.e("URL: ", stringURL)
        return try {
            URL(stringURL)
        } catch (e: MalformedURLException) {
            e.printStackTrace()
            throw IllegalArgumentException("Cannot Build URL $stringURL")
        }

    }

    fun buildHourlyForecastURL(lat: String, lon: String, key: String): URL {
        val stringURL = "https://api.weatherbit.io/v2.0/forecast/hourly?" +
                "lat=" + lat +
                "&lon=" + lon +
                "&key=" + key
        Log.e("Forcast Built URL:", stringURL)
        Log.e("URL: ", stringURL)
        return try {
            URL(stringURL)
        } catch (e: MalformedURLException) {
            e.printStackTrace()
            throw IllegalArgumentException("Cannot Build URL $stringURL")
        }

    }

    fun buildDailyForecastURL(cityId: String, key: String): URL {
        val stringURL = "https://api.weatherbit.io/v2.0/forecast/daily?" +
                "city_id=" + cityId +
                "&key=" + key
        Log.e("Forcast Built URL:", stringURL)
        Log.e("URL: ", stringURL)
        return try {
            URL(stringURL)
        } catch (e: MalformedURLException) {
            e.printStackTrace()
            throw IllegalArgumentException("Cannot Build URL $stringURL")
        }

    }

    fun buildDailyForecastURL(lat: String, lon: String, key: String): URL {
        val stringURL = "https://api.weatherbit.io/v2.0/forecast/daily?" +
                "lat=" + lat +
                "&lon=" + lon +
                "&key=" + key
        Log.e("Forcast Built URL:", stringURL)
        Log.e("URL: ", stringURL)
        return try {
            URL(stringURL)
        } catch (e: MalformedURLException) {
            e.printStackTrace()
            throw IllegalArgumentException("Cannot Build URL $stringURL")
        }

    }

    @Throws(IOException::class)
    fun getResponseFromURl(url: URL): String {
        val builder = StringBuilder()
        val httpsURLConnection = url.openConnection() as? HttpsURLConnection
        val inputStream: InputStream =
                if (httpsURLConnection == null) {
                    val httpURLConnection = url.openConnection() as HttpURLConnection
                    try {
                        httpURLConnection.inputStream
                    } finally {
                        httpURLConnection.disconnect()
                    }
                } else httpsURLConnection.inputStream

        val bufferedReader = BufferedReader(InputStreamReader(inputStream, "UTF-8"))
        var line = bufferedReader.readLine()

        while (line != null) {
            builder.append(line)
            line = bufferedReader.readLine()
        }
        httpsURLConnection?.disconnect()


        return builder.toString()
    }

}
