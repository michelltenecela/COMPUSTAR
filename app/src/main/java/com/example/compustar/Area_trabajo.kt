package com.example.compustar

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.cardview.widget.CardView


class Area_trabajo : Fragment(R.layout.fragment_area_trabajo) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val crdFiltro : CardView = view.findViewById(R.id.crdFiltro)
        crdFiltro.visibility = View.GONE

        val btnFiltro : ImageButton = view.findViewById(R.id.ibtnFiltrar)
        btnFiltro.setOnClickListener {
            if (crdFiltro.visibility == View.GONE){
                crdFiltro.visibility = View.VISIBLE
                btnFiltro.setImageResource(R.drawable.filtrar_clic)
            }else {
                crdFiltro.visibility = View.GONE
                btnFiltro.setImageResource(R.drawable.filtrar)
            }
        }

        val crdEquipo : CardView = view.findViewById(R.id.crdEquipo)

        crdEquipo.setOnClickListener {
            val fragmentManager = requireActivity().supportFragmentManager

            // Crear transacción
            val transaction = fragmentManager.beginTransaction()

            // Reemplazar fragmento
            transaction.replace(R.id.flHome, Equipo_PerfilFragment())
            transaction.addToBackStack(null)

            // Confirmar transacción
            transaction.commit()
        }
    }
}