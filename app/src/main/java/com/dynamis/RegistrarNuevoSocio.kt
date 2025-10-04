package com.dynamis

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Spinner

class RegistrarNuevoSocio : AppCompatActivity() {
    @SuppressLint("MissingInflatedId", "CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.registrar_nuevo_socio)

        val btnAtras = findViewById<ImageButton>(R.id.btnAtras)
        btnAtras.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()

        }

        val spinnerDocumento = findViewById<Spinner>(R.id.spinnerDocumento)
        val opcionesDocumento = arrayOf("DNI", "Pasaporte", "LC")

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, opcionesDocumento)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDocumento.adapter = adapter

        val spinnerActividad = findViewById<Spinner>(R.id.spinnerActividad)
        val opcionesActividad = arrayOf("Boxeo", "Musculación", "Pilates","TRX", "Yoga", "Zumba")

        val adapter2 = ArrayAdapter(this, android.R.layout.simple_spinner_item, opcionesActividad)
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerActividad.adapter = adapter2

        val spinnerTipoPago = findViewById<Spinner>(R.id.spinnerTipoPago)
        val opcionesTipoPago = arrayOf("Efectivo", "3 cuotas sin interés", "6 cuotas sin interés")

        val adapter3 = ArrayAdapter(this, android.R.layout.simple_spinner_item, opcionesTipoPago)
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTipoPago.adapter = adapter3



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.registrar_nuevo_socio)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}