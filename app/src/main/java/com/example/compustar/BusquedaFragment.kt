package com.example.compustar

import android.content.ContentValues
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.compustar.Adaptador.EquiposAreaAdapter
import com.example.compustar.Modelo.Equipo
import com.google.firebase.firestore.FirebaseFirestore

class BusquedaFragment : Fragment(R.layout.fragment_busqueda) {

    private val equipoList = mutableListOf<Equipo>()
    private lateinit var rcvEquipos: RecyclerView
    private lateinit var adapter: EquiposAreaAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rcvEquipos = view.findViewById(R.id.rcvEquipos)
        rcvEquipos.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        adapter = EquiposAreaAdapter(equipoList){id, view, cliente, trabajador ->
            val data = equipoList.find { it.idEquipo == id }
            if (data != null){
                val bundle = Bundle()
                bundle.putString("id_equipo", data.idEquipo)
                bundle.putString("cliente", cliente)
                bundle.putString("trabajador", trabajador)
                bundle.putString("falla", data.falla)
                bundle.putString("fecha", data.fechaIngreso)
                bundle.putString("observacion", data.observacion)
                bundle.putString("n_ingreso", data.nIngreso)
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
        readEquipo()

        val txtBusqueda: EditText = view.findViewById(R.id.txtBusqueda)

        txtBusqueda.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                readEquipo(s.toString())
            }
        })

    }

    fun readEquipo() {
        val db = FirebaseFirestore.getInstance()
        val collection = db?.collection("equipos")

        collection?.get()?.addOnSuccessListener { task ->
            equipoList.clear()
            for (document in task) {
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
                val idArea = document.getString("id_area") ?: ""
                val prioridad = document.getString("prioridad") ?: ""
                val observacionTecnico = document.getString("observacionTecnico") ?: ""
                val id = document.id
                val equipos = Equipo(id, idCliente, idTrabajador, nIngreso, equipo, nSerie, marca, modelo,
                    fechaIngreso, fechaFinalizacion, falla, observacion, estado, idArea, prioridad, observacionTecnico)
                equipoList.add(equipos)
            }
            equipoList.sortBy { it.estado }
            adapter.notifyDataSetChanged()
        }

    }

    fun readEquipo(searchText: String = "") {
        val db = FirebaseFirestore.getInstance()
        val collection = db.collection("equipos")

        // Aplica la búsqueda solo si hay un texto de búsqueda
        val query = if (searchText.isNotBlank()) {
            collection.whereEqualTo("n_ingreso", searchText)
        } else {
            collection
        }

        query.get().addOnSuccessListener { task ->
            equipoList.clear()
            for (document in task) {
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
                val idArea = document.getString("id_area") ?: ""
                val prioridad = document.getString("prioridad") ?: ""
                val observacionTecnico = document.getString("observacionTecnico") ?: ""
                val id = document.id
                val equipos = Equipo(id, idCliente, idTrabajador, nIngreso, equipo, nSerie, marca, modelo,
                    fechaIngreso, fechaFinalizacion, falla, observacion, estado, idArea, prioridad, observacionTecnico)
                equipoList.add(equipos)
            }
            equipoList.sortBy { it.estado }
            adapter.notifyDataSetChanged()
        }
    }

}