package com.dynamis

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class PagoActividad : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.pago_actividad)

        val btnAtras = findViewById<ImageButton>(R.id.btnAtras)
        btnAtras.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()

        }

        val spinnerActividad = findViewById<Spinner>(R.id.spinnerActividad)
        val opcionesActividad = arrayOf("Boxeo", "Musculación", "Pilates","TRX", "Yoga", "Zumba")

        val adapter2 = ArrayAdapter(this, android.R.layout.simple_spinner_item, opcionesActividad)
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerActividad.adapter = adapter2

        val spinnerTipoPago = findViewById<Spinner>(R.id.spinnerTipoPago)
        val opcionesTipoPago = arrayOf("Efectivo", "3 cuotas sin interés", "6 cuotas sin interés")

        val adapter3 = ArrayAdapter(this, android.R.layout.simple_spinner_item, opcionesTipoPago)
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTipoPago.adapter = adapter3

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.pago_actividad)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}