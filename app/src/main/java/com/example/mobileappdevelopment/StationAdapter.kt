package com.example.mobileappdevelopment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.mobileappdevelopment.bicimad.Station
import com.example.mobileappdevelopment.room.AppDatabase
import com.example.mobileappdevelopment.room.StationFavorite
import kotlinx.coroutines.launch

class StationAdapter(private val stations: List<Station>) :
    RecyclerView.Adapter<StationAdapter.StationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StationViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_station, parent, false)
        return StationViewHolder(view)
    }

    override fun onBindViewHolder(holder: StationViewHolder, position: Int) {
        val item = stations[position]
        val context = holder.itemView.context

        holder.name.text = item.name
        holder.bikes.text = context.getString(R.string.bikes_count, item.dock_bikes)
        holder.slots.text = context.getString(R.string.docks_count, item.free_bases)

        holder.itemView.setOnLongClickListener {
            val db = AppDatabase.getDatabase(context)

            val favorite = StationFavorite(
                id = item.id,
                name = item.name,
                address = item.address,
                latitude = item.geometry.coordinates[1],
                longitude = item.geometry.coordinates[0],
                dockBikes = item.dock_bikes,
                freeBases = item.free_bases
            )

            (context as AppCompatActivity).lifecycleScope.launch {
                db.favoriteDao().insertFavorite(favorite)
                Toast.makeText(context, "⭐ ${item.name} added to favorites!", Toast.LENGTH_SHORT).show()
            }
            true
        }
    }

    override fun getItemCount(): Int = stations.size

    class StationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.txtStationName)
        val bikes: TextView = view.findViewById(R.id.txtBikesAvailable)
        val slots: TextView = view.findViewById(R.id.txtSlotsAvailable)
    }
}