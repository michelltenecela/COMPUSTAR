package com.example.compustar

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.compustar.Adaptador.AreaPorcentajeAdapter
import com.example.compustar.Adaptador.EquiposAreaAdapter
import com.example.compustar.Modelo.Area
import com.example.compustar.Modelo.Equipo
import com.example.compustar.Modelo.Trabajador
import com.google.firebase.firestore.FirebaseFirestore


class Area_trabajo : Fragment(R.layout.fragment_area_trabajo) {

    private val equipoList = mutableListOf<Equipo>()
    private lateinit var rcvEquipos: RecyclerView
    private lateinit var adapter: EquiposAreaAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bundle = arguments
        val id_area = bundle?.getString("id_area") ?: ""
        val area_nombre = bundle?.getString("area_nombre") ?: ""

        val txtTitulo : TextView = view.findViewById(R.id.txtTitulo)

        txtTitulo.text = area_nombre

        rcvEquipos = view.findViewById(R.id.rcvEquipos)
        rcvEquipos.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        adapter = EquiposAreaAdapter(equipoList){id, view ->
            val data = equipoList.find { it.idEquipo == id }
            if (data != null){
                val bundle = Bundle()
                bundle.putString("id_tarea", data.idEquipo)
                val fragment = Equipo_PerfilFragment()
                fragment.arguments = bundle
                val fragmentManager = requireActivity().supportFragmentManager
                val transaction = fragmentManager.beginTransaction()
                transaction.replace(R.id.flHome, fragment)
                transaction.addToBackStack(null)
                transaction.commit()
            }
        }
        rcvEquipos.adapter = adapter

        readEquipo(id_area)

        val crdFiltro : CardView = view.findViewById(R.id.crdFiltro)
        crdFiltro.visibility = View.GONE

        val btnFiltro : ImageButton = view.findViewById(R.id.ibtnFiltrar)
        btnFiltro.setOnClickListener {
            if (crdFiltro.visibility == View.GONE){
                crdFiltro.visibility = View.VISIBLE
                btnFiltro.setImageResource(R.drawable.filtrar_clic)
            }else {
                crdFiltro.visibility = View.GONE
                btnFiltro.setImageResource(R.drawable.filtrar)
            }
        }

    }

    fun readEquipo(area:String) {
        val db = FirebaseFirestore.getInstance()
        val collection = db?.collection("equipos")

        val collectionTrabajo = db?.collection("trabajadores")
        collectionTrabajo?.get()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val trabajadorRead = mutableListOf<Trabajador>()
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
                val trabajadorList = trabajadorRead.filter { it.id_area == area } as MutableList<Trabajador>
                collection?.get()?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        equipoList.clear()
                        for (document in task.result) {
                            val idCliente = document.getString("id_cliente") ?: ""
                            val idTrabajador = document.getString("id_trabajador") ?: ""
                            val nIngreso = document.getString("n_ingreso") ?: ""
                            val equipo = document.getString("equipo") ?: ""
                            val nSerie = document.getString("n_serie") ?: ""
                            val marca = document.getString("marca") ?: ""
                            val modelo = document.getString("modelo") ?: ""
                            val fechaIngreso = document.getString("fecha_ingreso") ?: ""
                            val fechaFinalizacion = document.getString("fecha_finalizacion") ?: ""
                            val falla = document.getString("falla") ?: ""
                            val observacion = document.getString("observacion") ?: ""
                            val estado = document.getBoolean("estado") ?: false
                            val id = document.id
                            val equipos = Equipo(id,idCliente, idTrabajador, nIngreso, equipo, nSerie, marca, modelo, fechaIngreso, fechaFinalizacion, falla, observacion, estado)
                            val equipoTrabajador = trabajadorList.find { it.id_trabajador == idTrabajador }
                            if (equipoTrabajador != null) {
                                equipoList.add(equipos)
                            }
                        }
                        adapter.notifyDataSetChanged()
                    } else {
                        Log.w(ContentValues.TAG, "Error getting documents.", task.exception)
                    }
                }
            }
        }
    }
}