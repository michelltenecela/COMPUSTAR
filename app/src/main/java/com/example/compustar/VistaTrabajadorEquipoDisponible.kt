package com.example.compustar

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.compustar.Adaptador.EquiposAreaAdapter
import com.example.compustar.Modelo.Equipo
import com.google.firebase.firestore.FirebaseFirestore

@Suppress("DEPRECATION")
class VistaTrabajadorEquipoDisponible : AppCompatActivity() {

    private val equipoList = mutableListOf<Equipo>()
    private lateinit var rcvEquipos: RecyclerView
    private lateinit var adapter: EquiposAreaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vista_trabajador_equipo_disponible)

        val id_trabajador = getIntent().getStringExtra("id_trabajador") ?: ""
        val id_area = getIntent().getStringExtra("id_area") ?: ""

        rcvEquipos = findViewById(R.id.rcvEquipos)
        rcvEquipos.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
        adapter = EquiposAreaAdapter(equipoList){id, view, cliente, trabajador, precionado ->
            val data = equipoList.find { it.idEquipo == id }
            if (data != null){
                if (precionado){

                }else{
                    val bundle = Intent(applicationContext,VistaTrabajadorEquipoAceptar::class.java)
                    bundle.putExtra("id_equipo", data.idEquipo)
                    bundle.putExtra("cliente", cliente)
                    bundle.putExtra("trabajador", trabajador)
                    bundle.putExtra("falla", data.falla)
                    bundle.putExtra("fecha", data.fechaIngreso)
                    bundle.putExtra("observacion", data.observacion)
                    bundle.putExtra("observacionTecnico", data.obervacionTecnico)
                    bundle.putExtra("fechaF", data.fechaFinalizacion)
                    bundle.putExtra("estado", data.estado)
                    bundle.putExtra("n_ingreso", data.nIngreso)
                    bundle.putExtra("idTrabajador", id_trabajador)
                    startActivity(bundle)
                }

            }
        }
        rcvEquipos.adapter = adapter
        readEquipo(id_area)
    }

    override fun onResume() {
        super.onResume()
        val id_area = getIntent().getStringExtra("id_area") ?: ""

        readEquipo(id_area)
    }

    fun readEquipo(idArea: String) {
        val db = FirebaseFirestore.getInstance()
        val collection = db?.collection("equipos")

        collection?.whereEqualTo("id_trabajador","")?.get()?.addOnSuccessListener { task ->
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
                val id_area = document.getString("id_area") ?: ""
                val prioridad = document.getString("prioridad") ?: ""
                val obervacionTecnico = document.getString("obervacionTecnico") ?: ""
                val id = document.id
                val equipos = Equipo(id,idCliente, idTrabajador, nIngreso, equipo, nSerie, marca, modelo,
                    fechaIngreso, fechaFinalizacion, falla, observacion, estado, id_area,prioridad,obervacionTecnico)
                if (id_area == idArea) {
                    equipoList.add(equipos)
                }
            }
            adapter.notifyDataSetChanged()
        }
    }


}