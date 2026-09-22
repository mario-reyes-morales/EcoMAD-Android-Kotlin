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
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobileappdevelopment.bicimad.BiciMadClient
import com.example.mobileappdevelopment.bicimad.Station
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class BiciMadActivity : AppCompatActivity(), LocationListener {

    private val TAG = "BiciMad_Debug"
    private lateinit var locationManager: LocationManager

    private var miLatitud: Double = 0.0
    private var miLongitud: Double = 0.0
    private var listaEstaciones: List<Station> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bicimad)

        val recyclerView: RecyclerView = findViewById(R.id.rvStations)
        recyclerView.layoutManager = LinearLayoutManager(this)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        activarGps()
        obtenerBicis()

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.setOnNavigationItemSelectedListener { item ->
            val target = when (item.itemId) {
                R.id.navigation_home -> MainActivity::class.java
                R.id.navigation_map -> OpenStreetMapsActivity::class.java
                R.id.navigation_bicimad -> BiciMadActivity::class.java
                R.id.navigation_list -> FavoritesActivity::class.java
                else -> null
            }
            if (target != null && this::class.java != target) {
                val intent = Intent(this, target)
                intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                startActivity(intent)
                overridePendingTransition(0, 0)
                true
            } else false
        }
    }

    private fun activarGps() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1f, this)
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1f, this)

            val last = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                ?: locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

            last?.let {
                miLatitud = it.latitude
                miLongitud = it.longitude
            }
        }
    }

    override fun onLocationChanged(location: Location) {
        miLatitud = location.latitude
        miLongitud = location.longitude
        Log.d(TAG, "GPS: $miLatitud, $miLongitud")
        if (listaEstaciones.isNotEmpty()) {
            actualizarUI()
        }
    }

    private fun obtenerBicis() {
        lifecycleScope.launch {
            try {
                val resLogin = BiciMadClient.service.login(Secrets.EMT_CLIENT_ID, Secrets.EMT_PASS)
                val token = resLogin.data[0].accessToken
                val resBicis = BiciMadClient.service.getStations(token)

                if (resBicis.data.isNotEmpty()) {
                    listaEstaciones = resBicis.data
                    actualizarUI()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error red: ${e.message}")
            }
        }
    }

    private fun actualizarUI() {
        val listaOrdenada = if (miLatitud != 0.0) {
            listaEstaciones.sortedBy { s ->
                val lon = s.geometry.coordinates[0]
                val lat = s.geometry.coordinates[1]

                val results = FloatArray(1)
                android.location.Location.distanceBetween(miLatitud, miLongitud, lat, lon, results)
                results[0]
            }
        } else {
            listaEstaciones.sortedBy { it.name }
        }

        runOnUiThread {
            findViewById<TextView>(R.id.txtStatus).visibility = View.GONE
            val rv: RecyclerView = findViewById(R.id.rvStations)
            rv.adapter = StationAdapter(listaOrdenada)
        }
    }

    private fun parsearGeometry(geom: String): DoubleArray {
        return try {
            val limpia = geom.replace("POINT(", "").replace(")", "").trim()
            val partes = limpia.split(" ")
            doubleArrayOf(partes[0].toDouble(), partes[1].toDouble()) // [Longitud, Latitud]
        } catch (e: Exception) {
            doubleArrayOf(0.0, 0.0)
        }
    }

    override fun onResume() {
        super.onResume()
        findViewById<BottomNavigationView>(R.id.nav_view).menu.findItem(R.id.navigation_bicimad).isChecked = true
        activarGps()
    }

    override fun onProviderEnabled(p: String) {}
    override fun onProviderDisabled(p: String) {}
    override fun onStatusChanged(p: String?, s: Int, e: Bundle?) {}
}