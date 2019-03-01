package jamdevinc.weather.utilities


import jamdevinc.weather.data.CurrentWeather
import jamdevinc.weather.data.DailyForecast
import jamdevinc.weather.data.HourlyForecast
import org.json.JSONException
import org.json.JSONObject
import java.util.*


object ExtractingDataFromJSON {

    private const val COLUMN_CITY_ID = "city_id"
    private const val COLUMN_CITY_NAME = "city_name"
    private const val COLUMN_DATE = "ts"
    private const val COLUMN_TEMP = "temp"
    private const val COLUMN_MAX_TEMP = "max_temp"
    private const val COLUMN_MIN_TEMP = "min_temp"
    private const val COLUMN_WEATHER_ID = "code"
    private const val COLUMN_TIMEZONE = "timezone"
    private const val COLUMN_SUNRISE = "sunrise"
    private const val COLUMN_SUNSET = "sunset"
    private const val COLUMN_HUMIDITY = "rh"
    private const val COLUMN_PRESSURE = "pres"
    private const val COLUMN_WIND_SPEED = "wind_spd"
    private const val COLUMN_WIND_DEGREE = "wind_cdir"

    @Throws(JSONException::class)
    fun extractCurrentWeatherData(jsonString: String): CurrentWeather {


        val jsonObject = JSONObject(jsonString)
        val mainObject = jsonObject.getJSONArray("data").getJSONObject(0)

        val cityID = mainObject.getLong(COLUMN_CITY_ID)
        val cityName = mainObject.getString(COLUMN_CITY_NAME)
        val timeZone = mainObject.getString(COLUMN_TIMEZONE)
        val sunrise = mainObject.getString(COLUMN_SUNRISE)
        val sunset = mainObject.getString(COLUMN_SUNSET)

        val weatherObject = mainObject.getJSONObject("weather")
        val weatherID = weatherObject.getInt(COLUMN_WEATHER_ID)
        val weatherDescription = LiteUtilities.getWeatherObject(weatherID).first
        val weatherIcon = LiteUtilities.getWeatherObject(weatherID).second

        val temp = mainObject.getDouble(COLUMN_TEMP)
        val humidity = mainObject.getDouble(COLUMN_HUMIDITY)
        val pressure = mainObject.getDouble(COLUMN_PRESSURE)

        val windSpeed = mainObject.getDouble(COLUMN_WIND_SPEED)
        val windDirection = mainObject.getString(COLUMN_WIND_DEGREE)

        return CurrentWeather(cityID,cityName,temp,weatherID,weatherDescription,weatherIcon,timeZone,sunrise,sunset,humidity,pressure
        ,windSpeed,windDirection)
    }

    @Throws(JSONException::class)
    fun extractHourlyForecastWeatherData(jsonString: String): ArrayList<HourlyForecast> {
        val weatherValues = ArrayList<HourlyForecast>()

        val jsonObject = JSONObject(jsonString)
        val cityID = jsonObject.getLong(COLUMN_CITY_ID)
        val cityName = jsonObject.getString(COLUMN_CITY_NAME)

        val daysListArray = jsonObject.getJSONArray("data")
        for (i in 0 until daysListArray.length()) {
            val dayObject = daysListArray.getJSONObject(i)

            val dt = dayObject.getLong(COLUMN_DATE)

            val temp = dayObject.getDouble(COLUMN_TEMP)

            val weatherID = dayObject.getJSONObject("weather").getInt(COLUMN_WEATHER_ID)
            val weatherIcon = LiteUtilities.getWeatherObject(weatherID).second
            weatherValues.add(HourlyForecast(cityID,cityName,dt,weatherIcon,temp))
        }

        return weatherValues


    }

