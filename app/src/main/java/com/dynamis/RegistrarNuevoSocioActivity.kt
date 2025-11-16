package com.dynamis

import android.annotation.SuppressLint
import android.content.Intent
import android.database.sqlite.SQLiteConstraintException
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.dynamis.database.DynamisDbHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class RegistrarNuevoSocioActivity : AppCompatActivity() {

    private lateinit var dbHelper: DynamisDbHelper

    @SuppressLint("MissingInflatedId", "CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registrar_nuevo_socio)

        dbHelper = DynamisDbHelper(this)

        val tituloNavbar = findViewById<TextView>(R.id.navbar_title)
        tituloNavbar.text = "Registrar nuevo socio"
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


        val spinnerDocumento = findViewById<Spinner>(R.id.spinnerDocumento)
        val opcionesDocumento = arrayOf("DNI", "Pasaporte", "LC")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, opcionesDocumento)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDocumento.adapter = adapter

        val spinnerActividad = findViewById<Spinner>(R.id.spinnerActividad)
        val opcionesActividad = arrayOf("Boxeo", "Musculación", "Pilates", "TRX", "Yoga", "Zumba")
        val adapter2 = ArrayAdapter(this, android.R.layout.simple_spinner_item, opcionesActividad)
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerActividad.adapter = adapter2

        val spinnerTipoPago = findViewById<Spinner>(R.id.spinnerTipoPago)
        val opcionesTipoPago = arrayOf("Efectivo", "3 cuotas sin interés", "6 cuotas sin interés")
        val adapter3 = ArrayAdapter(this, android.R.layout.simple_spinner_item, opcionesTipoPago)
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTipoPago.adapter = adapter3

        val btnIngresar = findViewById<Button>(R.id.btnIngresar)
        btnIngresar.setOnClickListener {
            intentarGuardarSocio()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.registrar_nuevo_socio)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun intentarGuardarSocio() {

        val nombre = findViewById<EditText>(R.id.nombre).text.toString()
        val apellido = findViewById<EditText>(R.id.apellido).text.toString()
        val tipoDoc = findViewById<Spinner>(R.id.spinnerDocumento).selectedItem.toString()
        val dni = findViewById<EditText>(R.id.editTextNumber).text.toString()
        val fechaNac = findViewById<EditText>(R.id.txtFechaNacimiento).text.toString()
        val direccion = findViewById<EditText>(R.id.direccion).text.toString()

        val tieneFicha = findViewById<CheckBox>(R.id.chkFichaInscripcion).isChecked
        val tieneApto = findViewById<CheckBox>(R.id.chkAptoMedico).isChecked

        val actividad = findViewById<Spinner>(R.id.spinnerActividad).selectedItem.toString()
        val montoStr = findViewById<EditText>(R.id.txtPagoCuota).text.toString()
        val tipoPago = findViewById<Spinner>(R.id.spinnerTipoPago).selectedItem.toString()
        val fechaPagoStr = findViewById<EditText>(R.id.txtFechaPago).text.toString()

        if (nombre.isEmpty() || apellido.isEmpty() || dni.isEmpty() || fechaNac.isEmpty() || montoStr.isEmpty() || fechaPagoStr.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        val monto = montoStr.toDoubleOrNull()
        if (monto == null || monto <= 0) {
            Toast.makeText(this, "El monto del pago no es válido", Toast.LENGTH_SHORT).show()
            return
        }

        val fechaPagoLong = convertirFechaATimestamp(fechaPagoStr)
        if (fechaPagoLong == null) {
            Toast.makeText(
                this,
                "Formato de fecha de pago incorrecto (DD/MM/AAAA)",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val fechaVencimiento = calcularVencimiento(fechaPagoLong)

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val exitoUsuario = dbHelper.insertarUsuario(
                    nombre = nombre,
                    apellido = apellido,
                    tipoDoc = tipoDoc,
                    dni = dni,
                    fechaNac = fechaNac,
                    direccion = direccion,
                    fotoUri = null,
                    tieneFicha = tieneFicha,
                    tieneApto = tieneApto,
                    isSocio = true
                )

                if (!exitoUsuario) {
                    throw SQLiteConstraintException("Error al guardar usuario, DNI duplicado.")
                }

                val exitoPago = dbHelper.insertarPago(
                    usuarioDni = dni,
                    monto = monto,
                    fechaPago = fechaPagoLong,
                    fechaVencimiento = fechaVencimiento,
                    tipoPago = tipoPago,
                    actividad = actividad
                )

                if (!exitoPago) {
                    throw Exception("Error al guardar el pago.")
                }

                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@RegistrarNuevoSocioActivity,
                        "Socio guardado con éxito",
                        Toast.LENGTH_SHORT
                    ).show()

                    val intent =
                        Intent(this@RegistrarNuevoSocioActivity, CarnetActivity::class.java)
                    intent.putExtra("NOMBRE", nombre)
                    intent.putExtra("APELLIDO", apellido)
                    intent.putExtra("DNI", dni)
                    intent.putExtra("FECHA_NAC", fechaNac)
                    intent.putExtra("DIRECCION", direccion)

                    startActivity(intent)
                    finish()
                }

            } catch (e: SQLiteConstraintException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@RegistrarNuevoSocioActivity,
                        "Error: El DNI ya existe",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@RegistrarNuevoSocioActivity,
                        "Error: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }


    private fun convertirFechaATimestamp(fecha: String): Long? {
        return try {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            sdf.isLenient = false
            val date = sdf.parse(fecha)
            date?.time
        } catch (e: Exception) {
            null
        }
    }


    private fun calcularVencimiento(fechaPagoLong: Long): Long {
        val calendario = Calendar.getInstance()
        calendario.timeInMillis = fechaPagoLong
        calendario.add(Calendar.DAY_OF_YEAR, 30)
        return calendario.timeInMillis
    }

    private fun mostrarDialogoDeAyuda() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Ayuda: Registrar nuevo socio")
        builder.setMessage(
            "Esta es la pantalla de registro. Desde aquí puedes:\n\n" +
                    "• **Registrar:** Un nuevo socio y su pago,\n" +
                    " luego del registro, se mostrara el carnet."
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