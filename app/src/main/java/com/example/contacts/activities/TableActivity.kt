package com.example.contacts.activities

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.contactsgenerator.R
import com.example.contacts.presentation.dto.ContactDTO
import java.io.File
import java.io.IOException

class TableActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_table)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val contactList =
            intent.getParcelableArrayListExtra<ContactDTO>("contactsList") as ArrayList<ContactDTO>
        if (!contactList.isEmpty()) {
            showTable(contactList)
            val btnGenerate = findViewById<Button>(R.id.btnGenerate)
            btnGenerate.setOnClickListener {
                generatePdfReport(contactList)
            }
        }
    }

    private fun showTable(contactList: ArrayList<ContactDTO>) {
        val tableLayout = findViewById<TableLayout>(R.id.tableLayout)

        //se crea la caberecea de la tabla
        val columnsRow = TableRow(this)

        val nameTextColumnView = TextView(this).apply {
            text = "Nombre del contacto"
            textSize=16f
            setPadding(8, 8, 8, 8)
            setTypeface(null,Typeface.BOLD)
        }


        val numberTextColumnView = TextView(this).apply {
            text = "Numero de telefono"
            textSize=16f
            setPadding(8, 8, 8, 8)
            setTypeface(null,Typeface.BOLD)
        }

        columnsRow.addView(nameTextColumnView)
        columnsRow.addView(numberTextColumnView)

        val linea = createHorizontalLine(this)
        tableLayout.addView(columnsRow)
        tableLayout.addView(linea)


        contactList.forEach {
            val tableRow = TableRow(this)
            val nameTextView = TextView(this)
            nameTextView.text = it.name
            nameTextView.setPadding(8, 8, 8, 8)

            val numberTextView = TextView(this)
            numberTextView.text = it.number
            numberTextView.setPadding(8, 8, 8, 8)

            tableRow.addView(nameTextView)
            tableRow.addView(numberTextView)

            tableLayout.addView(tableRow)
        }
    }

    private fun generatePdfReport(contactsList: List<ContactDTO>) {
        val pdfDocument = PdfDocument()
        var pageNumber = 1

        // Configuración básica para la página
        val pageWidth = 500
        val pageHeight = 600
        val rowsPerPage = 25  // Número máximo de filas por página
        val startYPosition = 40f  // Posición inicial en Y
        val rowHeight = 20f  // Espaciado entre filas

        var currentYPosition = startYPosition
        var currentRow = 0

        // Crear una nueva página
        var pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
        var page = pdfDocument.startPage(pageInfo)
        var canvas = page?.canvas ?: throw IllegalStateException("Canvas is null")
        val paint = Paint()

        // Encabezados de la tabla
        paint.textSize = 12f
        paint.isFakeBoldText = true
        val nameColumnX = 20f
        val numberColumnX = 300f


        // Dibujar encabezados en la primera página
        canvas.drawText("Nombre", nameColumnX, currentYPosition, paint)
        canvas.drawText("Número", numberColumnX, currentYPosition, paint)
        currentYPosition += 10
        canvas.drawLine(20f, currentYPosition, (pageWidth - 20).toFloat(), currentYPosition, paint)
        currentYPosition += rowHeight

        paint.isFakeBoldText = false  // Estilo de texto normal para los datos

        // Iterar sobre la lista de contactos y dibujar en el PDF
        for (contact in contactsList) {
            // Si se alcanzan las filas permitidas, iniciar nueva página
            if (currentRow == rowsPerPage) {
                paint.isFakeBoldText = true
                pdfDocument.finishPage(page)
                pageNumber++
                pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
                page = pdfDocument.startPage(pageInfo)
                canvas = page?.canvas ?: throw IllegalStateException("Canvas is null")
                currentYPosition = startYPosition
                currentRow = 0

                canvas.drawText("Nombre", nameColumnX, currentYPosition, paint)
                canvas.drawText("Número", numberColumnX, currentYPosition, paint)
                currentYPosition += 10
                canvas.drawLine(20f, currentYPosition, (pageWidth - 20).toFloat(), currentYPosition, paint)
                currentYPosition += rowHeight
                paint.isFakeBoldText = false  // Estilo de texto normal para los datos
            }

            // Dibujar cada contacto
            canvas.drawText(contact.name, nameColumnX, currentYPosition, paint)
            canvas.drawText(contact.number, numberColumnX, currentYPosition, paint)
            currentYPosition += rowHeight
            currentRow++
        }

        // Finalizar la última página
        pdfDocument.finishPage(page)

        // Guardar el PDF en el almacenamiento interno
        val file = File(getExternalFilesDir(null), "contacts_report.pdf")
        try {
            file.outputStream().use {
                pdfDocument.writeTo(it)
            }
            Toast.makeText(this, "PDF generado: ${file.absolutePath}", Toast.LENGTH_LONG).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Error al generar el PDF", Toast.LENGTH_SHORT).show()
        }

        // Cerrar el documento
        pdfDocument.close()

        // Compartir el archivo PDF
        sharePdf(file)
    }

    private fun sharePdf(file: File) {
        val uri = FileProvider.getUriForFile(this, "${packageName}.provider", file)

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        startActivity(Intent.createChooser(intent, "Compartir PDF con"))

    }

    fun createHorizontalLine(context: Context): View {
        // Create a new view
        val line = View(context)

        // Set the dimensions of the line: width match_parent and height 2dp
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, // Full width
            TypedValue.applyDimension(  // Height of 1dp converted to pixels
                TypedValue.COMPLEX_UNIT_DIP,
                1f,
                context.resources.displayMetrics
            ).toInt()
        )

        // Apply the dimensions to the view
        line.layoutParams = params

        // Set the background color (e.g., a dark gray color)
        line.setBackgroundColor(Color.GRAY)

        return line
    }

}