    @Throws(JSONException::class)
    fun extractDailyForecastWeatherData(jsonString: String): ArrayList<DailyForecast> {
        val weatherValues = ArrayList<DailyForecast>()

        val jsonObject = JSONObject(jsonString)

        val cityID = jsonObject.getLong(COLUMN_CITY_ID)
        val cityName = jsonObject.getString(COLUMN_CITY_NAME)

        val daysListArray = jsonObject.getJSONArray("data")

        for (i in 0 until daysListArray.length()) {
            val dayObject = daysListArray.getJSONObject(i)


            val dt = dayObject.getLong(COLUMN_DATE)
            val maxTemp = dayObject.getDouble(COLUMN_MAX_TEMP)
            val minTemp = dayObject.getDouble(COLUMN_MIN_TEMP)

            val weatherID = dayObject.getJSONObject("weather").getInt(COLUMN_WEATHER_ID)
            val weatherIcon = LiteUtilities.getWeatherObject(weatherID).second
            weatherValues.add(DailyForecast(cityID,cityName,dt,weatherIcon,maxTemp,minTemp))
        }

        return weatherValues


    }




/*    @Throws(JSONException::class)
    fun extractCurrentWeatherData(jsonString: String): ContentValues {

        val contentValues = ContentValues()

        val jsonObject = JSONObject(jsonString)

        val mainObject = jsonObject.getJSONArray("data").getJSONObject(0)

        contentValues.put(CurrentWeatherEntry.COLUMN_CITY, mainObject.getString(CurrentWeatherEntry.COLUMN_CITY))
        contentValues.put(CurrentWeatherEntry.COLUMN_TIMEZONE, mainObject.getString(CurrentWeatherEntry.COLUMN_TIMEZONE))
        contentValues.put(CurrentWeatherEntry.COLUMN_SUNRISE, mainObject.getString(CurrentWeatherEntry.COLUMN_SUNRISE))
        contentValues.put(CurrentWeatherEntry.COLUMN_SUNSET, mainObject.getString(CurrentWeatherEntry.COLUMN_SUNSET))

        val weatherObject = mainObject.getJSONObject("weather")
        contentValues.put(CurrentWeatherEntry.COLUMN_WEATHER_ID, weatherObject.getInt(CurrentWeatherEntry.COLUMN_WEATHER_ID))

        contentValues.put(CurrentWeatherEntry.COLUMN_TEMP, mainObject.getDouble(CurrentWeatherEntry.COLUMN_TEMP))
        contentValues.put(CurrentWeatherEntry.COLUMN_HUMIDITY, mainObject.getDouble(CurrentWeatherEntry.COLUMN_HUMIDITY))
        contentValues.put(CurrentWeatherEntry.COLUMN_PRESSURE, mainObject.getDouble(CurrentWeatherEntry.COLUMN_PRESSURE))

        contentValues.put(CurrentWeatherEntry.COLUMN_WIND_SPEED, mainObject.getDouble(CurrentWeatherEntry.COLUMN_WIND_SPEED))
        contentValues.put(CurrentWeatherEntry.COLUMN_WIND_DEGREE, mainObject.getString(CurrentWeatherEntry.COLUMN_WIND_DEGREE))

        return contentValues
    }

    @Throws(JSONException::class)
    fun extractHourlyForecastWeatherData(jsonString: String): ArrayList<ContentValues> {
        val weatherValues = ArrayList<ContentValues>()

        val jsonObject = JSONObject(jsonString)
        val cityName = jsonObject.getString(HourlyForecastEntry.COLUMN_CITY)

        val daysListArray = jsonObject.getJSONArray("data")

        for (i in 0 until daysListArray.length()) {
            val value = ContentValues()
            val dayObject = daysListArray.getJSONObject(i)

            value.put(HourlyForecastEntry.COLUMN_CITY, cityName)

            val dt = dayObject.getLong(HourlyForecastEntry.COLUMN_DATE)
            value.put(HourlyForecastEntry.COLUMN_DATE, dt)

            value.put(HourlyForecastEntry.COLUMN_TEMP, dayObject.getDouble(HourlyForecastEntry.COLUMN_TEMP))

            value.put(DailyForecastEntry.COLUMN_WEATHER_ID, dayObject.getJSONObject("weather").getInt(DailyForecastEntry.COLUMN_WEATHER_ID))

            weatherValues.add(value)
        }

        return weatherValues


    }

    @Throws(JSONException::class)
    fun extractDailyForecastWeatherData(jsonString: String): ArrayList<ContentValues> {
        val weatherValues = ArrayList<ContentValues>()

        val jsonObject = JSONObject(jsonString)

        val cityName = jsonObject.getString(DailyForecastEntry.COLUMN_CITY)

        val daysListArray = jsonObject.getJSONArray("data")

        for (i in 0 until daysListArray.length()) {
            val value = ContentValues()
            val dayObject = daysListArray.getJSONObject(i)

            value.put(DailyForecastEntry.COLUMN_CITY, cityName)

            val dt = dayObject.getLong(DailyForecastEntry.COLUMN_DATE)
            value.put(DailyForecastEntry.COLUMN_DATE, dt)

            value.put(DailyForecastEntry.COLUMN_MAX_TEMP, dayObject.getDouble(DailyForecastEntry.COLUMN_MAX_TEMP))
            value.put(DailyForecastEntry.COLUMN_MIN_TEMP, dayObject.getDouble(DailyForecastEntry.COLUMN_MIN_TEMP))

            value.put(DailyForecastEntry.COLUMN_WEATHER_ID, dayObject.getJSONObject("weather").getInt(DailyForecastEntry.COLUMN_WEATHER_ID))

            weatherValues.add(value)
        }

        return weatherValues


    }*/
/*public static ContentValues extractCurrentWeatherData(String jsonString, String cityName) throws JSONException {

        ContentValues contentValues = new ContentValues();
        contentValues.put(CurrentWeatherEntry.COLUMN_CITY,cityName);

        JSONObject object = new JSONObject(jsonString);

        JSONObject sysObject = object.getJSONObject("sys");
        contentValues.put(CurrentWeatherEntry.COLUMN_SUNRISE,sysObject.getLong(CurrentWeatherEntry.COLUMN_SUNRISE));
        contentValues.put(CurrentWeatherEntry.COLUMN_SUNSET,sysObject.getLong(CurrentWeatherEntry.COLUMN_SUNSET));

        JSONObject weatherObject = object.getJSONArray("weather").getJSONObject(0);
        contentValues.put(CurrentWeatherEntry.COLUMN_WEATHER_ID,weatherObject.getInt(CurrentWeatherEntry.COLUMN_WEATHER_ID));
        contentValues.put(CurrentWeatherEntry.COLUMN_ICON,weatherObject.getString(CurrentWeatherEntry.COLUMN_ICON));

        JSONObject mainObject = object.getJSONObject("main");

        contentValues.put(CurrentWeatherEntry.COLUMN_TEMP,mainObject.getDouble(CurrentWeatherEntry.COLUMN_TEMP));
        contentValues.put(CurrentWeatherEntry.COLUMN_MAX_TEMP,mainObject.getDouble(CurrentWeatherEntry.COLUMN_MAX_TEMP));
        contentValues.put(CurrentWeatherEntry.COLUMN_MIN_TEMP,mainObject.getDouble(CurrentWeatherEntry.COLUMN_MIN_TEMP));
        contentValues.put(CurrentWeatherEntry.COLUMN_HUMIDITY,mainObject.getDouble(CurrentWeatherEntry.COLUMN_HUMIDITY));
        contentValues.put(CurrentWeatherEntry.COLUMN_PRESSURE,mainObject.getDouble(CurrentWeatherEntry.COLUMN_PRESSURE));

        JSONObject windObject = object.getJSONObject("wind");
        contentValues.put(CurrentWeatherEntry.COLUMN_WIND_SPEED,windObject.getDouble(CurrentWeatherEntry.COLUMN_WIND_SPEED));
        contentValues.put(CurrentWeatherEntry.COLUMN_WIND_DEGREE,windObject.getDouble(CurrentWeatherEntry.COLUMN_WIND_DEGREE));


        return contentValues;
    }
    public static ArrayList<ContentValues> extractDailyForecastWeatherData(String jsonString, String cityName) throws JSONException {

        JSONObject jsonObject = new JSONObject(jsonString);

        JSONArray daysListArray = jsonObject.getJSONArray("list");
        ArrayList<ContentValues> weatherValues = new ArrayList<>();
        for(int i =0;i<daysListArray.length();i++) {
            JSONObject dayObject = daysListArray.getJSONObject(i);
            long dt = dayObject.getLong(DailyForecastEntry.COLUMN_DATE);

            ContentValues value = new ContentValues();
            value.put(DailyForecastEntry.COLUMN_CITY,cityName);

            value.put(DailyForecastEntry.COLUMN_DATE,dt);

            JSONObject mainObject = dayObject.getJSONObject("main");

            value.put(DailyForecastEntry.COLUMN_MAX_TEMP,mainObject.getDouble(DailyForecastEntry.COLUMN_MAX_TEMP));
            value.put(DailyForecastEntry.COLUMN_MIN_TEMP,mainObject.getDouble(DailyForecastEntry.COLUMN_MIN_TEMP));

            JSONObject weatherObject = dayObject.getJSONArray("weather").getJSONObject(0);
            value.put(DailyForecastEntry.COLUMN_WEATHER_ID,weatherObject.getInt(DailyForecastEntry.COLUMN_WEATHER_ID));
            value.put(DailyForecastEntry.COLUMN_ICON,weatherObject.getString(DailyForecastEntry.COLUMN_ICON));

            weatherValues.add(value);
        }

        return weatherValues;


    }

*/
}
