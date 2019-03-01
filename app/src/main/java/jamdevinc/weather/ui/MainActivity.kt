package jamdevinc.weather.ui


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.app.SearchManager
import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.LocationManager
import android.net.ConnectivityManager
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.PagerAdapter
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.reward.RewardedVideoAd
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import jamdevinc.weather.R
import jamdevinc.weather.data.*
import jamdevinc.weather.sync.LiteSyncTask
import jamdevinc.weather.utilities.LiteUtilities
import jamdevinc.weather.utilities.NetworkUtils
import kotlinx.android.synthetic.main.activity_main.*
import java.net.InetAddress
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity(), MainFragment.OnSwipeToRefreshListener {


    private  var db : AppDatabase? = null
    private lateinit var adapter: PagerAdapter

    private lateinit var preferences: SharedPreferences
    private lateinit var defaultCity: String
    private lateinit var locations: ArrayList<String>
    private var locationsCount: Int = 0

    private lateinit var progressDialog: ProgressDialog

    private val _interstitialAd1: InterstitialAd = InterstitialAd(this)
    private val _interstitialAd2: InterstitialAd = InterstitialAd(this)
    private val _interstitialAd3: InterstitialAd = InterstitialAd(this)
    private lateinit var _videoAd1: RewardedVideoAd

    private var restToShowAds = 3


    private val isNetworkAvailable: Boolean
        get() {
            val managerCompat = getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            if (managerCompat == null ||
                    managerCompat.activeNetworkInfo == null ||
                    !managerCompat.activeNetworkInfo.isConnected)
                return false

            return try {
                val ipAddr = InetAddress.getByName("google.com")
                ipAddr.toString() != ""

            } catch (e: Exception) {
                false
            }

        }

    private val isGooglePlayServiceExist: Boolean
        get() {
            val googleApiAvailability = GoogleApiAvailability.getInstance()
            return googleApiAvailability != null && googleApiAvailability.isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS
        }


    private lateinit var dbWorkerThread : RepoHandlerThread

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        supportActionBar?.hide()
        setContentView(R.layout.activity_main)

        preferences  = PreferenceManager.getDefaultSharedPreferences(this)
        progressDialog = LiteUtilities.buildProgressDialog(this)

        rootLayout.setBackgroundColor(preferences.getInt(LiteUtilities.HARD_COLOR_PREF_KEY,
                resources.getColor(R.color.blueDarkBackground)))
        viewPager.setBackgroundColor(preferences.getInt(LiteUtilities.SOFT_COLOR_PREF_KEY,
                resources.getColor(R.color.blueBackground)))

        var locations :ArrayList<String> = ArrayList()

        dbWorkerThread = RepoHandlerThread("db_thread")
        dbWorkerThread.start()
        dbWorkerThread.postTask(Runnable {
           val repository = DataRepository.getInstance(application)
            repository.getAllLocations()?.observe(this, Observer {
                if (it != null) {
                    for (locationObject in it){
                        locations.add(locationObject.cityName)
                    }
                }
            })
        })




        defaultCity = preferences.getString(LiteUtilities.DEFAULT_CITY_NAME_KEY, "")

        locations = intent.getStringArrayListExtra(LiteUtilities.LOCATIONS_KEY) ?: ArrayList()
        if (locations.size == 0) {
            if (defaultCity == "") setupUserLocationData()

        }
        if (defaultCity != "") {
            if (!locations.contains(defaultCity))
                locations.add(0, defaultCity)
            gpsBt.visibility = View.INVISIBLE
        }

        locationsCount = locations.size

        adapter = SlidesPageAdapter(supportFragmentManager)
        viewPager.adapter = adapter
        tabDots.setupWithViewPager(viewPager)

        if (locationsCount > 1)
            deleteBt.visibility = View.VISIBLE

        deleteBt.setOnClickListener {
            val i = viewPager.currentItem
            val location = locations[i]

            locations.remove(location)
            locationsCount--
            if (locationsCount <= 1) deleteBt.visibility = View.INVISIBLE
            if (location == defaultCity) {
                defaultCity = ""
                gpsBt.visibility = View.VISIBLE
                preferences.edit()
                        .putString(LiteUtilities.DEFAULT_CITY_NAME_KEY, "")
                        .putString(LiteUtilities.DEFAULT_CITY_LAT_KEY, "")
                        .putString(LiteUtilities.DEFAULT_CITY_LON_KEY, "")
                        .apply()
            }
            adapter.notifyDataSetChanged()
            restToShowAds--
        }


        addBt.setOnClickListener {
            checkIfToLoadAds()
            onSearchRequested()
        }
        gpsBt.setOnClickListener { setupUserLocationData() }
        unitBt.setOnClickListener { changeTemperatureUnit() }

        if (preferences.getBoolean(LiteUtilities.IS_TEMP_FAH_UNIT, false))
            unitBt.setImageResource(R.drawable.ic_fahrenheit)
        else
            unitBt.setImageResource(R.drawable.ic_cellcius)

        syncData()
        setupAds()
    }

    private fun setupAds() {
         adView.loadAd(AdRequest.Builder().build())

        _interstitialAd1.adUnitId = getString(R.string.AdInterstitial1)
        _interstitialAd1.loadAd(AdRequest.Builder().build())

        _interstitialAd2.adUnitId = getString(R.string.AdInterstitial2)
        _interstitialAd2.loadAd(AdRequest.Builder().build())

        _interstitialAd3.adUnitId = getString(R.string.AdInterstitial3)
        _interstitialAd3.loadAd(AdRequest.Builder().build())

        _videoAd1  = MobileAds.getRewardedVideoAdInstance(this)
        _videoAd1.loadAd(getString(R.string.AdVideo1), AdRequest.Builder().build())
    }

    private fun checkIfToLoadAds() {
        if (restToShowAds <= 0) {

            if (_videoAd1.isLoaded) {
                _videoAd1.show()
                _videoAd1.loadAd(getString(R.string.AdVideo1), AdRequest.Builder().build())
            } else {
                _videoAd1.loadAd(getString(R.string.AdVideo1), AdRequest.Builder().build())
                if (_interstitialAd1.isLoaded) {
                    _interstitialAd1.show()
                    _interstitialAd1.loadAd(AdRequest.Builder().build())
                } else
                    _interstitialAd1.loadAd(AdRequest.Builder().build())

                if (_interstitialAd2.isLoaded) {
                    _interstitialAd2.show()
                    _interstitialAd2.loadAd(AdRequest.Builder().build())
                } else
                    _interstitialAd2.loadAd(AdRequest.Builder().build())

                if (_interstitialAd3.isLoaded) {
                    _interstitialAd3.show()
                    _interstitialAd3.loadAd(AdRequest.Builder().build())
                } else
                    _interstitialAd3.loadAd(AdRequest.Builder().build())
            }
            restToShowAds = 2
        } else {
            restToShowAds = 1
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        if (Intent.ACTION_VIEW == intent.action) {
            val cityId = intent.dataString
            val cityName = intent.getStringExtra(SearchManager.EXTRA_DATA_KEY)

            if (locations.contains(cityName))
                viewPager.currentItem = locations.indexOf(cityName)
            else {
                RequestWeatherData().execute(cityId)
                progressDialog.show()

            }
        }
    }

    private fun setupUserLocationData() {
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        val permissions = arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
        if (ActivityCompat.checkSelfPermission(this, permissions[0]) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, permissions[1]) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(permissions, 100)
            }

        }
        else if (isGooglePlayServiceExist) {
            fusedLocationProviderClient.lastLocation
                    .addOnSuccessListener { location ->
                        if (location != null) {
                            RequestWeatherData().execute("coord", location.latitude.toString() + "", location.longitude.toString() + "")

                        }
                        else {
                            val mLocationRequest = LocationRequest()
                            mLocationRequest.interval = 10000
                            mLocationRequest.fastestInterval = 5000
                            mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

                            val builder = LocationSettingsRequest.Builder()
                                    .addLocationRequest(mLocationRequest)
                            val client = LocationServices.getSettingsClient(this@MainActivity)
                            val task = client.checkLocationSettings(builder.build())
                            task.addOnSuccessListener {
                                LocationServices.getFusedLocationProviderClient(this).lastLocation
                                        .addOnSuccessListener { location ->
                                            if (location != null) {
                                                RequestWeatherData().execute("coord", location.latitude.toString() + "", location.longitude.toString() + "")

                                            }
                                            else
                                                Toast.makeText(this@MainActivity, "Cannot Get Location! Please Add it Manually", Toast.LENGTH_LONG).show()
                                        }
                            }
                            task.addOnFailureListener { e ->
                                    if (e is ResolvableApiException) {
                                        try {
                                            e.startResolutionForResult(this@MainActivity,
                                                    3)
                                        } catch (sendEx: IntentSender.SendIntentException) {
                                            //
                                        }

                                    }
                                }
                            }
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Permission Not Granted To Get Actual Location...", Toast.LENGTH_LONG).show()
                        onSearchRequested()
                    }
        }
        else {
            val locationManager = getSystemService(Context.LOCATION_SERVICE) as? LocationManager
            val provider = locationManager?.getBestProvider(Criteria(), false)
            val location = locationManager?.getLastKnownLocation(provider)
            if(location != null)
                RequestWeatherData().execute("coord", location.latitude.toString() + "", location.longitude.toString() + "")
            else {
                Toast.makeText(this@MainActivity, "Cannot Get Location! Please Add it Manually", Toast.LENGTH_LONG).show()
                onSearchRequested()
            }
            gpsBt.visibility = View.INVISIBLE
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                setupUserLocationData()
            } else
                Toast.makeText(this, "Permission Not Granted To Get Actual Location...", Toast.LENGTH_LONG).show()

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 3) {
            if (resultCode == Activity.RESULT_OK) {
                setupUserLocationData()
            } else {

                Toast.makeText(this@MainActivity, "Cannot Get Location! Please Add it Manually", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun changeTemperatureUnit() {

        if (preferences.getBoolean(LiteUtilities.IS_TEMP_FAH_UNIT, false)) {
            preferences.edit().putBoolean(LiteUtilities.IS_TEMP_FAH_UNIT, false).apply()
            unitBt.setImageResource(R.drawable.ic_cellcius)
            adapter.notifyDataSetChanged()
        } else {
            preferences.edit().putBoolean(LiteUtilities.IS_TEMP_FAH_UNIT, true).apply()
            unitBt.setImageResource(R.drawable.ic_fahrenheit)
            adapter.notifyDataSetChanged()

        }
    }

    override fun onSwipeToRefresh(refreshLayout: SwipeRefreshLayout) {
        Handler().postDelayed({
            refreshLayout.isRefreshing = false
            adapter.notifyDataSetChanged()
            rootLayout.setBackgroundColor(preferences.getInt(LiteUtilities.HARD_COLOR_PREF_KEY, resources.getColor(R.color.blueDarkBackground)))
            viewPager.setBackgroundColor(preferences.getInt(LiteUtilities.SOFT_COLOR_PREF_KEY, resources.getColor(R.color.blueBackground)))
        }, 2000)

        syncData()
        checkIfToLoadAds()
    }

    private fun syncData() {
        val lastSync = preferences.getLong(LiteUtilities.LAST_SYNC_TIME, LiteUtilities.SYSTEM_CURRENT_TIME)
        val lastSyncDaysInterval =
                TimeUnit.SECONDS.toHours(LiteUtilities.SYSTEM_CURRENT_TIME- lastSync)
        restToShowAds--
        Log.e("Last Sync:",lastSyncDaysInterval.toString())
        if (lastSyncDaysInterval > 10 && locationsCount > 0) SyncDataAsyncTask().execute()

    }

    @SuppressLint("StaticFieldLeak")
    private inner class RequestWeatherData : AsyncTask<String, Void, String>() {

        override fun onPreExecute() {
            super.onPreExecute()
            progressDialog.show()
        }

        override fun doInBackground(vararg strings: String): String {
            var cityAdded = ""
            /*if(!isNetworkAvailable) return cityAdded

            val isDone: Boolean
            val requestMethod = strings[0]

            if (requestMethod == "coord") {
                val lat = strings[1]
                val lon = strings[2]

                isDone = LiteSyncTask.insertWeatherData(this@MainActivity, lat, lon)
                if (isDone) {
                    cityAdded = preferences.getString(LiteUtilities.DEFAULT_CITY_NAME_KEY, "")
                    defaultCity = cityAdded
                    preferences.edit().putString(LiteUtilities.DEFAULT_CITY_LAT_KEY, lat)
                            .putString(LiteUtilities.DEFAULT_CITY_LON_KEY, lon).apply()
                }
            } else if (requestMethod == "id") {
                val cityId = strings[1]
                val cityName = strings[2]

                isDone = LiteSyncTask.insertWeatherData(this@MainActivity, cityId)
                if (isDone) {
                    val values = ContentValues()
                    values.put(LiteContract.LocationsEntry.COLUMN_CITY_ID, cityId)
                    values.put(LiteContract.LocationsEntry.COLUMN_CITY_NAME, cityName)
                    contentResolver.insert(LiteContract.LocationsEntry.LOCATION_BASE_CONTENT_URI, values)
                    cityAdded = cityName
                }

            }*/
            return cityAdded
        }

        override fun onPostExecute(cityAdded: String) {
            progressDialog.cancel()
            if (cityAdded != "") {
                locations.add(cityAdded)
                locationsCount++

                adapter.notifyDataSetChanged()
                viewPager.currentItem = locationsCount

                restToShowAds--
                if (cityAdded == defaultCity) gpsBt.visibility = View.INVISIBLE
                if (locationsCount > 1) deleteBt.visibility = View.VISIBLE
            } else
                Toast.makeText(this@MainActivity, "Cannot Get Weather Data, Check Your Network State...", Toast.LENGTH_LONG).show()
        }
    }

    @SuppressLint("StaticFieldLeak")
    private inner class SyncDataAsyncTask : AsyncTask<Void, Void, Boolean>() {

        override fun onPreExecute() {
            super.onPreExecute()
            progressDialog.show()
        }

        override fun doInBackground(vararg voids: Void): Boolean {
            return if (isNetworkAvailable) {
                return true
            }
            else false
        }

        override fun onPostExecute(isSynced: Boolean) {
            super.onPostExecute(isSynced)
            progressDialog.cancel()

            if (isSynced) {
                adapter.notifyDataSetChanged()
            } else
                Toast.makeText(this@MainActivity, "Cannot get Data! Please Check Your Network and Try Again", Toast.LENGTH_LONG).show()
        }
    }

    private inner class SlidesPageAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            val location = locations[position]
            Log.e("Adapter", "Get item at position $position And Location $location")
            return MainFragment.instantiate(location)
        }

        override fun getCount(): Int {
            return locationsCount
        }

        override fun getItemPosition(`object`: Any?): Int {
            return PagerAdapter.POSITION_NONE
        }
    }

    @SuppressLint("StaticFieldLeak")
    private inner class insertWeatherDataAsync : AsyncTask<String,Unit,String>(){
        override fun onPreExecute() {
            super.onPreExecute()
            progressDialog.show()
        }
        override fun doInBackground(vararg p0: String) :String{
            db = AppDatabase.getInstance(this@MainActivity)
            if(!isNetworkAvailable) return ""

                val dataObjects = LiteSyncTask.getWeatherContentValues(LiteSyncTask.buildURLs(p0[0], NetworkUtils.APP_KEY1))

                val currentWeather = dataObjects[0] as CurrentWeather
                val hourlyForecastValues = dataObjects[1] as ArrayList<*>
                val dailyForecastValues = dataObjects[2] as ArrayList<*>

                db?.currentWeatherDAO()?.insertCityCurrentWeather(currentWeather)
                for (i in hourlyForecastValues.indices) {
                    val hourlyForecast = hourlyForecastValues[i] as HourlyForecast
                    db?.hourlyWeatherDAO()?.insertCityHourlyWeather(hourlyForecast)
                }
                for (i in dailyForecastValues.indices) {
                    val dailyForecast = hourlyForecastValues[i] as DailyForecast
                    db?.dailyWeatherDAO()?.insertCityDailyWeather(dailyForecast)
                }

            return currentWeather.cityName
        }

        override fun onPostExecute(cityAdded: String) {
            super.onPostExecute(cityAdded)
            progressDialog.cancel()
            if (cityAdded != "") {
                locations.add(cityAdded)
                locationsCount++

                adapter.notifyDataSetChanged()
                viewPager.currentItem = locationsCount

                restToShowAds--
                if (cityAdded == defaultCity) gpsBt.visibility = View.INVISIBLE
                if (locationsCount > 1) deleteBt.visibility = View.VISIBLE
            } else
                Toast.makeText(this@MainActivity, "Cannot Get Weather Data, Check Your Network State...", Toast.LENGTH_LONG).show()

        }
    }



}
