package com.dynamis

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class RegistroActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)
        val tituloNavbar = findViewById<TextView>(R.id.navbar_title)

        tituloNavbar.text = "Registrar"
        val btnAtras = findViewById<ImageButton>(R.id.btnAtras)
        btnAtras.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()

        }
        val btnSocio = findViewById<Button>(R.id.btnSocio)
        btnSocio.setOnClickListener {
            val intent = Intent(this, GestionSociosActivity::class.java)
            startActivity(intent)

        }

        val btnNoSocios = findViewById<Button>(R.id.btnNoSocios)
        btnNoSocios.setOnClickListener {
            val intent = Intent(this, GestionNoSociosActivity::class.java)
            startActivity(intent)

        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.Registro)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val btnAyuda = findViewById<ImageButton>(R.id.navbar_help_button)
        btnAyuda.setOnClickListener {
            mostrarDialogoDeAyuda()
        }

        val btnMenuSesion = findViewById<ImageButton>(R.id.navbar_menu_button)
        btnMenuSesion.setOnClickListener { view ->
            val popup = PopupMenu(this, view)
            popup.menuInflater.inflate(R.menu.session_menu, popup.menu)

            popup.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_cerrar_sesion -> {
                        cerrarSesion()
                        true
                    }

                    else -> false
                }
            }
            popup.show()
        }
    }

    private fun mostrarDialogoDeAyuda() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Ayuda: Registro")
        builder.setMessage(
            "Esta es la pantalla de registro. Desde aquí puedes:\n\n" +
                    "• **Registrar:** Socios o no socios."
        )
        builder.setPositiveButton("Entendido") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun cerrarSesion() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}