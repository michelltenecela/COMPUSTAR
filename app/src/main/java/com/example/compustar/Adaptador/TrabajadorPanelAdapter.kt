package com.example.compustar.Adaptador

import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.compustar.Modelo.Trabajador
import com.example.compustar.R
import com.google.firebase.firestore.FirebaseFirestore

class TrabajadorPanelAdapter(private val trabajador: List<Trabajador>, private val onItemClick: (String, View) -> Unit) : RecyclerView.Adapter<TrabajadorPanelAdapter.TrabajadorPanelViewHolder>() {


    class TrabajadorPanelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombre: TextView = itemView.findViewById(R.id.txtNombre)
        val imgFoto: ImageView = itemView.findViewById(R.id.imgFoto)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrabajadorPanelViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_panel_trabajadores, parent, false)
        return TrabajadorPanelViewHolder(itemView)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: TrabajadorPanelViewHolder, position: Int) {
        val dato = trabajador[position]
        holder.nombre.text = dato.nombre
        holder.itemView.setOnClickListener {
            onItemClick(dato.id_trabajador,it)
        }

    }

    override fun getItemCount(): Int {
        return trabajador.size
    }
}