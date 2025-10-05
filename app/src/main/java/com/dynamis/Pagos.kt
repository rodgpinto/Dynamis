package com.dynamis

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Pagos : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.pagos)

        val btnAtras = findViewById<ImageButton>(R.id.btnAtras)
        btnAtras.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()

        }
        val btnPagarCuota = findViewById<Button>(R.id.btnPagarCuota)
        btnPagarCuota.setOnClickListener{
            val intent = Intent(this, PagoCuota::class.java)
            startActivity(intent)}

        val btnPagarActividad = findViewById<Button>(R.id.btnPagarActividad)
        btnPagarActividad.setOnClickListener{
            val intent = Intent(this, PagoActividad::class.java)
            startActivity(intent)}

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.pagos)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}