package com.example.compustar

import android.app.Dialog
import android.content.ContentValues
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import androidx.fragment.app.Fragment
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.compustar.Adaptador.EquiposAreaAdapter
import com.example.compustar.Modelo.Equipo
import com.example.compustar.Modelo.Trabajador
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.FirebaseFirestore

class BusquedaFragment : Fragment(R.layout.fragment_busqueda) {

    private val equipoList = mutableListOf<Equipo>()
    private val trabajadorRead = mutableListOf<Trabajador>()
    private lateinit var rcvEquipos: RecyclerView
    private lateinit var adapter: EquiposAreaAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rcvEquipos = view.findViewById(R.id.rcvEquipos)
        rcvEquipos.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        adapter = EquiposAreaAdapter(equipoList){id, view, cliente, trabajador, precionado ->
            val data = equipoList.find { it.idEquipo == id }
            if (data != null){
                if (precionado){
                    val popupMenu= PopupMenu(requireContext(),view)
                    popupMenu.inflate(R.menu.menu_equipo)
                    popupMenu.setOnMenuItemClickListener { menu: MenuItem ->
                        when(menu.itemId){
                            R.id.op_cambiar -> {
                                showDialogCambiar(data.idEquipo,data.nIngreso,trabajador)
                                true
                            }
                            else -> false
                        }
                    }
                    popupMenu.show()
                }else{
                    val bundle = Bundle()
                    bundle.putString("id_equipo", data.idEquipo)
                    bundle.putString("cliente", cliente)
                    bundle.putString("trabajador", trabajador)
                    bundle.putString("falla", data.falla)
                    bundle.putString("fecha", data.fechaIngreso)
                    bundle.putString("observacion", data.observacion)
                    bundle.putString("observacionTecnico", data.obervacionTecnico)
                    bundle.putString("n_ingreso", data.nIngreso)
                    bundle.putString("fechaF", data.fechaFinalizacion)
                    val fragment = Equipo_PerfilFragment()
                    fragment.arguments = bundle
                    val fragmentManager = requireActivity().supportFragmentManager
                    val transaction = fragmentManager.beginTransaction()
                    transaction.replace(R.id.flHome, fragment)
                    transaction.addToBackStack(null)
                    transaction.commit()
                }

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

    private fun showDialogCambiar(idEquipo: String, nEquipo: String, respontable: String) {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dialog_tarea_cambiar)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        val txtIngreso : TextView = dialog.findViewById(R.id.txtIngreso)
        val txtResponsabla : TextView = dialog.findViewById(R.id.txtResponsable)
        val btnCompletar : Button = dialog.findViewById(R.id.btnFinalizarSi)
        val btnCancelar : MaterialButton = dialog.findViewById(R.id.btnFinalizarNo)
        val spnTrabajadores : Spinner = dialog.findViewById(R.id.spnTecnico)
        val nombresTrabajadores = trabajadorRead.map { it.nombre }.toTypedArray()
        var idTrabajadorSeleccionado = ""

        txtIngreso.text = "N° ingreso del equipo: " + nEquipo
        txtResponsabla.text = "Técnico actual: " + respontable

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, nombresTrabajadores)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spnTrabajadores.adapter = adapter

        spnTrabajadores.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val trabajadorSeleccionado = trabajadorRead[position]
                idTrabajadorSeleccionado = trabajadorSeleccionado.id_trabajador
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        btnCancelar.setOnClickListener {
            dialog.dismiss()
        }

        btnCompletar.setOnClickListener {
            val db = FirebaseFirestore.getInstance()
            val collection = db?.collection("equipos")
            val document = collection?.document(idEquipo)

            val tareaData = hashMapOf<String, Any>(
                "id_trabajador" to idTrabajadorSeleccionado
            )

            document?.update(tareaData)?.addOnSuccessListener {
                dialog.dismiss()
                Toast.makeText(requireContext(),"Se actualizo correctamente", Toast.LENGTH_SHORT).show()
                Log.d("TAG", "Estado de la tarea actualizado exitosamente")
            }
                ?.addOnFailureListener { e ->
                    Log.w("TAG", "Error al actualizar el estado de la tarea", e)
                }

        }
        dialog.show()
    }

}