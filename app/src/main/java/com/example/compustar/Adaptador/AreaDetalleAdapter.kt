package com.example.compustar.Adaptador

import android.content.ContentValues
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.compustar.Modelo.Area
import com.example.compustar.Modelo.Trabajador
import com.example.compustar.R
import com.google.firebase.firestore.FirebaseFirestore

class AreaDetalleAdapter(private val area: List<Area>, private val onItemClick: (String, View, Int, Int) -> Unit) : RecyclerView.Adapter<AreaDetalleAdapter.AreaDetalleViewHolder>() {


    class AreaDetalleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val equipos: TextView = itemView.findViewById(R.id.txtEquipos)
        val nombre: TextView = itemView.findViewById(R.id.txtNombre)
        val rcvTrabajador: RecyclerView = itemView.findViewById(R.id.rcvTrabajadores)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AreaDetalleViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_home_area_detalle, parent, false)
        return AreaDetalleViewHolder(itemView)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: AreaDetalleViewHolder, position: Int) {
        val areaDato = area[position]
        var cantidadEquipos = 0
        var equiposReparados = 0
        holder.nombre.text = areaDato.nombre
        lateinit var adapter: TrabajadorHomeAdapter
        holder.rcvTrabajador.layoutManager = LinearLayoutManager(holder.itemView.context, LinearLayoutManager.HORIZONTAL, false)


        holder.itemView.setOnClickListener {
            onItemClick(areaDato.id_area,it, cantidadEquipos,equiposReparados)
        }

        val db = FirebaseFirestore.getInstance()
        val collection = db?.collection("trabajadores")
        collection?.get()?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val trabajadorRead = mutableListOf<Trabajador>()

                    for (document in task.result) {
                        val nombre = document.getString("nombre") ?: ""
                        val cedula = document.getString("cedula") ?: ""
                        val email = document.getString("email") ?: ""
                        val contraseña = document.getBoolean("contraseña") ?: false
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
                                if (estado) {
                                    equiposReparados++
                                }
                            }
                            holder.equipos.text = "Equipos reparados " + equiposReparados + "/" + cantidadEquipos
                        }?.addOnFailureListener { exception ->
                            Log.w("Error: ", "Error getting documents: ", exception)
                        }
                    }

                    adapter = TrabajadorHomeAdapter(trabajadorList){id, view ->
                        val area = trabajadorList.find { it.id_trabajador == id }
                        if (area != null){
                            Log.i("id_area: ", area.id_trabajador)
                        }
                    }

                    holder.rcvTrabajador.adapter = adapter


                } else {
                    Log.w(ContentValues.TAG, "Error getting documents.", task.exception)
                }
            }
    }

    override fun getItemCount(): Int {
        return area.size
    }

}