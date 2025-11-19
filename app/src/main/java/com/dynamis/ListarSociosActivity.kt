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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ListarSociosActivity : AppCompatActivity() {

    private lateinit var dbHelper: DynamisDbHelper
    private lateinit var rvListaSocios: RecyclerView
    private lateinit var adapter: SocioAdapter

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_listar_socios)

        dbHelper = DynamisDbHelper(this)
        rvListaSocios = findViewById(R.id.rvListaSocios)


        setupNavbar()


        setupRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        cargarSocios()
    }

    private fun setupNavbar() {
        val tituloNavbar = findViewById<TextView>(R.id.navbar_title)
        tituloNavbar.text = "Listar Socios"

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
        adapter = SocioAdapter(emptyList())
        rvListaSocios.adapter = adapter
    }

    private fun cargarSocios() {
        lifecycleScope.launch(Dispatchers.IO) {

            val listaSocios = mutableListOf<SocioItem>()

            val cursor = dbHelper.getListarTODOSSocios()

            try {
                if (cursor.moveToFirst()) {
                    do {

                        val apellido =
                            cursor.getString(cursor.getColumnIndexOrThrow(UsuarioEntry.COLUMN_APELLIDO))
                        val nombre =
                            cursor.getString(cursor.getColumnIndexOrThrow(UsuarioEntry.COLUMN_NOMBRE))
                        val dni =
                            cursor.getString(cursor.getColumnIndexOrThrow(UsuarioEntry.COLUMN_DNI))

                        val vencimientoTimestamp =
                            cursor.getLong(cursor.getColumnIndexOrThrow("ultima_fecha_vencimiento"))

                        val fechaVencimientoStr = if (vencimientoTimestamp == 0L) {
                            "Sin Pagos"
                        } else {
                            formatearFecha(vencimientoTimestamp)
                        }

                        val item = SocioItem(
                            nombreCompleto = "$apellido $nombre",
                            dni = dni,
                            fechaVencimiento = fechaVencimientoStr
                        )
                        listaSocios.add(item)
                    } while (cursor.moveToNext())
                }
            } catch (e: Exception) {
            } finally {
                cursor.close()
            }

            withContext(Dispatchers.Main) {
                if (listaSocios.isEmpty()) {
                    Toast.makeText(
                        this@ListarSociosActivity,
                        "No se encontraron socios",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                adapter.actualizarLista(listaSocios)
            }
        }
    }

    private fun formatearFecha(timestamp: Long): String {
        return try {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val date = Date(timestamp)
            sdf.format(date)
        } catch (e: Exception) {
            "Error"
        }
    }

    private fun mostrarDialogoDeAyuda() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Ayuda: Listar Socios")
        builder.setMessage("Esta pantalla muestra una lista de todos los socios registrados y su última fecha de vencimiento.")
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