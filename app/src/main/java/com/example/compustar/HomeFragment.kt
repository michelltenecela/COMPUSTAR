package com.example.compustar

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.LinearLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView

class HomeFragment : Fragment(R.layout.fragment_home) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnArea1 : LinearLayout = view.findViewById(R.id.llArea1)

        btnArea1.setOnClickListener {
            val fragmentManager = requireActivity().supportFragmentManager

            // Crear transacción
            val transaction = fragmentManager.beginTransaction()

            // Reemplazar fragmento
            transaction.replace(R.id.flHome, Area_trabajo())
            transaction.addToBackStack(null)

            // Confirmar transacción
            transaction.commit()
        }
    }
}