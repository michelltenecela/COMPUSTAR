package com.example.compustar

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.compustar.Adaptador.EquiposAreaAdapter
import com.example.compustar.Adaptador.PanelControlAdapter
import com.example.compustar.Modelo.Equipo
import com.example.compustar.Modelo.PanelControl


class EquiposFragment : Fragment(R.layout.fragment_equipos) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val panelList = mutableListOf<PanelControl>()
        lateinit var rcvPanel: RecyclerView
        lateinit var adapter: PanelControlAdapter

        panelList.add(PanelControl(1,"Trabajadores","trabajador"))
        panelList.add(PanelControl(2,"Areas de trabajo","area"))
        panelList.add(PanelControl(3,"Historial de mantenimiento","historial"))

        rcvPanel = view.findViewById(R.id.rcvPanel)
        rcvPanel.layoutManager = GridLayoutManager(requireContext(), 2)
        adapter = PanelControlAdapter(panelList){id, view ->
            val data = panelList.find { it.id_panel == id }
            if (data != null){
                if(data.id_panel == 1){
                    val fragment = trabajadoresFragment()
                    val fragmentManager = requireActivity().supportFragmentManager
                    val transaction = fragmentManager.beginTransaction()
                    transaction.replace(R.id.flHome, fragment)
                    transaction.addToBackStack(null)
                    transaction.commit()
                }

            }
        }
        rcvPanel.adapter = adapter
    }
}