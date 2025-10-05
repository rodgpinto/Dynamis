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

class ModificarEliminarRegistroNoSocio : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.modificar_eliminar_registro_no_socio)

        val btnAtras = findViewById<ImageButton>(R.id.btnAtras)
        btnAtras.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()

        }

        val spinnerDocumento = findViewById<Spinner>(R.id.spinnerDocumento)
        val opcionesDocumento = arrayOf("DNI", "Pasaporte", "LC")

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, opcionesDocumento)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDocumento.adapter = adapter


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.modificar_eliminar_registro_no_socio)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}