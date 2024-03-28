package com.example.compustar.Adaptador

import android.content.ContentValues
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.compustar.Modelo.Area
import com.example.compustar.Modelo.Filtro
import com.example.compustar.Modelo.Trabajador
import com.example.compustar.R
import com.google.firebase.firestore.FirebaseFirestore

class FiltroAdapter (private val filtro: List<Filtro>, private val onItemClick: (String, View, Boolean, Int) -> Unit) : RecyclerView.Adapter<FiltroAdapter.FiltroViewHolder>() {

    fun updateItemStatus(position: Int, estado: Boolean) {
        filtro.forEachIndexed { index, item ->
            if(index == position){
                item.estado = estado
                notifyItemChanged(position)
            }else{
                item.estado = false
                notifyItemChanged(index)
            }

        }
    }

    class FiltroViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombre: TextView = itemView.findViewById(R.id.txtNombre)
        val crdFiltro: CardView = itemView.findViewById(R.id.crdFiltro)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FiltroViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_filtro_trabajador, parent, false)
        return FiltroViewHolder(itemView)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: FiltroViewHolder, position: Int) {
        val dato = filtro[position]
        holder.nombre.text = dato.nombre
        if (dato.estado){
            holder.crdFiltro.setCardBackgroundColor(Color.parseColor("#84DBFF"))
        }else{
            holder.crdFiltro.setCardBackgroundColor(Color.parseColor("#FFFFFF"))
        }

        holder.itemView.setOnClickListener {
            onItemClick(dato.id_trabajador,it, dato.estado, position)
        }


    }


    override fun getItemCount(): Int {
        return filtro.size
    }
}