package com.example.compustar

import android.app.Dialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.compustar.Adaptador.TareaAdapter
import com.example.compustar.Modelo.Tarea
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar

@Suppress("DEPRECATION")
class VistaTrabajadorEquipoAceptar : AppCompatActivity() {

    private val tareaList = mutableListOf<Tarea>()
    private lateinit var rcvTareas: RecyclerView
    private lateinit var adapter: TareaAdapter

    var mRevisado = ""
    var mReparado = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vista_trabajador_equipo_aceptar)
        val id_equipo = getIntent().getStringExtra("id_equipo") ?: ""
        val cliente = getIntent().getStringExtra("cliente") ?: ""
        val trabajador = getIntent().getStringExtra("trabajador") ?: ""
        val falla = getIntent().getStringExtra("falla") ?: ""
        val fecha = getIntent().getStringExtra("fecha") ?: ""
        val observacion = getIntent().getStringExtra("observacion") ?: ""
        val ingreso = getIntent().getStringExtra("n_ingreso") ?: ""
        val fechaFinal = getIntent().getStringExtra("fechaF") ?: ""
        val id_trabajador = getIntent().getStringExtra("idTrabajador") ?: ""

        val txtCliente : TextView = findViewById(R.id.txtTitulo)
        val txtFalla : TextView = findViewById(R.id.txtFalla)
        val txtObservacion : TextView = findViewById(R.id.txtObservacion)
        val txtTrabajador : TextView = findViewById(R.id.txtTrabajador)
        val txtFecha : TextView = findViewById(R.id.txtFecha)
        val txtFechaF : TextView = findViewById(R.id.txtFechaF)
        val btnCompletar : Button = findViewById(R.id.btnCompletar)
        val txtIngreso : TextView = findViewById(R.id.txtIngreso)


        btnCompletar.setOnClickListener {
            showDialogCompletarEquipo(id_equipo, id_trabajador)
        }

        txtCliente.text = cliente
        txtFalla.text = falla
        txtObservacion.text = observacion
        txtTrabajador.text = trabajador
        txtFecha.text = fecha
        txtIngreso.text = ingreso
        txtFechaF.text = fechaFinal

        rcvTareas = findViewById(R.id.rcvTareas)
        rcvTareas.layoutManager = FlexboxLayoutManager(this, FlexDirection.ROW, FlexWrap.WRAP)

        adapter = TareaAdapter(tareaList) { id, view ->
            val data = tareaList.find { it.idTarea == id }
            if (data != null){

            }
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
            tareaList.sortBy { it.falla }
            adapter.notifyDataSetChanged()

        }?.addOnFailureListener { exception ->
            Log.w("Error: ", "Error getting documents: ", exception)
        }
    }
    private fun showDialogCompletarEquipo(idEquipo: String, idTrabajador: String) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dialog_tarea_realizada)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        val txtTitulo : TextView = dialog.findViewById(R.id.txtvEncabezado)
        val txtDescripcion : TextView = dialog.findViewById(R.id.txtMensajeDescipcion)
        val txtTarea : TextView = dialog.findViewById(R.id.txtAvisoBloquear)
        val btnCompletar : Button = dialog.findViewById(R.id.btnFinalizarSi)
        val btnCancelar : MaterialButton = dialog.findViewById(R.id.btnFinalizarNo)
        val imgFotoTitulo : ImageView = dialog.findViewById(R.id.imgFotoTitulo)
        val imgFoto : ImageView = dialog.findViewById(R.id.imgFoto)

        imgFoto.visibility = View.GONE
        txtTarea.visibility = View.GONE
        imgFotoTitulo.setImageResource(R.drawable.advertencia_equipo)

        txtTitulo.setText("Dar mantenimiento")
        txtDescripcion.setText("Cuando aceptas dar un mantenimiento ese equipo queda a tu responsabilidad")

        btnCancelar.setOnClickListener {
            dialog.dismiss()
        }

        btnCompletar.setOnClickListener {
            val db = FirebaseFirestore.getInstance()
            val collection = db?.collection("equipos")
            val document = collection?.document(idEquipo)

            val equipoData = hashMapOf<String, Any>(
                "id_trabajador" to idTrabajador
            )

            document?.update(equipoData)?.addOnSuccessListener {
                onBackPressed()
                dialog.dismiss()
                Log.d("TAG", "Estado de la tarea actualizado exitosamente")
            }
                ?.addOnFailureListener { e ->
                    Log.w("TAG", "Error al actualizar el estado de la tarea", e)
                }

        }
        dialog.show()
    }

}