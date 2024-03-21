package com.example.compustar

import android.app.Dialog
import android.content.ContentValues
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.compustar.Adaptador.AreaDetalleAdapter
import com.example.compustar.Adaptador.EquiposAreaAdapter
import com.example.compustar.Adaptador.TrabajadorHomeAdapter
import com.example.compustar.Adaptador.TrabajadorPanelAdapter
import com.example.compustar.Modelo.Equipo
import com.example.compustar.Modelo.Trabajador
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.runBlocking
import org.json.JSONObject

class trabajadoresFragment : Fragment(R.layout.fragment_trabajadores) {

    private val trabajadorList = mutableListOf<Trabajador>()
    private lateinit var rcvTrabajador: RecyclerView
    private lateinit var adapter: TrabajadorPanelAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rcvTrabajador = view.findViewById(R.id.rcvTrabajadores)
        rcvTrabajador.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        adapter = TrabajadorPanelAdapter(trabajadorList){id, view ->
            val trabajador = trabajadorList.find { it.id_trabajador == id }
            if (trabajador != null){
                Toast.makeText(requireContext(),trabajador.nombre,Toast.LENGTH_LONG).show()
            }
        }
        rcvTrabajador.adapter = adapter

        readTrabajador()

        val btnAgregar : ImageView = view.findViewById(R.id.btnAgregar)
        btnAgregar.setOnClickListener {
            val fragment = trabajadorAgregarFragmento()
            val fragmentManager = requireActivity().supportFragmentManager
            val transaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.flHome, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

    }

    fun readTrabajador(){
        val db = FirebaseFirestore.getInstance()
        val collection = db?.collection("trabajadores")
        collection?.get()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                trabajadorList.clear()
                for (document in task.result) {
                    val nombre = document.getString("nombre") ?: ""
                    val cedula = document.getString("cedula") ?: ""
                    val email = document.getString("email") ?: ""
                    //val contraseña = document.getString("tipo") ?: ""
                    val id_area = document.getString("id_area") ?: ""
                    val id = document.id
                    val trabajador = Trabajador(id,email,nombre,cedula,"contraseña",id_area)
                    trabajadorList.add(trabajador)
                }
                adapter.notifyDataSetChanged()
            } else {
                Log.w(ContentValues.TAG, "Error getting documents.", task.exception)
            }
        }

    }
}

