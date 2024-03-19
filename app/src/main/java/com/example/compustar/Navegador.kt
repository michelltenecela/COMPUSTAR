package com.example.compustar

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.google.android.material.bottomnavigation.BottomNavigationView

class Navegador : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    private lateinit var bottomNavigationView: BottomNavigationView
    private var selectedItemId: Int = R.id.nav_Home

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navegador)

        bottomNavigationView = findViewById(R.id.bnvMenu)
        bottomNavigationView.setOnNavigationItemSelectedListener(this)

        bottomNavigationView.menu.findItem(R.id.nav_Home).setIcon(R.drawable.homen)

        // Carga el fragmento inicial
        val initialFragment = HomeFragment()
        supportFragmentManager.beginTransaction().replace(R.id.flHome, initialFragment).commit()

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.nav_Home -> {
                val previousMenuItem = bottomNavigationView.menu.findItem(selectedItemId)
                item.setIcon(R.drawable.homen)
                if(selectedItemId == R.id.nav_Agregar){
                    previousMenuItem.setIcon(R.drawable.agregarb)
                }else if(selectedItemId == R.id.nav_Equipo){
                    previousMenuItem.setIcon(R.drawable.equipob)
                }
                selectedItemId = item.itemId

                val fragment1 = HomeFragment()
                supportFragmentManager.beginTransaction().replace(R.id.flHome, fragment1).commit()
            }
            R.id.nav_Agregar -> {
                val previousMenuItem = bottomNavigationView.menu.findItem(selectedItemId)
                item.setIcon(R.drawable.agregarn)
                if(selectedItemId == R.id.nav_Home){
                    previousMenuItem.setIcon(R.drawable.homeb)
                }else if(selectedItemId == R.id.nav_Equipo){
                    previousMenuItem.setIcon(R.drawable.equipob)
                }
                selectedItemId = item.itemId

                val fragment2 = AgregarFragment()
                supportFragmentManager.beginTransaction().replace(R.id.flHome, fragment2).commit()
            }
            R.id.nav_Equipo -> {
                val previousMenuItem = bottomNavigationView.menu.findItem(selectedItemId)
                item.setIcon(R.drawable.equipon)
                if(selectedItemId == R.id.nav_Home){
                    previousMenuItem.setIcon(R.drawable.homeb)
                }else if(selectedItemId == R.id.nav_Agregar){
                    previousMenuItem.setIcon(R.drawable.agregarb)
                }
                selectedItemId = item.itemId

                val fragment3 = EquiposFragment()
                supportFragmentManager.beginTransaction().replace(R.id.flHome, fragment3).commit()
            }
        }
        return true
    }
}