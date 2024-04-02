package com.example.compustar
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ImageSpan
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.compustar.Modelo.Area
import com.example.compustar.Modelo.Cliente
import com.example.compustar.Modelo.Equipo
import com.example.compustar.Modelo.Tarea
import com.example.compustar.Modelo.Trabajador
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.chip.ChipGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper
import kotlinx.coroutines.launch
import org.bouncycastle.util.Arrays.append
import java.io.IOException

class AgregarFragment : Fragment(R.layout.fragment_agregar) {

    private val PICK_PDF_FILE = 1
    private lateinit var textViewIngreso: TextView
    private lateinit var textViewNombre: TextView
    private lateinit var textViewCedula: TextView
    private lateinit var textViewTelefono: TextView
    private lateinit var textViewEquipo: TextView
    private lateinit var textViewSerie: TextView
    private lateinit var textViewMarca: TextView
    private lateinit var textViewFecha: TextView
    private lateinit var textViewModelo: TextView
    private lateinit var textViewFalla: TextView
    private lateinit var textViewObservacion: TextView
    private lateinit var chipGroup: ChipGroup
    private lateinit var Lista: List<String>
    private lateinit var selectedItem: String
    private lateinit var autoCompleteTextView: AutoCompleteTextView


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnAgregarBD:Button = view.findViewById(R.id.btnTrabajador)
        val btnAgregar2:Button = view.findViewById(R.id.btnArea)
        val btnAgregar: ImageButton = view.findViewById(R.id.ibtnPDF)

        autoCompleteTextView = view.findViewById<AutoCompleteTextView>(R.id.tvprioridad)

        val sugerencias = arrayOf("Alta", "Media", "Baja")

        // Crea un adaptador ArrayAdapter y configúralo en el AutoCompleteTextView
        val adapter = ArrayAdapter(requireActivity(), android.R.layout.simple_dropdown_item_1line, sugerencias)
        autoCompleteTextView.setAdapter(adapter)

        selectedItem = "Bajo"

        textViewIngreso = view.findViewById(R.id.tvIngreso)
        textViewNombre = view.findViewById(R.id.tvNombre)
        textViewCedula = view.findViewById(R.id.tvCedula)
        textViewTelefono = view.findViewById(R.id.tvTelefono)
        textViewEquipo = view.findViewById(R.id.tvEquipo)
        textViewSerie = view.findViewById(R.id.tvSerie)
        textViewMarca = view.findViewById(R.id.tvMarca)
        textViewFecha = view.findViewById(R.id.tvFecha)
        textViewModelo = view.findViewById(R.id.tvModelo)
        textViewFalla = view.findViewById(R.id.tvFalla)
        textViewObservacion = view.findViewById(R.id.tvObservacion)

        PDFBoxResourceLoader.init(requireActivity().applicationContext)

        autoCompleteTextView.setOnItemClickListener { parent, view, position, id ->
            selectedItem = parent.getItemAtPosition(position).toString()
        }

        btnAgregar.setOnClickListener {
            selectPdfFile()
        }

        btnAgregarBD.setOnClickListener {

            val trabajador = Trabajador("","","","",false,"")
            trabajador.readUsers(
                onSuccess = { users ->
                    // Extrayendo nombres de usuario para mostrar en el diálogo
                    val userNames = users.map { it.nombre }.toTypedArray()

                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Selecciona un usuario")
                        .setItems(userNames) { dialog, which ->
                            val selectedUserId = users[which].id_trabajador
                            Enviar(selectedUserId,"")
                        }
                        .show()
                },
                onFailure = { exception ->
                    Toast.makeText(requireContext(), "Error al obtener usuarios", Toast.LENGTH_SHORT).show()
                }
            )
        }

