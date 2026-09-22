package com.example.mobileappdevelopment

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.appcompat.widget.SwitchCompat
import androidx.appcompat.widget.Toolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.mobileappdevelopment.weather.IWeatherService
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.google.firebase.auth.FirebaseAuth
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import android.app.Activity

class MainActivity : AppCompatActivity(), LocationListener {

    private val TAG = "MainActivity"
    private lateinit var auth: FirebaseAuth

    companion object {
        private const val RC_SIGN_IN = 123
    }

    private lateinit var locationManager: LocationManager
    private val locationPermissionCode = 2
    private var lastLocation: Location? = null

    private lateinit var textViewStatus: TextView
    private lateinit var txtRecomendacion: TextView

    private val API_KEY = "7d2e7a5d6ee64b1fd7fd8dd8303616dc"
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.openweathermap.org/data/2.5/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val weatherService = retrofit.create(IWeatherService.IWeatherService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

        if (auth.currentUser == null) {
            launchSignInFlow()
        } else {
            updateUIWithUsername()
        }

        textViewStatus = findViewById(R.id.mainTextView)
        txtRecomendacion = findViewById(R.id.txtRecomendacion)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.setOnNavigationItemSelectedListener { item ->
            val targetActivity = when (item.itemId) {
                R.id.navigation_home -> MainActivity::class.java
                R.id.navigation_map -> OpenStreetMapsActivity::class.java
                R.id.navigation_bicimad -> BiciMadActivity::class.java
                R.id.navigation_list -> FavoritesActivity::class.java
                else -> null
            }

            if (targetActivity != null && this::class.java != targetActivity) {
                val intent = Intent(this, targetActivity)
                intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                startActivity(intent)
                overridePendingTransition(0, 0)
                true
            } else false
        }

        val locationSwitch: SwitchCompat = findViewById(R.id.locationSwitch)
        val isCurrentlyEnabled = getLocationPreference()
        locationSwitch.isChecked = isCurrentlyEnabled

        if (isCurrentlyEnabled) {
            checkLocationPermissions()
        } else {
            textViewStatus.text = "GPS Off"
        }

        locationSwitch.setOnCheckedChangeListener { _, isChecked ->
            toggleLocation(isChecked)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(this, "Welcome!", Toast.LENGTH_SHORT).show()
                updateUIWithUsername()
            } else {
                Log.e(TAG, "Error: ${response?.error?.errorCode}")
                finish()
            }
        }
    }

    private fun launchSignInFlow() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )
        startActivityForResult(
            AuthUI.getInstance().createSignInIntentBuilder()
                .setAvailableProviders(providers).build(),
            RC_SIGN_IN
        )
    }

    private fun pedirTiempo(lat: Double, lon: Double) {
        lifecycleScope.launch {
            try {
                val res = weatherService.getWeather(lat, lon, API_KEY)

                findViewById<TextView>(R.id.tvCity).text = res.name
                val temperaturaC = res.main.temp
                findViewById<TextView>(R.id.tvTemp).text = "${temperaturaC.toInt()}°C"

                textViewStatus.text = "${res.weather[0].description.replaceFirstChar { it.uppercase() }}\n" +
                        "Clouds: ${res.clouds.all}% | Humidity: ${res.main.humidity}%"

                val iconCode = res.weather[0].icon
                val iconUrl = "https://openweathermap.org/img/wn/$iconCode@4x.png"
                Glide.with(this@MainActivity)
                    .load(iconUrl)
                    .into(findViewById<ImageView>(R.id.ivWeatherIcon))

                mostrarRecomendacion(temperaturaC, res.weather[0].description)

            } catch (e: Exception) {
                Log.e("WEATHER", "Error: ${e.message}")
            }
        }
    }

    private fun mostrarRecomendacion(temp: Double, clima: String) {
        val recomendacion = when {
            clima.contains("Rain", true) || clima.contains("Drizzle", true) -> {
                "🌧️ Slippery pavement. If you use BiciMAD today, keep a safe distance and brake gently."
            }
            clima.contains("Snow", true) -> {
                "❄️ Snow detected. We advise against riding a bike today due to the risk of ice patches."
            }
            temp < 10.0 -> {
                "🥶 It's cold out there (${temp.toInt()}ºC). Remember to wear gloves so you don't lose grip while braking."
            }
            temp > 30.0 -> {
                "☀️ Heat warning (${temp.toInt()}ºC). Avoid intense physical effort and carry water if you're going to pedal."
            }
            temp in 15.0..26.0 -> {
                "🍃 Perfect weather (${temp.toInt()}ºC)! Riding a bike today is the best way to reduce your carbon footprint."
            }
            else -> {
                "🚲 Current temperature: ${temp.toInt()}ºC. Always check your tire pressure before unlocking your BiciMAD."
            }
        }

        runOnUiThread {
            txtRecomendacion.text = recomendacion
        }
    }

    private fun checkLocationPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                locationPermissionCode
            )
        } else {
            startLocationUpdates()
        }
    }

    private fun startLocationUpdates() {
        try {
            textViewStatus.text = "GPS On - Updating..."
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0f, this)
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0f, this)

            val lastKnown = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                ?: locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

            lastKnown?.let { onLocationChanged(it) }
        } catch (e: SecurityException) {
            Log.e(TAG, "Error: ${e.message}")
        }
    }

    override fun onLocationChanged(location: Location) {

        pedirTiempo(location.latitude, location.longitude)
    }



    private fun updateUIWithUsername() {
        val user = auth.currentUser
        val userNameTV: TextView? = findViewById(R.id.userNameTextView)
        user?.let {
            val name = it.displayName ?: "No name"
            userNameTV?.text = "🤵‍♂️ $name"
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                AuthUI.getInstance().signOut(this).addOnCompleteListener {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun toggleLocation(isEnabled: Boolean) {
        if (isEnabled) {
            startLocationUpdates()
            Toast.makeText(this, "GPS On", Toast.LENGTH_SHORT).show()
        } else {
            locationManager.removeUpdates(this)
            textViewStatus.text = "GPS Off"
            lastLocation = null
            Toast.makeText(this, "GPS Off", Toast.LENGTH_SHORT).show()
        }
        saveLocationPreference(isEnabled)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == locationPermissionCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show()
                startLocationUpdates()
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                textViewStatus.text = "No GPS permission"
            }
        }
    }

    private fun saveLocationPreference(isEnabled: Boolean) {
        getSharedPreferences("AppPreferences", Context.MODE_PRIVATE).edit().putBoolean("locationEnabled", isEnabled).apply()
    }

    private fun getLocationPreference(): Boolean {
        return getSharedPreferences("AppPreferences", Context.MODE_PRIVATE).getBoolean("locationEnabled", true)
    }

    override fun onProviderEnabled(provider: String) {}
    override fun onProviderDisabled(provider: String) {}
    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}

    override fun onPause() {
        super.onPause()
        overridePendingTransition(0, 0)
    }

    override fun onResume() {
        super.onResume()
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.menu.findItem(R.id.navigation_home).isChecked = true

        val isEnabled = getLocationPreference()
        if (isEnabled) {
            checkLocationPermissions()
        }
    }
}