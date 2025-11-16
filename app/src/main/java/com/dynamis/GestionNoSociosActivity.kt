package com.dynamis

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class GestionNoSociosActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_gestion_no_socios)
        val tituloNavbar = findViewById<TextView>(R.id.navbar_title)
        tituloNavbar.text = "Gestión No Socios"
        val btnAtras = findViewById<ImageButton>(R.id.btnAtras)
        btnAtras.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
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

        val btnNoSocios = findViewById<Button>(R.id.btnRegistrarNoSocioNuevo)
        btnNoSocios.setOnClickListener{
            val intent = Intent(this, RegistrarNuevoNoSocioActivity::class.java)
            startActivity(intent)}

            val btnModificarNoSocios = findViewById<Button>(R.id.btnModificarNoSocios)
            btnModificarNoSocios.setOnClickListener{
                val intent = Intent(this, ModificarEliminarRegistroNoSocioActivity::class.java)
                startActivity(intent)}

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.gestion_no_socios)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    private fun mostrarDialogoDeAyuda() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Ayuda: Gestión No socio")
        builder.setMessage(
            "Esta es la pantalla para gestionar los no socios:\n\n" +
                    "  **Registrar:** Un nuevo Nosocio y su pago,\n" +
                    "  **Modificar:** Los datos de un Nosocio existente,\n" +
                    "  **Eliminar:** Eliminar un Nosocio existente."
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
