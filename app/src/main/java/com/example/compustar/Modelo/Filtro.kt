package com.example.compustar.Modelo

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class Filtro(val id_trabajador: String,
             val email: String,
             val nombre: String,
             val cedula: String,
             val tipo: Boolean,
             val id_area: String,
             var estado: Boolean = false)