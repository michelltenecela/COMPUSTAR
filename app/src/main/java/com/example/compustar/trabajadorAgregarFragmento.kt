package com.example.compustar

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.compustar.Modelo.Area
import com.example.compustar.Modelo.Trabajador
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class trabajadorAgregarFragmento : Fragment(R.layout.fragment_trabajador_agregar_fragmento) {

    private lateinit var areas: List<Area>
    private lateinit var seleccion: String


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val btnAgregarBD: Button = view.findViewById(R.id.btnAgregarUser)

        val area = Area("","",true)

        area.readArea(
            onSuccess = { areasObtenidas ->
                areas = areasObtenidas // Guarda las áreas obtenidas aquí
                val nombresDeAreas = areas.map { it.nombre }

                val adaptador = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    nombresDeAreas
                )

                val tvArea: AutoCompleteTextView = view.findViewById(R.id.tvarea)
                tvArea.setAdapter(adaptador)

                tvArea.setOnItemClickListener { adapterView, view, position, id ->
                    val areaSeleccionada = areas[position]
                    seleccion = areaSeleccionada.id_area
                }
            },
            onFailure = { exception ->
                Log.w("Firestore", "Error al leer áreas: ", exception)
            }
        )

        btnAgregarBD.setOnClickListener {
            val auth: FirebaseAuth = FirebaseAuth.getInstance()

            val email: TextView = view.findViewById(R.id.tvEmail)
            val password: TextView = view.findViewById(R.id.tvpass)
            val nombre: TextView = view.findViewById(R.id.tvNombre)
            val cedula: TextView = view.findViewById(R.id.tvCedula)
            val adminSwitch: SwitchMaterial = view.findViewById(R.id.smAdmin)

            auth.createUserWithEmailAndPassword(email.text.toString(), password.text.toString())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val firebaseUser: FirebaseUser? = auth.currentUser

                        val userId = firebaseUser?.uid ?: throw IllegalStateException("User ID no encontrado")

                        val trabajador = Trabajador(
                            userId,
                            email.text.toString(),
                            nombre.text.toString(),
                            cedula.text.toString(),
                            adminSwitch.isChecked,
                            seleccion
                        )

                        trabajador.addTrabajador()

                    } else {
                        Toast.makeText(requireContext(), "El registro ha fallado.",
                            Toast.LENGTH_SHORT).show()
                    }
                }

        }
    }
}