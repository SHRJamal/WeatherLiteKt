/*
package jamdevinc.weather.data



private fun createCitiesList() {
    var i = _locationObjectArrayList.size
    if (i > 20000) return
    Log.e("Search Provide", "Create Location List Length:" + _locationObjectArrayList.size)
    try {
        val builder = StringBuilder()
        val `is` = context!!.assets.open("cities.json")
        val bufferedReader = BufferedReader(InputStreamReader(`is`, "UTF-8"))
        var line = bufferedReader.readLine()
        while (line != null) {
            builder.append(line)
            line = bufferedReader.readLine()
        }

        val jsonArray = JSONArray(builder.toString())
        _locationObjectArrayList = ArrayList()

        while (i < jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)

            val cityName = jsonObject.getString("city_name")
            val cityId = jsonObject.getString("id")
            val countryCode = jsonObject.getString("country_code")

            _locationObjectArrayList.add(LocationObject(cityName, cityId,countryCode))
            i++
        }
    } catch (e: IOException) {
        e.printStackTrace()
    } catch (e: JSONException) {
        e.printStackTrace()
    }



}*/
