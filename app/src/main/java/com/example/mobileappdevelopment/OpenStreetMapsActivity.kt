package com.example.mobileappdevelopment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.util.Log
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.lifecycleScope
import com.example.mobileappdevelopment.bicimad.BiciMadClient
import com.example.mobileappdevelopment.bicimad.Station
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

class OpenStreetMapsActivity : AppCompatActivity(), LocationListener {
    private lateinit var map: MapView
    private lateinit var locationOverlay: MyLocationNewOverlay
    private lateinit var locationManager: LocationManager
    private val TAG = "OpenStreetMapView"

    private var miLatitud: Double = 0.0
    private var miLongitud: Double = 0.0

    private var listaOriginalEstaciones: List<Station> = emptyList()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_open_street_maps)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

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
                startActivity(Intent(this, targetActivity).apply { flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT })
                overridePendingTransition(0, 0)
                true
            } else false
        }

        Configuration.getInstance().userAgentValue = packageName
        Configuration.getInstance().load(applicationContext, getSharedPreferences("osm", MODE_PRIVATE))

        map = findViewById(R.id.map)
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)

        val provider = GpsMyLocationProvider(this)
        locationOverlay = MyLocationNewOverlay(provider, map)
        locationOverlay.enableMyLocation()
        locationOverlay.enableFollowLocation()
        map.overlays.add(locationOverlay)

        val icon = ContextCompat.getDrawable(this, android.R.drawable.ic_menu_mylocation)?.toBitmap()
        if (icon != null) {
            locationOverlay.setPersonIcon(icon)
            locationOverlay.setDirectionArrow(icon, icon)
        }

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        activarGps()
        descargarBicisUnaVez()
    }

    private fun activarGps() {
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 5f, this)
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 5f, this)

            val last = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                ?: locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

            last?.let {
                miLatitud = it.latitude
                miLongitud = it.longitude
                map.controller.setZoom(17.0)
                map.controller.setCenter(GeoPoint(miLatitud, miLongitud))
            }
        } catch (e: SecurityException) {
            Log.e(TAG, "Faltan permisos de GPS")
        }
    }

    override fun onLocationChanged(location: Location) {
        miLatitud = location.latitude
        miLongitud = location.longitude
        Log.d(TAG, "Me he movido a: $miLatitud, $miLongitud")

        if (listaOriginalEstaciones.isNotEmpty()) {
            actualizarPuntosEnMapa()
        }
    }

    private fun descargarBicisUnaVez() {
        lifecycleScope.launch {
            try {
                val resLogin = BiciMadClient.service.login(Secrets.EMT_CLIENT_ID, Secrets.EMT_PASS)
                val token = resLogin.data[0].accessToken
                val resBicis = BiciMadClient.service.getStations(token)

                if (resBicis.data.isNotEmpty()) {
                    listaOriginalEstaciones = resBicis.data
                    runOnUiThread { actualizarPuntosEnMapa() }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error bajando bicis: ${e.message}")
            }
        }
    }

    private fun actualizarPuntosEnMapa() {
        val top10 = listaOriginalEstaciones.sortedBy { s ->
            val lon = s.geometry.coordinates[0]
            val lat = s.geometry.coordinates[1]
            val results = FloatArray(1)
            Location.distanceBetween(miLatitud, miLongitud, lat, lon, results)
            results[0]
        }.take(10)

        runOnUiThread {
            map.overlays.removeAll { it is Marker }

            top10.forEach { estacion ->
                val lon = estacion.geometry.coordinates[0]
                val lat = estacion.geometry.coordinates[1]

                val marker = Marker(map)
                marker.position = GeoPoint(lat, lon)
                marker.title = "${estacion.name}\nLibres: ${estacion.dock_bikes}"
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

                map.overlays.add(marker)
            }

            map.invalidate()
        }
    }

    override fun onPause() {
        super.onPause()
        map.onPause()
        if (::locationOverlay.isInitialized) locationOverlay.disableMyLocation()
    }

    override fun onResume() {
        super.onResume()
        map.onResume()
        findViewById<BottomNavigationView>(R.id.nav_view).menu.findItem(R.id.navigation_map).isChecked = true

        val isEnabled = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE).getBoolean("locationEnabled", true)
        if (isEnabled && ::locationOverlay.isInitialized) {
            locationOverlay.enableMyLocation()
            locationOverlay.enableFollowLocation()
        }
        activarGps()
    }

    override fun onProviderEnabled(p: String) {}
    override fun onProviderDisabled(p: String) {}
    override fun onStatusChanged(p: String?, s: Int, e: Bundle?) {}
}