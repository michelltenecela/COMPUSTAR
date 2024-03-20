package com.example.compustar.Modelo

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

class Area(
    val id_area: String,
    val nombre: String,
    val estado: Boolean)
{
    private val TAG = "FirestoreManager"
    private var db: FirebaseFirestore? = null


    fun readArea() {
        val db = FirebaseFirestore.getInstance()
        val collection = db?.collection("areas")

        collection?.get()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result) {
                        //Log.d(TAG, "${document.id} => ${document.data}")
                        val nombre = document.getString("nombre") ?: ""
                        val estado = document.getBoolean("estado") ?: false
                        val id = document.id
                        val area = Area(id,nombre, estado)
                        //areaList.add(area)
                    }
                } else {
                    Log.w(TAG, "Error getting documents.", task.exception)
                }
            }
    }

    fun addArea(nombre: String, estado: Boolean) {
        val db = FirebaseFirestore.getInstance()
        val area = hashMapOf(
            "nombre" to nombre,
            "estado" to estado
        )

        db?.collection("areas")?.add(area)
            ?.addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            ?.addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }

    fun updateArea(documentId: String?, nombre: String?, estado: Boolean?) {
        val db = FirebaseFirestore.getInstance()

        if (documentId != null) {
            val collection = db.collection("areas")
            val document = collection.document(documentId)

            val areaData = HashMap<String, Any>()

            // Añadir campos no nulos al mapa
            nombre?.let { areaData["nombre"] = it }
            estado?.let { areaData["estado"] = it }

            if (areaData.isNotEmpty()) {
                document.update(areaData)
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



}