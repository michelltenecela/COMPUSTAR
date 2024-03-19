package com.example.compustar.Modelo

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

class Tarea {
    private val TAG = "FirestoreManager"
    private var db: FirebaseFirestore? = null

    fun addTarea(
        idEquipo: String, // Cambiado a String para simplificar, ajusta según tu necesidad
        falla: String,
        descripcion: String,
        fechaFinalizacion: String,
        estado: Boolean
    ) {
        val db = FirebaseFirestore.getInstance()
        val tareaData = hashMapOf(
            "id_equipo" to idEquipo,
            "falla" to falla,
            "descripcion" to descripcion,
            "fecha_finalizacion" to fechaFinalizacion,
            "estado" to estado
        )

        db?.collection("tareas")?.add(tareaData)
            ?.addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            ?.addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }

    fun updateTarea(
        documentId: String?,
        idEquipo: String?,
        falla: String?,
        descripcion: String?,
        fechaFinalizacion: String?,
        estado: Boolean?
    ) {
        val db = FirebaseFirestore.getInstance()

        if (documentId != null) {
            val collection = db.collection("tareas")
            val document = collection.document(documentId)

            val tareaData = HashMap<String, Any>()

            // Añadir campos no nulos al mapa
            idEquipo?.let { tareaData["id_equipo"] = it }
            falla?.let { tareaData["falla"] = it }
            descripcion?.let { tareaData["descripcion"] = it }
            fechaFinalizacion?.let { tareaData["fecha_finalizacion"] = it }
            estado?.let { tareaData["estado"] = it }

            if (tareaData.isNotEmpty()) {
                document.update(tareaData)
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

    fun readTarea() {
        val db = FirebaseFirestore.getInstance()
        val collection = db?.collection("tareas")

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