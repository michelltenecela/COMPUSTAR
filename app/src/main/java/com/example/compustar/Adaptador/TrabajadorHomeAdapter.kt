package com.example.compustar.Adaptador

import android.content.ContentValues
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
import com.example.compustar.Modelo.Area
import com.example.compustar.Modelo.Trabajador
import com.example.compustar.R
import com.google.firebase.firestore.FirebaseFirestore

class TrabajadorHomeAdapter(private val trabajador: List<Trabajador>, private val onItemClick: (String, View) -> Unit) : RecyclerView.Adapter<TrabajadorHomeAdapter.TrabajadorHomeViewHolder>() {


    class TrabajadorHomeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val equipos: TextView = itemView.findViewById(R.id.txtEquiposReparados)
        val nombre: TextView = itemView.findViewById(R.id.txtNombre)
        val porcentaje: TextView = itemView.findViewById(R.id.txtPorcentaje)
        val pbporcentaje: ProgressBar = itemView.findViewById(R.id.pbPorcentaje)
        val imgFoto: ImageView = itemView.findViewById(R.id.imgFoto)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrabajadorHomeViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_home_trabajador, parent, false)
        return TrabajadorHomeViewHolder(itemView)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: TrabajadorHomeViewHolder, position: Int) {
        val trabajadorDato = trabajador[position]
        holder.nombre.text = trabajadorDato.nombre
        /*holder.itemView.setOnClickListener {
            onItemClick(areaDato.id_area,it)
        }*/

        val db = FirebaseFirestore.getInstance()
        val collection = db?.collection("equipos")
        collection?.whereEqualTo("id_trabajador", trabajadorDato.id_trabajador)?.get()?.addOnSuccessListener { result ->
            var cantidadEquipos = 0
            var equiposReparados = 0
            for (equipoDocument in result) {
                val estado = equipoDocument.getBoolean("estado") ?: false
                cantidadEquipos++
                if (!estado) {
                    equiposReparados++
                }
            }
            holder.equipos.text = "Reparados " + equiposReparados + "/" + cantidadEquipos
            if (cantidadEquipos!= 0){
                holder.porcentaje.text = ((equiposReparados.toFloat()/cantidadEquipos.toFloat())*100).toInt().toString() + "%"
                holder.pbporcentaje.progress = ((equiposReparados.toFloat()/cantidadEquipos.toFloat())*100).toInt()

            }
        }?.addOnFailureListener { exception ->
            Log.w("Error: ", "Error getting documents: ", exception)
        }

    }

    override fun getItemCount(): Int {
        return trabajador.size
    }
}