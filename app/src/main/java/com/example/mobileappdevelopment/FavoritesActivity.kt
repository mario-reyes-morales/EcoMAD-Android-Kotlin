package com.example.mobileappdevelopment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobileappdevelopment.room.AppDatabase
import com.example.mobileappdevelopment.room.StationFavorite
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class FavoritesActivity : AppCompatActivity() {

    private lateinit var database: AppDatabase
    private lateinit var recyclerView: RecyclerView
    private lateinit var tvEmpty: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)

        database = AppDatabase.getDatabase(this)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "My favorites ⭐"

        recyclerView = findViewById(R.id.recyclerViewFavorites)
        tvEmpty = findViewById(R.id.tvEmpty)
        recyclerView.layoutManager = LinearLayoutManager(this)

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
            } else {
                false
            }
        }

        cargarFavoritos()
    }

    private fun cargarFavoritos() {
        lifecycleScope.launch {
            val favoritos = database.favoriteDao().getAllFavorites()

            if (favoritos.isEmpty()) {
                tvEmpty.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            } else {
                tvEmpty.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE

                recyclerView.adapter = FavoritosAdapter(
                    estaciones = favoritos,
                    onLongClick = { estacion ->
                        lifecycleScope.launch {
                            database.favoriteDao().deleteFavorite(estacion)
                            Toast.makeText(this@FavoritesActivity, "🗑️ Eliminada de favoritos", Toast.LENGTH_SHORT).show()
                            cargarFavoritos()
                        }
                    }
                )
            }
        }
    }

    override fun onPause() {
        super.onPause()
        overridePendingTransition(0, 0)
    }

    override fun onResume() {
        super.onResume()
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.menu.findItem(R.id.navigation_list).isChecked = true
        cargarFavoritos()
    }
}

class FavoritosAdapter(
    private val estaciones: List<StationFavorite>,
    private val onLongClick: (StationFavorite) -> Unit
) : RecyclerView.Adapter<FavoritosAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.txtStationName)
        val bikes: TextView = view.findViewById(R.id.txtBikesAvailable)
        val slots: TextView = view.findViewById(R.id.txtSlotsAvailable)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_station, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val estacion = estaciones[position]
        val context = holder.itemView.context

        holder.name.text = "⭐ ${estacion.name}"

        holder.bikes.text = context.getString(R.string.bikes_count, estacion.dockBikes)
        holder.slots.text = context.getString(R.string.docks_count, estacion.freeBases)

        holder.itemView.setOnLongClickListener {
            onLongClick(estacion)
            true
        }
    }

    override fun getItemCount() = estaciones.size
}