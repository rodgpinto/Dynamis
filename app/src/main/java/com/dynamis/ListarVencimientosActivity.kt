package com.dynamis

import android.annotation.SuppressLint
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.dynamis.database.DatabaseContract.UsuarioEntry
import com.dynamis.database.DynamisDbHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class ListarVencimientosActivity : AppCompatActivity() {

    private lateinit var dbHelper: DynamisDbHelper
    private lateinit var rvListaVencimientos: RecyclerView
    private lateinit var vencimientoAdapter: VencimientoAdapter



    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listar_vencimientos)

        dbHelper = DynamisDbHelper(this)
        rvListaVencimientos = findViewById(R.id.rvListaVencimientos)

        setupNavbar()

        setupRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        cargarVencimientos()
    }

    private fun setupNavbar() {
        val tituloNavbar = findViewById<TextView>(R.id.navbar_title)
        tituloNavbar.text = "Socios Vencidos"

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
        vencimientoAdapter = VencimientoAdapter(emptyList())
        rvListaVencimientos.adapter = vencimientoAdapter
    }
    @SuppressLint("Range")
    private fun cargarVencimientos() {

        lifecycleScope.launch {
            val (inicioHoy, _) = getTimestampsHoy()

            val listaVencidos: List<VencimientoItem> = withContext(Dispatchers.IO) {
                var cursor: Cursor? = null
                val tempLista = mutableListOf<VencimientoItem>()
                try {
                    cursor = dbHelper.getListarSociosVENCIDOS(inicioHoy)

                    if (cursor != null && cursor.moveToFirst()) {
                        do {
                            val nombre = cursor.getString(cursor.getColumnIndexOrThrow(UsuarioEntry.COLUMN_NOMBRE))
                            val apellido = cursor.getString(cursor.getColumnIndexOrThrow(UsuarioEntry.COLUMN_APELLIDO))
                            val dni = cursor.getString(cursor.getColumnIndexOrThrow(UsuarioEntry.COLUMN_DNI))
                            val vencimiento = cursor.getLong(cursor.getColumnIndexOrThrow("ultima_fecha_vencimiento"))

                            tempLista.add(
                                VencimientoItem(
                                    nombreCompleto = "$apellido, $nombre",
                                    dni = dni,
                                    fechaVencimiento = vencimiento
                                )
                            )
                        } while (cursor.moveToNext())
                    }
                } catch(e: Exception) {
                    e.printStackTrace()
                } finally {
                    cursor?.close()
                }
                tempLista
            }

            if (listaVencidos.isEmpty()) {
                Toast.makeText(this@ListarVencimientosActivity, "No hay socios vencidos que mostrar", Toast.LENGTH_LONG).show()
                rvListaVencimientos.visibility = View.GONE
            } else {
                rvListaVencimientos.visibility = View.VISIBLE
                vencimientoAdapter.actualizarLista(listaVencidos)
            }
        }
    }

    private fun getTimestampsHoy(): Pair<Long, Long> {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val startOfDayMillis = cal.timeInMillis

        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        cal.set(Calendar.MILLISECOND, 999)
        val endOfDayMillis = cal.timeInMillis

        return Pair(startOfDayMillis, endOfDayMillis)
    }

    private fun mostrarDialogoDeAyuda() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Ayuda: Vencimientos")
        builder.setMessage("Esta pantalla lista todos los socios cuya cuota ha expirado, requiriendo renovación inmediata.")
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