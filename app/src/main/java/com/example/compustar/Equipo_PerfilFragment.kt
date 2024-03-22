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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.compustar.Adaptador.EquiposAreaAdapter
import com.example.compustar.Adaptador.TareaAdapter
import com.example.compustar.Modelo.Equipo
import com.example.compustar.Modelo.Tarea
import com.example.compustar.Modelo.Trabajador
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.firebase.firestore.FirebaseFirestore

class Equipo_PerfilFragment : Fragment(R.layout.fragment_equipo_perfil) {

    private val tareaList = mutableListOf<Tarea>()
    private lateinit var rcvTareas: RecyclerView
    private lateinit var adapter: TareaAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bundle = arguments
        val id_equipo = bundle?.getString("id_equipo") ?: ""
        val cliente = bundle?.getString("cliente") ?: ""
        val trabajador = bundle?.getString("trabajador") ?: ""
        val falla = bundle?.getString("falla") ?: ""
        val fecha = bundle?.getString("fecha") ?: ""
        val observacion = bundle?.getString("observacion") ?: ""

        val txtCliente : TextView = view.findViewById(R.id.txtTitulo)
        val txtFalla : TextView = view.findViewById(R.id.txtFalla)
        val txtObservacion : TextView = view.findViewById(R.id.txtObservacion)
        val txtTrabajador : TextView = view.findViewById(R.id.txtTrabajador)
        val txtFecha : TextView = view.findViewById(R.id.txtFecha)

        txtCliente.text = cliente
        txtFalla.text = falla
        txtObservacion.text = observacion
        txtTrabajador.text = trabajador
        txtFecha.text = fecha

        rcvTareas = view.findViewById(R.id.rcvTareas)
        rcvTareas.layoutManager = FlexboxLayoutManager(requireContext(), FlexDirection.ROW, FlexWrap.WRAP)

        adapter = TareaAdapter(tareaList){id, view ->

        }
        rcvTareas.adapter = adapter

        readTarea(id_equipo)

    }

    fun readTarea(equipo:String) {
        val db = FirebaseFirestore.getInstance()
        val collectionTarea = db?.collection("tareas")
        collectionTarea?.whereEqualTo("id_equipo", equipo)?.get()?.addOnSuccessListener { result ->
            tareaList.clear()
            for (tareaDocument in result) {
                val id_equipo = tareaDocument.getString("id_equipo") ?: ""
                val falla = tareaDocument.getString("falla") ?: ""
                val descripcion = tareaDocument.getString("descripcion") ?: ""
                val fecha_finalizacion = tareaDocument.getString("fecha_finalizacion") ?: ""
                val estado = tareaDocument.getBoolean("estado") ?: false
                val id = tareaDocument.id
                val tareas = Tarea(id,id_equipo,falla,descripcion,fecha_finalizacion,estado)
                tareaList.add(tareas)
            }
            adapter.notifyDataSetChanged()

        }?.addOnFailureListener { exception ->
            Log.w("Error: ", "Error getting documents: ", exception)
        }
    }
}