        btnAgregar2.setOnClickListener {
            val trabajador = Area("","",false)
            trabajador.readArea(
                onSuccess = { users ->
                    // Extrayendo nombres de usuario para mostrar en el diálogo
                    val userNames = users.map { it.nombre }.toTypedArray()

                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Selecciona una Area")
                        .setItems(userNames) { dialog, which ->
                            val selectedUserId = users[which].id_area
                            Enviar("",selectedUserId)
                        }
                        .show()
                },
                onFailure = { exception ->
                    Toast.makeText(requireContext(), "Error al obtener usuarios", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    private fun LlenarChips(texto: String): List<String>{
        val words = texto.split(" ")
        val chipTexts = words.filter { it.length > 1 }

        chipGroup.removeAllViews()

        chipTexts.forEach { text ->
            val chip = Chip(requireContext()).apply {
                this.text = text
                isClickable = true
                isCheckable = false
                isCloseIconVisible = true // Muestra el ícono de cierre en el chip

                // Establece un listener para el ícono de cierre
                setOnCloseIconClickListener {
                    // Elimina el chip del ChipGroup cuando el ícono de cierre es tocado
                    chipGroup.removeView(this)
                }
            }
            chipGroup.addView(chip)
        }
        return chipTexts
    }

    private fun Enviar(trabajador: String,Area: String){
        val cliente = Cliente()
        val equipo = Equipo("","","",
            "","","","","","","","","",false,"","","")
        val tarea = Tarea("","","","","",false)
        Lista = emptyList()
        Lista = listOf("1. Revisado","2. Reparado", "3. No desea reparar", "4. Pendiente por confirmar")
        cliente.addCliente(textViewNombre.text.toString(),textViewCedula.text.toString(),
            textViewTelefono.text.toString(),onSuccess = { clienteId ->
                equipo.addEquipo(clienteId,trabajador,Area,
                    textViewIngreso.text.toString(),
                    textViewEquipo.text.toString(),
                    textViewSerie.text.toString(),
                    textViewMarca.text.toString(),
                    textViewModelo.text.toString(),
                    textViewFecha.text.toString(),"",
                    textViewFalla.text.toString(),
                    textViewObservacion.text.toString(),
                    selectedItem,
                    "",
                    estado = false,
                    onSuccess = { equipoId ->
                        Lista.forEach { text ->
                        tarea.addTarea(equipoId,text,"","",false)
                        }
                        limpiar()
                        Toast.makeText(requireContext(),"¡Registro completado con éxito!", Toast.LENGTH_LONG).show()
                    },
                    onFailure = { exception ->
                        println("Error agregando documento: ${exception.message}")
                    }
                )
            },
            onFailure = { exception ->
                println("Error agregando documento: ${exception.message}")
            })
    }

    private fun limpiar(){
        textViewIngreso.text = ""
        textViewNombre. text = ""
        textViewCedula.text = ""
        textViewTelefono. text = ""
        textViewEquipo.text = ""
        textViewSerie. text = ""
        textViewMarca.text = ""
        textViewFecha.text = ""
        textViewModelo.text = ""
        textViewFalla. text = ""
        textViewObservacion.text = ""
        autoCompleteTextView.text = null
    }

    private fun mostrar(textoPdf: String) {
        val textoUnaLinea = textoPdf.replace("\n", " ")

        textViewIngreso.text = extraerTextoEntre(textoUnaLinea, "INGRESO N°", "Nombre")
        textViewNombre.text = extraerTextoEntre(textoUnaLinea, "Nombre", "Cédula")
        textViewCedula.text = extraerTextoEntre(textoUnaLinea, "Cédula", "Teléfono")
        textViewTelefono.text = extraerTextoEntre(textoUnaLinea, "Teléfono", "Equipo")
        textViewEquipo.text = extraerTextoEntre(textoUnaLinea, "Equipo", "N° Serie")
        textViewSerie.text = extraerTextoEntre(textoUnaLinea, "N° Serie", "Marca")
        textViewMarca.text = extraerTextoEntre(textoUnaLinea, "Marca", "Fecha")
        textViewFecha.text = extraerTextoEntre(textoUnaLinea, "Ingreso", "Modelo")
        textViewModelo.text = extraerTextoEntre(textoUnaLinea, "Modelo", "X")
        textViewFalla.text = extraerTextoEntre(textoUnaLinea, "Falla", "Observación")
        textViewObservacion.text = extraerTextoEntre(textoUnaLinea, "Observación", "Total")
    }

    private fun extraerTextoEntre(texto: String, inicio: String, fin: String): String {
        val pattern = "$inicio\\s+(.*?)\\s+$fin".toRegex()
        val resultado = pattern.find(texto)
        return resultado?.groups?.get(1)?.value?.trim() ?: ""
    }



    private fun selectPdfFile() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "application/pdf"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        startActivityForResult(intent, PICK_PDF_FILE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_PDF_FILE && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                mostrar(loadAndStripPdf(uri))
            }
        }
    }

    private fun loadAndStripPdf(uri: Uri): String {
        var document: PDDocument? = null
        var parsedText = ""
        try {
            requireActivity().contentResolver.openInputStream(uri)?.use { inputStream ->
                document = PDDocument.load(inputStream)
                PDFTextStripper().apply {
                    startPage = 0
                    endPage = 1
                    parsedText = getText(document)
                }
            }
        } catch (e: IOException) {
            Log.e("PdfBox-Android-Sample", "Exception thrown while loading document to strip or extracting text", e)
            return "" // Esto garantiza que si ocurre una excepción, se devolverá una cadena vacía.
        } finally {
            try {
                document?.close()
            } catch (e: IOException) {
                Log.e("PdfBox-Android-Sample", "Exception thrown while closing document", e)
            }
        }
        return parsedText // Esto devuelve el texto extraído después de procesar el documento PDF.
    }
}
