package com.example.compustar.Modelo

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

class Cliente {
    private val TAG = "FirestoreManager"
    private var db: FirebaseFirestore? = null

    fun addCliente(nombre: String, cedula: String, telefono: String) {
        val db = FirebaseFirestore.getInstance()
        val cliente = hashMapOf(
            "nombre" to nombre,
            "cedula" to cedula,
            "telefono" to telefono
        )

        db?.collection("clientes")?.add(cliente)
            ?.addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            ?.addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }

    fun updateCliente(documentId: String?, nombre: String?, cedula: String?, telefono: String?) {
        val db = FirebaseFirestore.getInstance()

        if (documentId != null) {
            val collection = db.collection("clientes")
            val document = collection.document(documentId)

            val clienteData = HashMap<String, Any>()

            // Añadir campos no nulos al mapa
            nombre?.let { clienteData["nombre"] = it }
            cedula?.let { clienteData["cedula"] = it }
            telefono?.let { clienteData["telefono"] = it }

            if (clienteData.isNotEmpty()) {
                document.update(clienteData)
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

    fun readCliente() {
        val db = FirebaseFirestore.getInstance()
        val collection = db?.collection("clientes")

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
}