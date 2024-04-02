package com.example.compustar

import android.app.Dialog
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.compustar.Adaptador.AreaPorcentajeAdapter
import com.example.compustar.Adaptador.EquiposAreaAdapter
import com.example.compustar.Adaptador.FiltroAdapter
import com.example.compustar.Modelo.Area
import com.example.compustar.Modelo.Equipo
import com.example.compustar.Modelo.Filtro
import com.example.compustar.Modelo.Trabajador
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.FirebaseFirestore


class Area_trabajo : Fragment(R.layout.fragment_area_trabajo) {

    private val equipoList = mutableListOf<Equipo>()
    private val trabajadorRead = mutableListOf<Trabajador>()
    private val filtroList = mutableListOf<Filtro>()
    private lateinit var rcvEquipos: RecyclerView
    private lateinit var adapter: EquiposAreaAdapter
    private lateinit var rcvFiltro: RecyclerView
    private lateinit var adapterFiltro: FiltroAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bundle = arguments
        val id_area = bundle?.getString("id_area") ?: ""
        val area_nombre = bundle?.getString("area_nombre") ?: ""
        val cantidad = bundle?.getInt("cantidad") ?: 0
        val reparados = bundle?.getInt("reparados") ?: 0

        val btnFiltro : ImageButton = view.findViewById(R.id.ibtnFiltrar)
        val crdFiltro : CardView = view.findViewById(R.id.crdFiltro)
        val txtTitulo : TextView = view.findViewById(R.id.txtTitulo)
        val txtPorcentaje : TextView = view.findViewById(R.id.txtPorcentaje)
        val txtEquiposReparados : TextView = view.findViewById(R.id.txtEquiposReparados)
        val pbPorcentaje : ProgressBar = view.findViewById(R.id.pbArea)

        txtTitulo.text = area_nombre

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
        readEquipo(id_area,txtEquiposReparados,txtPorcentaje,pbPorcentaje,cantidad,reparados)

        rcvFiltro = view.findViewById(R.id.rcvFiltro)
        rcvFiltro.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        adapterFiltro = FiltroAdapter(filtroList){id, view, estado, posicion ->
            val data = filtroList.find { it.id_trabajador == id }
            if (data != null){
                if (estado){
                    adapterFiltro.updateItemStatus(posicion, false)
                    txtEquiposReparados.text = reparados.toString() + "/" + cantidad.toString() + " equipos reparados"
                    txtPorcentaje.text = ((reparados.toFloat()/cantidad.toFloat())*100).toInt().toString() + "%"
                    pbPorcentaje.progress = ((reparados.toFloat()/cantidad.toFloat())*100).toInt()
                    readEquipo(id_area,txtEquiposReparados,txtPorcentaje,pbPorcentaje,cantidad,reparados)
                }else{
                    adapterFiltro.updateItemStatus(posicion, true)
                    readEquipoFiltro(data.id_trabajador,txtEquiposReparados,txtPorcentaje,pbPorcentaje)
                }

            }
        }
        rcvFiltro.adapter = adapterFiltro
        readFiltro(id_area)



        crdFiltro.visibility = View.GONE


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


    fun readEquipo(area:String, txtEquiposReparados: TextView, txtPorcentaje: TextView, pbPorcentaje: ProgressBar, cantidad: Int, reparados:Int) {
        val db = FirebaseFirestore.getInstance()
        val collection = db?.collection("equipos")

        val collectionTrabajo = db?.collection("trabajadores")
        collectionTrabajo?.get()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                trabajadorRead.clear()
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
                val filtroList = trabajadorRead.filter { it.id_area == area } as MutableList<Trabajador>

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
                            val id_area = document.getString("id_area") ?: ""
                            val prioridad = document.getString("prioridad") ?: ""
                            val obervacionTecnico = document.getString("obervacionTecnico") ?: ""
                            val id = document.id
                            val equipos = Equipo(id,idCliente, idTrabajador, nIngreso, equipo, nSerie, marca, modelo,
                                fechaIngreso, fechaFinalizacion, falla, observacion, estado,id_area,prioridad,obervacionTecnico)
                            val equipoTrabajador = filtroList.find { it.id_trabajador == idTrabajador }
                            if (equipoTrabajador != null) {
                                equipoList.add(equipos)
                            }
                        }
                        equipoList.sortBy { it.estado }
                        txtEquiposReparados.text = reparados.toString() + "/" + cantidad.toString() + " equipos reparados"
                        txtPorcentaje.text = ((reparados.toFloat()/cantidad.toFloat())*100).toInt().toString() + "%"
                        pbPorcentaje.progress = ((reparados.toFloat()/cantidad.toFloat())*100).toInt()
                        adapter.notifyDataSetChanged()
                        adapterFiltro.notifyDataSetChanged()
                    } else {
                        Log.w(ContentValues.TAG, "Error getting documents.", task.exception)
                    }
                }
            }
        }
    }

    fun readFiltro(area: String){
        val db = FirebaseFirestore.getInstance()
        val collectionTrabajo = db?.collection("trabajadores")
        collectionTrabajo?.whereEqualTo("id_area", area)?.get()?.addOnSuccessListener { result ->
            filtroList.clear()
            for (document in result) {
                val nombre = document.getString("nombre") ?: ""
                val cedula = document.getString("cedula") ?: ""
                val email = document.getString("email") ?: ""
                val contraseña = document.getBoolean("contraseña") ?: false
                val id_area = document.getString("id_area") ?: ""
                val id = document.id
                val trabajador = Filtro(id,email,nombre,cedula,contraseña,id_area,false)
                filtroList.add(trabajador)
            }
            adapterFiltro.notifyDataSetChanged()
        }?.addOnFailureListener { exception ->
            Log.w("Error: ", "Error getting documents: ", exception)
        }

    }

    fun readEquipoFiltro(id_trabajador:String, txtEquiposReparados: TextView, txtPorcentaje: TextView, pbPorcentaje: ProgressBar) {
        val db = FirebaseFirestore.getInstance()
        val collection = db?.collection("equipos")

        collection?.whereEqualTo("id_trabajador",id_trabajador)?.get()?.addOnSuccessListener { task ->
            equipoList.clear()
            var cantidadT = 0
            var reparadosT = 0
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
                val id = document.id
                val id_area = document.getString("id_area") ?: ""
                val prioridad = document.getString("prioridad") ?: ""
                val obervacionTecnico = document.getString("obervacionTecnico") ?: ""
                val equipos = Equipo(id,idCliente, idTrabajador, nIngreso, equipo, nSerie, marca, modelo,
                    fechaIngreso, fechaFinalizacion, falla, observacion, estado, id_area,prioridad,obervacionTecnico)
                equipoList.add(equipos)
                cantidadT++
                if (estado) {
                    reparadosT++
                }
            }
            equipoList.sortBy { it.estado }
            txtEquiposReparados.text = reparadosT.toString() + "/" + cantidadT.toString() + " equipos reparados"
            txtPorcentaje.text = ((reparadosT.toFloat()/cantidadT.toFloat())*100).toInt().toString() + "%"
            pbPorcentaje.progress = ((reparadosT.toFloat()/cantidadT.toFloat())*100).toInt()
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