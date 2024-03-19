package com.example.compustar

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.compustar.Modelo.Area
import com.example.compustar.Modelo.AreaDetalle
import com.example.compustar.Modelo.Cliente
import com.example.compustar.Modelo.Equipo
import com.example.compustar.Modelo.Tarea
import com.example.compustar.Modelo.Trabajador
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.time.LocalDate

class MainActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var authStateListener: AuthStateListener
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //probando

        val btnEntrar: Button = findViewById(R.id.btnIniciarSesion)
        val txtemail : TextView = findViewById(R.id.txtUsuario)
        val txtpass : TextView = findViewById(R.id.txtClave)
        firebaseAuth = Firebase.auth

        btnEntrar.setOnClickListener {
            signIn(txtemail.text.toString(),txtpass.text.toString())

        }
    }

    private fun signIn(email: String, password: String)
    {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if(task.isSuccessful){
                    val user = firebaseAuth.currentUser
                    Toast.makeText(applicationContext,"Bienvenido", Toast.LENGTH_LONG).show()

                    val intent = Intent(applicationContext,Navegador::class.java)
                    startActivity(intent)
                }
            }
    }
}