package com.example.compustar

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var authStateListener: AuthStateListener
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnEntrar: Button = findViewById(R.id.btnIniciarSesion)
        val txtemail : TextView = findViewById(R.id.txtUsuario)
        val txtpass : TextView = findViewById(R.id.txtClave)
        firebaseAuth = Firebase.auth

        firebaseAuth.addAuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {

                FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                        return@OnCompleteListener
                    }

                    val token = task.result
                    Token(token)
                })

                val intent = Intent(applicationContext,Navegador::class.java)
                startActivity(intent)
                finish()
            }
        }

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

                    FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                            return@OnCompleteListener
                        }

                        val token = task.result
                        Token(token)
                    })

                    val intent = Intent(applicationContext,Navegador::class.java)
                    startActivity(intent)
                    finish()
                }
            }
    }
    private fun Token(token: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val userDocRef = FirebaseFirestore.getInstance().collection("trabajadores").document(userId)

        userDocRef.update("fcmToken", token)
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot successfully updated with token: $token")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error updating document", e)
            }
    }


}