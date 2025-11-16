package com.dynamis

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.widget.ImageButton
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu

class ActividadesActivity : AppCompatActivity() {

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
        setContentView(R.layout.activity_actividades)

        val tituloNavbar = findViewById<TextView>(R.id.navbar_title)
        tituloNavbar.text = "Actividades"
        val btnAtras = findViewById<ImageButton>(R.id.btnAtras)
        btnAtras.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        val btnAyuda = findViewById<ImageButton>(R.id.navbar_help_button)
        btnAyuda.setOnClickListener {
            mostrarDialogoDeAyuda()
        }
        val btnMenuSesion = findViewById<ImageButton>(R.id.navbar_menu_button)
        btnMenuSesion.setOnClickListener { view ->
            val popup = PopupMenu(this, view)
            popup.menuInflater.inflate(R.menu.session_menu, popup.menu)

            popup.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_cerrar_sesion -> {
                        cerrarSesion()
                        true
                    }

                    else -> false
                }
            }
            popup.show()
        }

        val tabla = findViewById<TableLayout>(R.id.Actividades)

        val filaDias = TableRow(this)
        filaDias.addView(celda("Actividad", true))
        for (dia in dias) {
            filaDias.addView(celda(dia, true))
        }
        tabla.addView(filaDias)

        for (i in actividades.indices) {
            val fila = TableRow(this)
            fila.addView(celda(actividades[i], true))
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
    private fun mostrarDialogoDeAyuda() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Ayuda: Registrar nuevo socio")
        builder.setMessage(
            "En esta seccion podrás ver las actividades y sus horarios"
        )
        builder.setPositiveButton("Entendido") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun cerrarSesion() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}