package com.example.compustar.Adaptador

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.compustar.Modelo.PanelControl
import com.example.compustar.Modelo.Tarea
import com.example.compustar.R

class PanelControlAdapter (private val panel: List<PanelControl>, private val onItemClick: (Int, View) -> Unit) : RecyclerView.Adapter<PanelControlAdapter.PanelControlViewHolder>() {


    class PanelControlViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombre: TextView = itemView.findViewById(R.id.txtNombre)
        val foto: ImageView = itemView.findViewById(R.id.imgFoto)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PanelControlViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_panel_control, parent, false)
        return PanelControlViewHolder(itemView)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: PanelControlViewHolder, position: Int) {
        val data = panel[position]

        holder.nombre.text = data.nombre

        if (data.foto == "trabajador"){
            holder.foto.setImageResource(R.drawable.panel_trabajador)
        }else if (data.foto == "area"){
            holder.foto.setImageResource(R.drawable.panel_area)
        }else if (data.foto == "historial"){
            holder.foto.setImageResource(R.drawable.panel_historia)
        }


        holder.itemView.setOnClickListener {
            onItemClick(data.id_panel,it)
        }
    }

    override fun getItemCount(): Int {
        return panel.size
    }
}