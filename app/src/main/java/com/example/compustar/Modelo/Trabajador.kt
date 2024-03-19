package com.example.compustar.Modelo

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

class Trabajador {
    private val TAG = "FirestoreManager"
    private var db: FirebaseFirestore? = null

    fun Trabajador() {
        db = FirebaseFirestore.getInstance()
    }

    fun addTrabajador(email: String, nombre: String, cedula: String, contraseña: String) {
        val db = FirebaseFirestore.getInstance()
        val trabajador = hashMapOf(
            "email" to email,
            "nombre" to nombre,
            "cedula" to cedula,
            "contraseña" to contraseña
        )

        db?.collection("trabajadores")?.add(trabajador)
            ?.addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            ?.addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }

    fun readUsers() {
        val db = FirebaseFirestore.getInstance()
        val collection = db?.collection("trabajadores")

        collection?.get()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result) {
                        Log.d(TAG, "${document.id} => ${document.data}")
                    }
                } else {
                    Log.w(TAG, "Error getting documents.", task.exception)
                }
            }
    }

    fun updateTrabajador(
        documentId: String?,
        email: String?,
        nombre: String?,
        cedula: String?,
        contraseña: String?
    ) {
        val db = FirebaseFirestore.getInstance()

        if (documentId != null) {
            val collection = db.collection("trabajadores")
            val document = collection.document(documentId)

            val trabajadorData = HashMap<String, Any>()

            // Añadir campos no nulos al mapa
            email?.let { trabajadorData["email"] = it }
            nombre?.let { trabajadorData["nombre"] = it }
            cedula?.let { trabajadorData["cedula"] = it }
            contraseña?.let { trabajadorData["contraseña"] = it }

            if (trabajadorData.isNotEmpty()) {
                document.update(trabajadorData)
                    .addOnSuccessListener {
                        Log.d(TAG, "DocumentSnapshot successfully updated!")
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Error updating document", e)
                    }
            } else {
                Log.w(TAG, "Error: No fields to update")
            }
        } else {
            Log.w(TAG, "Error: documentId is null")
        }
    }

    fun deleteUser(documentId: String?) {
        val db = FirebaseFirestore.getInstance()
        val collection = db?.collection("trabajadores")
        val document = documentId?.let { collection?.document(it) }

        document?.delete()
            ?.addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot successfully deleted!")
            }
            ?.addOnFailureListener { e ->
                Log.w(TAG, "Error deleting document", e)
            }
    }

}