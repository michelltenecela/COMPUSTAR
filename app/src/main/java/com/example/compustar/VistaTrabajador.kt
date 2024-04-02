package com.example.compustar

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.compustar.Adaptador.EquiposAreaAdapter
import com.example.compustar.Modelo.Equipo
import com.example.compustar.Modelo.Trabajador
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.w3c.dom.Text

class VistaTrabajador : AppCompatActivity() {

    private val equipoList = mutableListOf<Equipo>()
    private lateinit var rcvEquipos: RecyclerView
    private lateinit var adapter: EquiposAreaAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vista_trabajador)

        val txtPorcentaje : TextView = findViewById(R.id.txtPorcentaje)
        val txtEquiposReparados : TextView = findViewById(R.id.txtEquiposReparados)
        val pbPorcentaje : ProgressBar = findViewById(R.id.pbArea)
        val btnNotificacion : FrameLayout = findViewById(R.id.btnNotificacion)
        val crdNotificacion : CardView = findViewById(R.id.crdNotificacionHome)
        val txtNotificacion : TextView = findViewById(R.id.txtNotificacion)

        val id_trabajador = getIntent().getStringExtra("id_trabajador") ?: ""
        val id_area = getIntent().getStringExtra("id_area") ?: ""



        btnNotificacion.setOnClickListener {
            val bundle = Intent(applicationContext,VistaTrabajadorEquipoDisponible::class.java)
            bundle.putExtra("id_trabajador", id_trabajador)
            bundle.putExtra("id_area", id_area)
            startActivity(bundle)
        }

        val imgMenu : ImageView = findViewById(R.id.imgMenu)

        imgMenu.setOnClickListener {
            val popupMenu = PopupMenu(this, it)
            popupMenu.inflate(R.menu.menu_salir)
            popupMenu.setOnMenuItemClickListener { menuItem: MenuItem ->
                when (menuItem.itemId) {
                    R.id.op_salir -> {
                        val mAuth = FirebaseAuth.getInstance()
                        mAuth.signOut()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        this.finish()
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }

        rcvEquipos = findViewById(R.id.rcvEquipos)
        rcvEquipos.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        adapter = EquiposAreaAdapter(equipoList){id, view, cliente, trabajador, precionado ->
            val data = equipoList.find { it.idEquipo == id }
            if (data != null){
                if (precionado){

                }else{
                    val bundle = Intent(applicationContext,VistaTrabajadorEquipo::class.java)
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
                    startActivity(bundle)
                }

            }
        }
        rcvEquipos.adapter = adapter

        readEquipo(id_trabajador,txtEquiposReparados,txtPorcentaje,pbPorcentaje)

        readEquipo(id_area,crdNotificacion,txtNotificacion)
    }

    override fun onResume() {
        super.onResume()

        val txtPorcentaje : TextView = findViewById(R.id.txtPorcentaje)
        val txtEquiposReparados : TextView = findViewById(R.id.txtEquiposReparados)
        val pbPorcentaje : ProgressBar = findViewById(R.id.pbArea)
        val btnNotificacion : FrameLayout = findViewById(R.id.btnNotificacion)
        val crdNotificacion : CardView = findViewById(R.id.crdNotificacionHome)
        val txtNotificacion : TextView = findViewById(R.id.txtNotificacion)

        val id_trabajador = getIntent().getStringExtra("id_trabajador") ?: ""
        val id_area = getIntent().getStringExtra("id_area") ?: ""
        readEquipo(id_trabajador,txtEquiposReparados,txtPorcentaje,pbPorcentaje)

        readEquipo(id_area,crdNotificacion,txtNotificacion)

    }

    fun readEquipo(id_trabajador:String, txtEquiposReparados: TextView, txtPorcentaje: TextView, pbPorcentaje: ProgressBar) {
        val db = FirebaseFirestore.getInstance()
        val collection = db?.collection("equipos")

        collection?.whereEqualTo("id_trabajador",id_trabajador)?.get()?.addOnSuccessListener { task ->
            equipoList.clear()
            var cantidad = 0
            var reparados = 0
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
                equipoList.add(equipos)
                cantidad++
                if (estado) {
                    reparados++
                }
            }
            equipoList.sortBy { it.estado }
            txtEquiposReparados.text = reparados.toString() + "/" + cantidad.toString() + " equipos reparados"
            txtPorcentaje.text = ((reparados.toFloat()/cantidad.toFloat())*100).toInt().toString() + "%"
            pbPorcentaje.progress = ((reparados.toFloat()/cantidad.toFloat())*100).toInt()
            adapter.notifyDataSetChanged()
        }
    }

    fun readEquipo(idArea: String,crdNotificacion: CardView, txtNotificacion: TextView) {
        val db = FirebaseFirestore.getInstance()
        val collection = db?.collection("equipos")
        var contador = 0

        collection?.whereEqualTo("id_trabajador","")?.get()?.addOnSuccessListener { task ->
            for (document in task) {
                val id_area = document.getString("id_area") ?: ""
                if (id_area == idArea){
                    contador++
                }
            }
            if (contador != 0){
                crdNotificacion.visibility = View.VISIBLE
                txtNotificacion.text = contador.toString()
            }else{
                crdNotificacion.visibility = View.GONE
            }
        }
    }
}