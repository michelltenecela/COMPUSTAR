package com.example.compustar.Adaptador

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.compustar.Modelo.Tarea
import com.example.compustar.R

class TareaAdapter(private val tarea: List<Tarea>) : RecyclerView.Adapter<TareaAdapter.TareaViewHolder>() {


    class TareaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tarea: TextView = itemView.findViewById(R.id.txtTarea)
        val estado: ImageView = itemView.findViewById(R.id.imgEstado)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TareaViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_equipo_tareas, parent, false)
        return TareaViewHolder(itemView)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: TareaViewHolder, position: Int) {
        val data = tarea[position]
        holder.tarea.text = data.descripcion
        if(data.estado){
            holder.estado.setImageResource(R.drawable.tarea_terminada)
        }else{
            holder.estado.setImageResource(R.drawable.tarea_faltante)
        }
    }

    override fun getItemCount(): Int {
        return tarea.size
    }
}