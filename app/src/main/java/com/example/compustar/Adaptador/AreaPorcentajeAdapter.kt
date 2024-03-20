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
import com.example.compustar.Modelo.Trabajador
import com.example.compustar.R
import com.google.firebase.firestore.FirebaseFirestore

class AreaPorcentajeAdapter(private val area: List<Area>, private val onItemClick: (String, View) -> Unit) : RecyclerView.Adapter<AreaPorcentajeAdapter.AreaViewHolder>() {


    class AreaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val porcentaje: TextView = itemView.findViewById(R.id.txtPorcentaje)
        val nombre: TextView = itemView.findViewById(R.id.txtArea)
        val pbporcentaje: ProgressBar = itemView.findViewById(R.id.pbArea)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AreaViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_home_area_porcentaje, parent, false)
        return AreaViewHolder(itemView)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: AreaViewHolder, position: Int) {
        val areaDato = area[position]
        holder.nombre.text = areaDato.nombre
        holder.itemView.setOnClickListener {
            onItemClick(areaDato.id_area,it)
        }

        val db = FirebaseFirestore.getInstance()
        val collection = db?.collection("trabajadores")
        collection?.get()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val trabajadorRead = mutableListOf<Trabajador>()
                var cantidadEquipos = 0
                var equiposReparados = 0
                for (document in task.result) {
                    val nombre = document.getString("nombre") ?: ""
                    val cedula = document.getString("cedula") ?: ""
                    val email = document.getString("email") ?: ""
                    val contraseña = document.getString("contraseña") ?: ""
                    val id_area = document.getString("id_area") ?: ""
                    val id = document.id
                    val trabajador = Trabajador(id,email,nombre,cedula,contraseña,id_area)
                    trabajadorRead.add(trabajador)

                }
                val trabajadorList = trabajadorRead.filter { it.id_area == areaDato.id_area } as MutableList<Trabajador>
                for (data in trabajadorList) {
                    val collectionEquipo = db?.collection("equipos")
                    collectionEquipo?.whereEqualTo("id_trabajador", data.id_trabajador)?.get()?.addOnSuccessListener { result ->
                        for (equipoDocument in result) {
                            val estado = equipoDocument.getBoolean("estado") ?: false
                            cantidadEquipos++
                            Log.w("Datos: ", cantidadEquipos.toString())
                            if (!estado) {
                                equiposReparados++
                            }
                        }
                        holder.porcentaje.text = ((equiposReparados.toFloat()/cantidadEquipos.toFloat())*100).toInt().toString() + "%"
                        holder.pbporcentaje.progress = ((equiposReparados.toFloat()/cantidadEquipos.toFloat())*100).toInt()
                    }?.addOnFailureListener { exception ->
                        Log.w("Error: ", "Error getting documents: ", exception)
                    }
                }


            } else {
                Log.w(ContentValues.TAG, "Error getting documents.", task.exception)
            }
        }

    }

    override fun getItemCount(): Int {
        return area.size
    }
}
