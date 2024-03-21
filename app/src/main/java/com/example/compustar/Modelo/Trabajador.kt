package com.example.compustar.Modelo

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class Trabajador( val id_trabajador: String,
                  val email: String,
                  val nombre: String,
                  val cedula: String,
                  val tipo: Boolean,
                  val id_area: String) {
    private val TAG = "FirestoreManager"
    private var db: FirebaseFirestore? = null

    fun readUsers(
        onSuccess: (List<Trabajador>) -> Unit, // Callback para manejar el éxito
        onFailure: (Exception) -> Unit // Callback para manejar el fallo
    ) {
        val db = FirebaseFirestore.getInstance()
        db.collection("trabajadores").get()
            .addOnSuccessListener { result ->
                val users = mutableListOf<Trabajador>()
                for (document in result) {
                    val user = Trabajador(
                        id_trabajador = document.id,
                        email = document.getString("email") ?: "",
                        nombre = document.getString("nombre") ?: "",
                        cedula = document.getString("cedula") ?: "",
                        tipo = document.getBoolean("tipo") ?: false,
                        id_area = document.getString("id_area") ?: ""
                    )
                    users.add(user)
                }
                onSuccess(users) // Llama al callback con la lista de usuarios
            }
            .addOnFailureListener { e ->
                onFailure(e) // Llama al callback de fallo con la excepción
            }
    }

    fun addTrabajador(){

        val db = FirebaseFirestore.getInstance()
        val trabajador = hashMapOf(
            "email" to email,
            "nombre" to nombre,
            "cedula" to cedula,
            "tipo" to tipo,
            "id_area" to id_area
        )

        // Usa el método document con el ID que deseas para obtener un DocumentReference
        val documentReference = db.collection("trabajadores").document(id_trabajador)

        // Luego establece los datos en ese DocumentReference
        documentReference.set(trabajador)
            .addOnSuccessListener {
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error al añadir documento", e)
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