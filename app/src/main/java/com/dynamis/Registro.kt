package com.dynamis

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Registro : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.registro)
        val btnAtras = findViewById<ImageButton>(R.id.btnAtras)
        btnAtras.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()

        }
        val btnSocio = findViewById<Button>(R.id.btnSocio)
        btnSocio.setOnClickListener{
            val intent = Intent(this, GestionSocios::class.java)
            startActivity(intent)

        }

        val btnNoSocios = findViewById<Button>(R.id.btnNoSocios)
        btnNoSocios.setOnClickListener{
            val intent = Intent(this, GestionNoSocios::class.java)
            startActivity(intent)

        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.Registro)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}