package com.dynamis

import android.annotation.SuppressLint
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
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
import java.util.Calendar

class MainMenuActivity : AppCompatActivity() {

    private lateinit var dbHelper: DynamisDbHelper
    private lateinit var rvLista: RecyclerView
    private lateinit var vencimientoAdapter: VencimientoAdapter
    private lateinit var txtVencimientos: TextView

    @SuppressLint("MissingInflatedId", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)

        val txtBienvenida = findViewById<TextView>(R.id.txtBienvenida)
        val usuario = intent.getStringExtra("usuario")
        txtBienvenida.text = "Bienvenido, $usuario"

        val btnRegistro = findViewById<Button>(R.id.btnRegistro)
        btnRegistro.setOnClickListener {
            val intent = Intent(this, RegistroActivity::class.java)
            startActivity(intent)
        }
        val btnPagos = findViewById<Button>(R.id.btnPagos)
        btnPagos.setOnClickListener {
            val intent = Intent(this, PagosActivity::class.java)
            startActivity(intent)
        }
        val btnListar = findViewById<Button>(R.id.btnListar)
        btnListar.setOnClickListener {
            val intent = Intent(this, ListarActivity::class.java)
            startActivity(intent)
        }
        val btnActividad = findViewById<Button>(R.id.btnActividad)
        btnActividad.setOnClickListener {
            val intent = Intent(this, ActividadesActivity::class.java)
            startActivity(intent)
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
        val rootView = findViewById<android.view.View>(R.id.activity_main_menu)
        ViewCompat.setOnApplyWindowInsetsListener(rootView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        dbHelper = DynamisDbHelper(this)
        rvLista = findViewById(R.id.rvLista)
        txtVencimientos = findViewById(R.id.txtVencimientos)
        setupRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        cargarVencimientos()
    }

    private fun setupRecyclerView() {
        vencimientoAdapter = VencimientoAdapter(emptyList())
        rvLista.adapter = vencimientoAdapter
    }


    /**
     * Usa Coroutines para cargar los vencimientos desde la BD
     * en un hilo de fondo y luego mostrarlos en la UI.
     */
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
                txtVencimientos.text = "No hay socios vencidos"
                txtVencimientos.visibility = View.VISIBLE
                rvLista.visibility = View.GONE
            } else {
                txtVencimientos.text = "Socios Vencidos:"
                txtVencimientos.visibility = View.VISIBLE
                rvLista.visibility = View.VISIBLE
                vencimientoAdapter.actualizarLista(listaVencidos)
            }
        }
    }

    /**
     * (Esta función no cambia, está correcta)
     */
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
        builder.setTitle("Ayuda: Menú Principal")
        builder.setMessage(
            "Esta es la pantalla principal. Desde aquí puedes:\n\n" +
                    "• **Registro:** Gestionar socios y no socios.\n" +
                    "• **Pagos:** Registrar el pago de cuotas o actividades.\n" +
                    "• **Listar:** Ver listas de socios, no socios y vencimientos.\n" +
                    "• **Actividad:** Registrar la entrada/salida de personas al gimnasio.\n\n" +
                    "La lista 'Socios Vencidos' muestra a todos los que deben renovar."
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