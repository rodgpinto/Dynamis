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

class Listar : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.listar)

        val btnAtras = findViewById<ImageButton>(R.id.btnAtras)
        btnAtras.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()

        }

        val btnListarSocios = findViewById<Button>(R.id.btnListarSocios)
        btnListarSocios.setOnClickListener{
            val intent = Intent(this, ListarSocios::class.java)
            startActivity(intent)}

        /*val btnListarNoSocios = findViewById<Button>(R.id.btnListarNoSocios)
        btnListarNoSocios.setOnClickListener{
            val intent = Intent(this, ListarNoSocios::class.java)
            startActivity(intent)}

        val btnListarVencimientos = findViewById<Button>(R.id.btnListarVencimientos)
        btnListarVencimientos.setOnClickListener{
            val intent = Intent(this, ListarVencimientos::class.java)
            startActivity(intent)}*/

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.listar)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}