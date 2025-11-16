package com.dynamis

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu


class CarnetActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carnet)
        val tituloNavbar = findViewById<TextView>(R.id.navbar_title)
        tituloNavbar.text = "Carnet"
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

        val nombre = intent.getStringExtra("NOMBRE")
        val apellido = intent.getStringExtra("APELLIDO")
        val dni = intent.getStringExtra("DNI")
        val fechaNac = intent.getStringExtra("FECHA_NAC")
        val fotoUriString = intent.getStringExtra("FOTO_URI")
        val direccion = intent.getStringExtra("DIRECCION")

        val txtNombre = findViewById<TextView>(R.id.carnet_nombre)
        val txtApellido = findViewById<TextView>(R.id.carnet_apellido)
        val txtDni = findViewById<TextView>(R.id.carnet_dni)
        val txtFechaNac = findViewById<TextView>(R.id.carnet_fecha_nac)
        val imgFoto = findViewById<ImageView>(R.id.carnet_foto)
        val txtDireccion = findViewById<TextView>(R.id.carnet_direccion)

        txtNombre.text = nombre
        txtApellido.text = apellido
        txtDni.text = "DNI: $dni"
        txtFechaNac.text = "Nac: $fechaNac"
        txtDireccion.text = "Direc: $direccion"




    }
    private fun mostrarDialogoDeAyuda() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Ayuda: Entrega de carnet")
        builder.setMessage(
            "En esta pantalla se visualiza el carnet del socio registrado."
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