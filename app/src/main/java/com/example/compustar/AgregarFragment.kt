package com.example.compustar
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.compustar.Modelo.Area
import com.example.compustar.Modelo.AreaDetalle
import com.example.compustar.Modelo.Cliente
import com.example.compustar.Modelo.Equipo
import com.example.compustar.Modelo.Tarea
import com.example.compustar.Modelo.Trabajador
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper
import java.io.IOException

class AgregarFragment : Fragment(R.layout.fragment_agregar) {

    private val PICK_PDF_FILE = 1
    private lateinit var tv: TextView
    private lateinit var textViewIngreso: TextView
    private lateinit var textViewNombre: TextView
    private lateinit var textViewCedula: TextView
    private lateinit var textViewTelefono: TextView
    private lateinit var textViewEquipo: TextView
    private lateinit var textViewSerie: TextView
    private lateinit var textViewMarca: TextView
    private lateinit var textViewFecha: TextView
    private lateinit var textViewModelo: TextView
    private lateinit var textViewTarea: TextView
    private lateinit var textViewFalla: TextView
    private lateinit var textViewObservacion: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnAgregarBD:Button = view.findViewById(R.id.btnAgregarBD)
        val btnAgregar: ImageButton = view.findViewById(R.id.ibtnPDF)
        textViewIngreso = view.findViewById(R.id.tvIngreso)
        textViewNombre = view.findViewById(R.id.tvNombre)
        textViewCedula = view.findViewById(R.id.tvCedula)
        textViewTelefono = view.findViewById(R.id.tvTelefono)
        textViewEquipo = view.findViewById(R.id.tvEquipo)
        textViewSerie = view.findViewById(R.id.tvSerie)
        textViewMarca = view.findViewById(R.id.tvMarca)
        textViewFecha = view.findViewById(R.id.tvFecha)
        textViewModelo = view.findViewById(R.id.tvModelo)
        textViewTarea = view.findViewById(R.id.tvTarea)
        textViewFalla = view.findViewById(R.id.tvFalla)
        textViewObservacion = view.findViewById(R.id.tvObservacion)

        // Inicialización para la carga de recursos de PDFBox en Android
        PDFBoxResourceLoader.init(requireActivity().applicationContext)

        btnAgregar.setOnClickListener {
            selectPdfFile()
        }

        btnAgregarBD.setOnClickListener {
            Enviar()
        }
    }

    private fun Enviar(){
        val cliente = Cliente()
        val equipo = Equipo("AquiNoImportaElIDPorqueEstasAdd","hola","hola",
            textViewIngreso.text.toString(),
            textViewEquipo.text.toString(),
            textViewSerie.text.toString(),
            textViewMarca.text.toString(),
            textViewModelo.text.toString(),
            textViewFecha.text.toString(),textViewFecha.text.toString(),
            textViewFalla.text.toString(),
            textViewObservacion.text.toString(),estado = false)
        val tarea = Tarea()

        cliente.addCliente(textViewNombre.text.toString(),textViewCedula.text.toString(),textViewTelefono.text.toString())
        equipo.addEquipo()

        tarea.addTarea("hola","hola","","",false)

        textViewIngreso.text = ""
        textViewNombre. text = ""
        textViewCedula.text = ""
        textViewTelefono. text = ""
        textViewEquipo.text = ""
        textViewSerie. text = ""
        textViewMarca.text = ""
        textViewFecha.text = ""
        textViewModelo.text = ""
        textViewTarea.text = ""
        textViewFalla. text = ""
        textViewObservacion.text = ""
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
        textViewTarea.text = extraerTextoEntre(textoUnaLinea, "X", "Falla")
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
