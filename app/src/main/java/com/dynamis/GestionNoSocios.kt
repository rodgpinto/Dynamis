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

class GestionNoSocios : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.gestion_no_socios)
        val btnAtras = findViewById<ImageButton>(R.id.btnAtras)
        btnAtras.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()

        }

        val btnNoSocios = findViewById<Button>(R.id.btnRegistrarNoSocioNuevo)
        btnNoSocios.setOnClickListener{
            val intent = Intent(this, RegistrarNuevoNoSocio::class.java)
            startActivity(intent)}

            val btnModificarNoSocios = findViewById<Button>(R.id.btnModificarNoSocios)
            btnModificarNoSocios.setOnClickListener{
                val intent = Intent(this, ModificarEliminarRegistroNoSocio::class.java)
                startActivity(intent)}

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.gestion_no_socios)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
