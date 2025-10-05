package com.dynamis

import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.widget.ImageButton
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class Actividades : AppCompatActivity() {

    private val dias = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado")
    private val actividades = listOf("Boxeo", "Musculación", "Pilates", "TRX", "Yoga", "Zumba")

    private val horarios = arrayOf(
        arrayOf("18:00", "18:00", "18:00", "18:00", "18:00", "10:00"), // Boxeo
        arrayOf("08:00", "08:00", "08:00", "08:00", "08:00", "—"),     // Musculación
        arrayOf("09:00", "—", "09:00", "—", "09:00", "—"),             // Pilates
        arrayOf("—", "17:00", "—", "17:00", "—", "11:00"),             // TRX
        arrayOf("07:00", "—", "07:00", "—", "07:00", "—"),             // Yoga
        arrayOf("—", "19:00", "—", "19:00", "—", "11:30")              // Zumba
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.actividades)

        val btnAtras = findViewById<ImageButton>(R.id.btnAtras)
        btnAtras.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val tabla = findViewById<TableLayout>(R.id.Actividades)

        val filaDias = TableRow(this)
        filaDias.addView(celda("Actividad", true))
        for (dia in dias) {
            filaDias.addView(celda(dia, true))
        }
        tabla.addView(filaDias)

        // Filas de actividades
        for (i in actividades.indices) {
            val fila = TableRow(this)
            fila.addView(celda(actividades[i], true)) // Actividad
            for (j in dias.indices) {
                fila.addView(celda(horarios[i][j]))
            }
            tabla.addView(fila)
        }
    }

    private fun celda(texto: String, esTitulo: Boolean = false): TextView {
        val tv = TextView(this)
        tv.text = texto
        tv.setPadding(12, 8, 12, 8)
        tv.gravity = Gravity.CENTER
        tv.setBackgroundResource(android.R.drawable.dialog_holo_light_frame)
        tv.setTextColor(if (esTitulo) android.graphics.Color.BLACK else android.graphics.Color.DKGRAY)
        tv.setTypeface(null, if (esTitulo) Typeface.BOLD else Typeface.NORMAL)
        return tv
    }
}