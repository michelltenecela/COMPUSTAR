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
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.compustar.Adaptador.TareaAdapter
import com.example.compustar.Modelo.Tarea
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class VistaTrabajadorEquipo : AppCompatActivity() {

    private val tareaList = mutableListOf<Tarea>()
    private lateinit var rcvTareas: RecyclerView
    private lateinit var adapter: TareaAdapter

    var estado = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vista_trabajador_equipo)

        val id_equipo = getIntent().getStringExtra("id_equipo") ?: ""
        val cliente = getIntent().getStringExtra("cliente") ?: ""
        val trabajador = getIntent().getStringExtra("trabajador") ?: ""
        val falla = getIntent().getStringExtra("falla") ?: ""
        val fecha = getIntent().getStringExtra("fecha") ?: ""
        val observacion = getIntent().getStringExtra("observacion") ?: ""
        val ingreso = getIntent().getStringExtra("n_ingreso") ?: ""
        estado = getIntent().getBooleanExtra("estado",false)

        val txtCliente : TextView = findViewById(R.id.txtTitulo)
        val txtFalla : TextView = findViewById(R.id.txtFalla)
        val txtObservacion : TextView = findViewById(R.id.txtObservacion)
        val txtTrabajador : TextView = findViewById(R.id.txtTrabajador)
        val txtFecha : TextView = findViewById(R.id.txtFecha)
        val btnCompletar : Button = findViewById(R.id.btnCompletar)
        val txtIngreso : TextView = findViewById(R.id.txtIngreso)

        if(estado){
            btnCompletar.setText("Reanudar")
            btnCompletar.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#CB2027"))
        }else{
            btnCompletar.setText("Completar")
            btnCompletar.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#27D1B3"))
        }

        btnCompletar.setOnClickListener {
            showDialogCompletarEquipo(id_equipo, btnCompletar)
        }

        txtCliente.text = cliente
        txtFalla.text = falla
        txtObservacion.text = observacion
        txtTrabajador.text = trabajador
        txtFecha.text = fecha
        txtIngreso.text = ingreso

        rcvTareas = findViewById(R.id.rcvTareas)
        rcvTareas.layoutManager = FlexboxLayoutManager(this, FlexDirection.ROW, FlexWrap.WRAP)

        adapter = TareaAdapter(tareaList) { id, view ->
            val data = tareaList.find { it.idTarea == id }
            if (data != null){
                showDialogCompletar(data.idTarea,data.falla,data.estado)
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
            adapter.notifyDataSetChanged()

        }?.addOnFailureListener { exception ->
            Log.w("Error: ", "Error getting documents: ", exception)
        }
    }

    private fun showDialogCompletar(idTarea: String, tarea: String, estado: Boolean) {
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
        val imgFoto : ImageView = dialog.findViewById(R.id.imgFoto)
        val imgFotoTitulo : ImageView = dialog.findViewById(R.id.imgFotoTitulo)

        txtTarea.setText(tarea)


        if (estado){
            imgFotoTitulo.setImageResource(R.drawable.advertencia)
            txtTitulo.setTextColor(Color.parseColor("#CB2027"))
            btnCompletar.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#CB2027"))
            btnCancelar.setTextColor(Color.parseColor("#CB2027"))
            btnCancelar.strokeColor = ColorStateList.valueOf(Color.parseColor("#CB2027"))
            imgFoto.setImageResource(R.drawable.tarea_faltante)
            txtTitulo.setText("¿Aun no completas la tarea?")
            txtDescripcion.setText("Cuando no completas una tarea se mostrara la tarea con una x y deminuye la barra de progreso")
        }else{
            imgFotoTitulo.setImageResource(R.drawable.advertencia_equipo)
            txtTitulo.setTextColor(Color.parseColor("#27D1B3"))
            btnCompletar.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#27D1B3"))
            btnCancelar.setTextColor(Color.parseColor("#27D1B3"))
            btnCancelar.strokeColor = ColorStateList.valueOf(Color.parseColor("#27D1B3"))
            imgFoto.setImageResource(R.drawable.tarea_terminada)
            txtTitulo.setText("¿Has completado la tarea?")
            txtDescripcion.setText("Cuando completas una tarea se mostrara la tarea con un visto y aumentara la barra de progreso")
            }
        btnCancelar.setOnClickListener {
            dialog.dismiss()
        }

        btnCompletar.setOnClickListener {
            val db = FirebaseFirestore.getInstance()
            val collection = db?.collection("tareas")
            val document = collection?.document(idTarea)

            val tareaData = hashMapOf<String, Any>(
                "estado" to !estado
            )

            document?.update(tareaData)?.addOnSuccessListener {
                    adapter.updateTarea(idTarea,!estado)
                    dialog.dismiss()
                    Log.d("TAG", "Estado de la tarea actualizado exitosamente")
                }
                ?.addOnFailureListener { e ->
                    Log.w("TAG", "Error al actualizar el estado de la tarea", e)
                }

        }
        dialog.show()
    }

    private fun showDialogCompletarEquipo(idEquipo: String, btnComple : Button) {
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

        if (estado){
            imgFotoTitulo.setImageResource(R.drawable.advertencia)
            txtTitulo.setTextColor(Color.parseColor("#CB2027"))
            btnCompletar.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#CB2027"))
            btnCancelar.setTextColor(Color.parseColor("#CB2027"))
            btnCancelar.strokeColor = ColorStateList.valueOf(Color.parseColor("#CB2027"))
            txtTitulo.setText("¿Aun no completas el mantenimiento?")
            txtDescripcion.setText("Cuando no completas el mantenimiento de un equipo se mostrara el equipo como faltante y disminuye la barra de progreso")
        }else {
            imgFotoTitulo.setImageResource(R.drawable.advertencia_equipo)
            txtTitulo.setTextColor(Color.parseColor("#27D1B3"))
            btnCompletar.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#27D1B3"))
            btnCancelar.setTextColor(Color.parseColor("#27D1B3"))
            btnCancelar.strokeColor = ColorStateList.valueOf(Color.parseColor("#27D1B3"))
            txtTitulo.setText("¿Has completado el mantenimiento?")
            txtDescripcion.setText("Cuando completas el mantenimiento de un equipo se mostrara el equipo terminado con un visto y aumentara la barra de progreso")
        }



            btnCancelar.setOnClickListener {
            dialog.dismiss()
        }

        btnCompletar.setOnClickListener {
            val db = FirebaseFirestore.getInstance()
            val collection = db?.collection("equipos")
            val document = collection?.document(idEquipo)

            val equipoData = hashMapOf<String, Any>(
                "estado" to !estado
            )

            document?.update(equipoData)?.addOnSuccessListener {
                adapter.updateTarea(idEquipo,!estado)
                if (!estado){
                    btnComple.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#CB2027"))
                    btnComple.setText("Reanudar")
                }else{
                    btnComple.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#27D1B3"))
                    btnComple.setText("Completar")
                }
                estado = !estado
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