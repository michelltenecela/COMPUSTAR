package com.example.compustar.Adaptador

import android.content.ContentValues
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.compustar.Modelo.Area
import com.example.compustar.Modelo.Equipo
import com.example.compustar.Modelo.Trabajador
import com.example.compustar.R
import com.google.firebase.firestore.FirebaseFirestore

class EquiposAreaAdapter(private val equipo: List<Equipo>, private val onItemClick: (String, View, String, String) -> Unit) : RecyclerView.Adapter<EquiposAreaAdapter.EquiposAreaViewHolder>() {


    class EquiposAreaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tareas: TextView = itemView.findViewById(R.id.txtTareas)
        val nombre: TextView = itemView.findViewById(R.id.txtNombre)
        val pbtareas: ProgressBar = itemView.findViewById(R.id.pbTareas)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EquiposAreaViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_area_equipos, parent, false)
        return EquiposAreaViewHolder(itemView)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: EquiposAreaViewHolder, position: Int) {
        val equipoDato = equipo[position]
        var trabajador = ""

        holder.itemView.setOnClickListener {
            onItemClick(equipoDato.idEquipo,it,holder.nombre.text.toString(),trabajador)
        }

        val db = FirebaseFirestore.getInstance()
        val collection = db?.collection("clientes")
        collection?.document(equipoDato.idCliente)?.get()?.addOnSuccessListener { task ->
            holder.nombre.text = task.getString("nombre") ?: ""
        }

        val collectionTrabajador = db?.collection("trabajadores")
        collectionTrabajador?.document(equipoDato.idTrabajador)?.get()?.addOnSuccessListener { task ->
            trabajador = task.getString("nombre") ?: ""
        }


        val collectionTarea = db?.collection("tareas")
        collectionTarea?.whereEqualTo("id_equipo", equipoDato.idEquipo)?.get()?.addOnSuccessListener { result ->
            var cantidadTareas = 0
            var tareasCompletadas = 0
            for (tareaDocument in result) {
                val estado = tareaDocument.getBoolean("estado") ?: false
                cantidadTareas++
                if (!estado) {
                    tareasCompletadas++
                }
            }
            holder.tareas.text = tareasCompletadas.toString() + "/" + cantidadTareas.toString() + "Tareas realizadas"
            holder.pbtareas.progress = ((tareasCompletadas.toFloat()/cantidadTareas.toFloat())*100).toInt()
        }?.addOnFailureListener { exception ->
            Log.w("Error: ", "Error getting documents: ", exception)
        }

    }

    override fun getItemCount(): Int {
        return equipo.size
    }
}