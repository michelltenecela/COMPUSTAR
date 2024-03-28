package com.example.compustar

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.compustar.Adaptador.AreaDetalleAdapter
import com.example.compustar.Adaptador.AreaPorcentajeAdapter
import com.example.compustar.Modelo.Area
import com.google.firebase.firestore.FirebaseFirestore

class HomeFragment : Fragment(R.layout.fragment_home) {

    private val areaList = mutableListOf<Area>()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AreaPorcentajeAdapter

    private lateinit var rcvAreaDetalle: RecyclerView
    private lateinit var adapterAreaDetalle: AreaDetalleAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val txtPorcentaje : TextView = view.findViewById(R.id.txtPorcentaje)
        val pbPorcentaje : ProgressBar = view.findViewById(R.id.pbtotalArea)

        recyclerView = view.findViewById(R.id.rcvArea)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
        rcvAreaDetalle = view.findViewById(R.id.rcvAreaDetalle)
        rcvAreaDetalle.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        adapter = AreaPorcentajeAdapter(areaList){id, view, cantidad, reparados ->
            val area = areaList.find { it.id_area == id }
            if (area != null){
                val bundle = Bundle()
                bundle.putString("id_area", area.id_area)
                bundle.putString("area_nombre", area.nombre)
                bundle.putInt("cantidad", cantidad)
                bundle.putInt("reparados", reparados)
                val fragment = Area_trabajo()
                fragment.arguments = bundle
                val fragmentManager = requireActivity().supportFragmentManager
                val transaction = fragmentManager.beginTransaction()
                transaction.replace(R.id.flHome, fragment)
                transaction.addToBackStack(null)
                transaction.commit()
            }
        }
        recyclerView.adapter = adapter

        adapterAreaDetalle = AreaDetalleAdapter(areaList, requireContext()){id, view, cantidad, reparados ->
            val area = areaList.find { it.id_area == id }
            if (area != null){
                val bundle = Bundle()
                bundle.putString("id_area", area.id_area)
                bundle.putString("area_nombre", area.nombre)
                bundle.putInt("cantidad", cantidad)
                bundle.putInt("reparados", reparados)
                val fragment = Area_trabajo()
                fragment.arguments = bundle
                val fragmentManager = requireActivity().supportFragmentManager
                val transaction = fragmentManager.beginTransaction()
                transaction.replace(R.id.flHome, fragment)
                transaction.addToBackStack(null)
                transaction.commit()
            }
        }
        rcvAreaDetalle.adapter = adapterAreaDetalle

        readArea()
        totalArea(txtPorcentaje,pbPorcentaje)
    }

    fun readArea() {
        val db = FirebaseFirestore.getInstance()
        val collection = db?.collection("areas")

        collection?.get()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    areaList.clear()
                    for (document in task.result) {
                        val nombre = document.getString("nombre") ?: ""
                        val estado = document.getBoolean("estado") ?: false
                        val id = document.id
                        val area = Area(id,nombre, estado)
                        areaList.add(area)
                    }
                    adapter.notifyDataSetChanged()
                    adapterAreaDetalle.notifyDataSetChanged()
                } else {
                    Log.w(TAG, "Error getting documents.", task.exception)
                }
            }
    }

    fun totalArea(porcentaje : TextView, pbporcentaje : ProgressBar) {
        val db = FirebaseFirestore.getInstance()
        val collection = db?.collection("equipos")

        collection?.get()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    var cantidadEquipos = 0
                    var equiposReparados = 0
                    for (document in task.result) {
                        val estado = document.getBoolean("estado") ?: false
                        cantidadEquipos++
                        Log.w("Datos: ", estado.toString())
                        if (estado) {
                            equiposReparados++
                        }
                    }
                    if (cantidadEquipos!= 0){
                        porcentaje.text = ((equiposReparados.toFloat()/cantidadEquipos.toFloat())*100).toInt().toString() + "%"
                        pbporcentaje.progress = ((equiposReparados.toFloat()/cantidadEquipos.toFloat())*100).toInt()
                    }
                } else {
                    Log.w(TAG, "Error getting documents.", task.exception)
                }
            }
    }


}