package com.example.compustar.Modelo

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

class Equipo(
    val idEquipo: String,
    val idCliente: String, // Cambiado a String para simplificar, ajusta según tu necesidad
    val idTrabajador: String, // Cambiado a String para simplificar, ajusta según tu necesidad
    val nIngreso: String,
    val equipo: String,
    val nSerie: String,
    val marca: String,
    val modelo: String,
    val fechaIngreso: String,
    val fechaFinalizacion: String,
    val falla: String,
    val observacion: String, // Corregido el nombre del campo
    val estado: Boolean
) {
    private val TAG = "FirestoreManager"
    private var db: FirebaseFirestore? = null
    var globalEquipoId: String? = null

    fun addEquipo(
        idCliente: String,
        idTrabajador: String,
        idArea: String,
        nIngreso: String,
        equipo: String,
        nSerie: String,
        marca: String,
        modelo: String,
        fechaIngreso: String,
        fechaFinalizacion: String,
        falla: String,
        observacion: String,
        estado: Boolean,
        onSuccess: (String) -> Unit, // Callback para manejar el éxito
        onFailure: (Exception) -> Unit // Callback para manejar el fallo
    ) {
        val db = FirebaseFirestore.getInstance()
        val equipoData = hashMapOf(
            "id_cliente" to idCliente,
            "id_trabajador" to idTrabajador,
            "id_area" to idArea,
            "n_ingreso" to nIngreso,
            "equipo" to equipo,
            "n_serie" to nSerie,
            "marca" to marca,
            "modelo" to modelo,
            "fecha_ingreso" to fechaIngreso,
            "fecha_finalizacion" to fechaFinalizacion,
            "falla" to falla,
            "observacion" to observacion,
            "estado" to estado
        )

        //prioridad-string
        //obervacionTecnico-string
        //estado-string
        //id_area-String

        db.collection("equipos").add(equipoData)
            .addOnSuccessListener { documentReference ->
                onSuccess(documentReference.id) // Llama al callback con el ID del documento
            }
            .addOnFailureListener { e ->
                onFailure(e) // Llama al callback de fallo con la excepción
            }
    }

    fun updateEquipo(
        documentId: String?,
        idCliente: String?,
        idTrabajador: String?,
        nIngreso: String?,
        equipo: String?,
        nSerie: String?,
        marca: String?,
        modelo: String?,
        fechaIngreso: String?,
        fechaFinalizacion: String?,
        falla: String?,
        observacion: String?,
        estado: Boolean?
    ) {
        val db = FirebaseFirestore.getInstance()

        if (documentId != null) {
            val collection = db.collection("equipos")
            val document = collection.document(documentId)

            val equipoData = HashMap<String, Any>()

            idCliente?.let { equipoData["id_cliente"] = it }
            idTrabajador?.let { equipoData["id_trabajador"] = it }
            nIngreso?.let { equipoData["n_ingreso"] = it }
            equipo?.let { equipoData["equipo"] = it }
            nSerie?.let { equipoData["n_serie"] = it }
            marca?.let { equipoData["marca"] = it }
            modelo?.let { equipoData["modelo"] = it }
            fechaIngreso?.let { equipoData["fecha_ingreso"] = it }
            fechaFinalizacion?.let { equipoData["fecha_finalizacion"] = it }
            falla?.let { equipoData["falla"] = it }
            observacion?.let { equipoData["observacion"] = it }
            estado?.let { equipoData["estado"] = it }

            if (equipoData.isNotEmpty()) {
                document.update(equipoData)
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


    fun readEquipo() {
        val db = FirebaseFirestore.getInstance()
        val collection = db?.collection("equipos")

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