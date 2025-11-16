package com.dynamis

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.dynamis.database.DatabaseContract.UsuarioEntry
import com.dynamis.database.DynamisDbHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ListarNoSociosActivity : AppCompatActivity() {

    private lateinit var dbHelper: DynamisDbHelper
    private lateinit var rvListaNoSocios: RecyclerView
    private lateinit var adapter: NoSociosAdapter

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_listar_no_socios)

        dbHelper = DynamisDbHelper(this)
        rvListaNoSocios = findViewById(R.id.rvListaNoSocios)

        setupNavbar()

        setupRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        cargarNoSocios()
    }

    private fun setupNavbar() {
        val tituloNavbar = findViewById<TextView>(R.id.navbar_title)
        tituloNavbar.text = "Listar No Socios"

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
    }

    private fun setupRecyclerView() {
        adapter = NoSociosAdapter(emptyList())
        rvListaNoSocios.adapter = adapter
    }

    private fun cargarNoSocios() {
        lifecycleScope.launch(Dispatchers.IO) {

            val listaNoSocios = mutableListOf<NoSocioItem>()

            val cursor = dbHelper.getUsuarios(isSocio = false)

            try {
                if (cursor.moveToFirst()) {
                    do {
                        val nombre = cursor.getString(cursor.getColumnIndexOrThrow(UsuarioEntry.COLUMN_NOMBRE))
                        val apellido = cursor.getString(cursor.getColumnIndexOrThrow(UsuarioEntry.COLUMN_APELLIDO))
                        val dni = cursor.getString(cursor.getColumnIndexOrThrow(UsuarioEntry.COLUMN_DNI))
                        val fechaNac = cursor.getString(cursor.getColumnIndexOrThrow(UsuarioEntry.COLUMN_FECHA_NACIMIENTO))

                        val item = NoSocioItem(
                            nombreCompleto = "$nombre $apellido",
                            dni = dni,
                            fecha = fechaNac
                        )
                        listaNoSocios.add(item)
                    } while (cursor.moveToNext())
                }
            } catch (e: Exception) {
            } finally {
                cursor.close()
            }

            withContext(Dispatchers.Main) {
                if (listaNoSocios.isEmpty()) {
                    Toast.makeText(this@ListarNoSociosActivity, "No se encontraron no-socios", Toast.LENGTH_SHORT).show()
                }
                adapter.actualizarLista(listaNoSocios)
            }
        }
    }

    private fun mostrarDialogoDeAyuda() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Ayuda: Listar No Socios")
        builder.setMessage("Esta pantalla muestra una lista de todas las personas registradas que no son socios.")
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