package com.dynamis

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class GestionSocios : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.gestion_socios)
        val btnSocio = findViewById<Button>(R.id.btnRegistrarSocioNuevo)
        btnSocio.setOnClickListener{
            val intent = Intent(this, RegistrarNuevoSocio::class.java)
            startActivity(intent)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.gestion_socios)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}}