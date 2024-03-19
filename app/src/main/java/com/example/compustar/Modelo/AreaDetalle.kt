package com.example.compustar.Modelo

import com.google.firebase.firestore.FirebaseFirestore
import android.util.Log

class AreaDetalle {

    private val TAG = "FirestoreManager"
    private var db: FirebaseFirestore? = null

    fun addAreaDetalle(
        idAreaTrabajo: String, // Cambiado a String para simplificar, ajusta según tu necesidad
        idTrabajador: String // Cambiado a String para simplificar, ajusta según tu necesidad
    ) {
        val db = FirebaseFirestore.getInstance()
        val areaDetalleData = hashMapOf(
            "id_areatrabajo" to idAreaTrabajo,
            "id_trabajador" to idTrabajador
        )

        db?.collection("areaDetalles")?.add(areaDetalleData)
            ?.addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            ?.addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }

    fun updateAreaDetalle(
        documentId: String?,
        idAreaTrabajo: String?,
        idTrabajador: String?
    ) {
        val db = FirebaseFirestore.getInstance()

        if (documentId != null) {
            val collection = db.collection("areaDetalles")
            val document = collection.document(documentId)

            val areaDetalleData = HashMap<String, Any>()

            // Añadir campos no nulos al mapa
            idAreaTrabajo?.let { areaDetalleData["id_areatrabajo"] = it }
            idTrabajador?.let { areaDetalleData["id_trabajador"] = it }

            if (areaDetalleData.isNotEmpty()) {
                document.update(areaDetalleData)
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

    fun readAreaDetalle() {
        val db = FirebaseFirestore.getInstance()
        val collection = db?.collection("areaDetalles")

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