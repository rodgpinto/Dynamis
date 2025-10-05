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

class PagoCuota : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.pago_cuota)

        val btnAtras = findViewById<ImageButton>(R.id.btnAtras)
        btnAtras.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()

        }

        val spinnerTipoPago = findViewById<Spinner>(R.id.spinnerTipoPago)
        val opcionesTipoPago = arrayOf("Efectivo", "3 cuotas sin interés", "6 cuotas sin interés")

        val adapter3 = ArrayAdapter(this, android.R.layout.simple_spinner_item, opcionesTipoPago)
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTipoPago.adapter = adapter3

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.pago_cuota)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}