package com.dynamis

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login) //

        val txtUsuario = findViewById<EditText>(R.id.txtUsuario)
        val txtPassword = findViewById<EditText>(R.id.txtPassword)
        val btnIniciarSesion = findViewById<Button>(R.id.btnIniciarSesion)

        btnIniciarSesion.setOnClickListener {
          try{  val usuario = txtUsuario.text.toString().trim()
                val password = txtPassword.text.toString().trim()

            if (usuario == "admin" && password == "1234") {
                val intent = Intent(this, MainMenu::class.java)
                startActivity(intent)
                finish() //
            } else {
                Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
            }
          }catch (e: Exception){
              Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
          }
        }
    }
}