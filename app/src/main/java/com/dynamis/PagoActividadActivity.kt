package com.dynamis

import android.annotation.SuppressLint
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
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

class PagoActividadActivity : AppCompatActivity() {

    private lateinit var dbHelper: DynamisDbHelper
    private lateinit var dni: EditText
    private lateinit var cuota: EditText
    private lateinit var fechaPago: EditText
    private lateinit var spinnerTipoPago: Spinner
    private lateinit var spinnerActividad: Spinner
    private lateinit var btnIngresar: Button
    private lateinit var btnLimpiar: Button

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pago_actividad)

        dbHelper = DynamisDbHelper(this)

        dni = findViewById(R.id.txtDocumento)
        cuota = findViewById(R.id.txtCuota)
        fechaPago = findViewById(R.id.txtFechaPago)
        spinnerTipoPago = findViewById(R.id.spinnerTipoPago)
        spinnerActividad = findViewById(R.id.spinnerActividad)
        btnIngresar = findViewById(R.id.btnIngresar)
        btnLimpiar = findViewById(R.id.btnLimpiar)

        val tituloNavbar = findViewById<TextView>(R.id.navbar_title)
        tituloNavbar.text = "Pago de Actividad"

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

        setupSpinners()

        btnLimpiar.setOnClickListener {
            limpiarCampos()
        }
        btnIngresar.setOnClickListener {
            intentarGuardarPago()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.pago_actividad)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupSpinners() {
        val opcionesActividad = arrayOf("Boxeo", "Musculación", "Pilates", "TRX", "Yoga", "Zumba")
        val adapterAct = ArrayAdapter(this, android.R.layout.simple_spinner_item, opcionesActividad)
        adapterAct.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerActividad.adapter = adapterAct

        val opcionesTipoPago = arrayOf("Efectivo", "3 cuotas sin interés", "6 cuotas sin interés")
        val adapterPago = ArrayAdapter(this, android.R.layout.simple_spinner_item, opcionesTipoPago)
        adapterPago.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTipoPago.adapter = adapterPago
    }

    private fun mostrarDialogoDeAyuda() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Ayuda: Pago de Actividad")
        builder.setMessage(
            "Esta pantalla se usa para registrar el pago de una actividad individual o pase diario, generalmente para No-Socios.\n\n" +
                    "1. Ingrese el DNI del usuario (debe estar pre-registrado).\n" +
                    "2. Complete los datos del pago."
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


    private fun limpiarCampos() {
        dni.text.clear()
        cuota.text.clear()
        fechaPago.text.clear()
        spinnerTipoPago.setSelection(0)
        spinnerActividad.setSelection(0)
        dni.requestFocus()
    }

    private fun intentarGuardarPago() {
        val dniStr = dni.text.toString()
        val montoStr = cuota.text.toString()
        val fechaPagoStr = fechaPago.text.toString()
        val tipoPago = spinnerTipoPago.selectedItem.toString()
        val actividad = spinnerActividad.selectedItem.toString()

        if (dniStr.isEmpty() || montoStr.isEmpty() || fechaPagoStr.isEmpty()) {
            Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show()
            return
        }
        val monto = montoStr.toDoubleOrNull()
        if (monto == null || monto <= 0) {
            Toast.makeText(this, "Monto inválido", Toast.LENGTH_SHORT).show()
            return
        }
        val fechaPagoLong = convertirFechaATimestamp(fechaPagoStr)
        if (fechaPagoLong == null) {
            Toast.makeText(this, "Formato de fecha incorrecto (DD/MM/AAAA)", Toast.LENGTH_SHORT).show()
            return
        }

        val fechaVencimiento = calcularVencimiento(fechaPagoLong)

        lifecycleScope.launch(Dispatchers.IO) {
            var cursor: Cursor? = null
            try {
                cursor = dbHelper.consultarUsuario(dniStr)
                if (cursor != null && cursor.moveToFirst()) {
                    val exitoPago = dbHelper.insertarPago(
                        usuarioDni = dniStr,
                        monto = monto,
                        fechaPago = fechaPagoLong,
                        fechaVencimiento = fechaVencimiento,
                        tipoPago = tipoPago,
                        actividad = actividad
                    )

                    withContext(Dispatchers.Main) {
                        if (exitoPago) {
                            Toast.makeText(this@PagoActividadActivity, "Pago de actividad registrado", Toast.LENGTH_SHORT).show()
                            limpiarCampos()
                        } else {
                            Toast.makeText(this@PagoActividadActivity, "Error al guardar el pago", Toast.LENGTH_LONG).show()
                        }
                    }

                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@PagoActividadActivity, "Error: Usuario con DNI $dniStr no encontrado", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@PagoActividadActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            } finally {
                cursor?.close()
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
        calendario.add(Calendar.DAY_OF_YEAR, 1)
        return calendario.timeInMillis
    }
}