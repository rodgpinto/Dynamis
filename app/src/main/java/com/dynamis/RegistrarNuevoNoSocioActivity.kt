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

class RegistrarNuevoNoSocioActivity : AppCompatActivity() {

    private lateinit var dbHelper: DynamisDbHelper
    private lateinit var nombre: EditText
    private lateinit var apellido: EditText
    private lateinit var spinnerDocumento: Spinner
    private lateinit var dni: EditText
    private lateinit var fechaNac: EditText
    private lateinit var direccion: EditText
    private lateinit var chkFicha: CheckBox
    private lateinit var chkApto: CheckBox
    private lateinit var spinnerActividad: Spinner
    private lateinit var pagoCuota: EditText
    private lateinit var spinnerTipoPago: Spinner
    private lateinit var fechaPago: EditText
    private lateinit var btnIngresar: Button
    private lateinit var btnLimpiar: Button

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registrar_nuevo_no_socio)

        dbHelper = DynamisDbHelper(this)

        nombre = findViewById(R.id.nombre)
        apellido = findViewById(R.id.apellido)
        spinnerDocumento = findViewById(R.id.spinnerDocumento)
        dni = findViewById(R.id.editTextNumber)
        fechaNac = findViewById(R.id.txtFechaNacimiento)
        direccion = findViewById(R.id.direccion)
        chkFicha = findViewById(R.id.chkFichaInscripcion)
        chkApto = findViewById(R.id.chkAptoMedico)
        spinnerActividad = findViewById(R.id.spinnerActividad)
        pagoCuota = findViewById(R.id.txtPagoCuota)
        spinnerTipoPago = findViewById(R.id.spinnerTipoPago)
        fechaPago = findViewById(R.id.txtFechaPago)
        btnIngresar = findViewById(R.id.btnIngresar)
        btnLimpiar = findViewById(R.id.btnLimpiar)

        val tituloNavbar = findViewById<TextView>(R.id.navbar_title)
        tituloNavbar.text = "Registrar No Socio"

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
            intentarGuardarNoSocio()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.registrar_nuevo_no_socio)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupSpinners() {
        val opcionesDocumento = arrayOf("DNI", "Pasaporte", "LC")
        val adapterDoc = ArrayAdapter(this, android.R.layout.simple_spinner_item, opcionesDocumento)
        adapterDoc.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDocumento.adapter = adapterDoc

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
        builder.setTitle("Ayuda: Registrar No Socio")
        builder.setMessage(
            "Completa todos los campos para registrar a una persona que usará las instalaciones sin ser socio (ej. pase diario)."
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
        nombre.text.clear()
        apellido.text.clear()
        spinnerDocumento.setSelection(0)
        dni.text.clear()
        fechaNac.text.clear()
        direccion.text.clear()
        chkFicha.isChecked = false
        chkApto.isChecked = false
        spinnerActividad.setSelection(0)
        pagoCuota.text.clear()
        spinnerTipoPago.setSelection(0)
        fechaPago.text.clear()
        nombre.requestFocus()
    }

    private fun intentarGuardarNoSocio() {
        val nombreStr = nombre.text.toString()
        val apellidoStr = apellido.text.toString()
        val tipoDoc = spinnerDocumento.selectedItem.toString()
        val dniStr = dni.text.toString()
        val fechaNacStr = fechaNac.text.toString()
        val direccionStr = direccion.text.toString()
        val tieneFicha = chkFicha.isChecked
        val tieneApto = chkApto.isChecked
        val actividad = spinnerActividad.selectedItem.toString()
        val montoStr = pagoCuota.text.toString()
        val tipoPago = spinnerTipoPago.selectedItem.toString()
        val fechaPagoStr = fechaPago.text.toString()

        if (nombreStr.isEmpty() || apellidoStr.isEmpty() || dniStr.isEmpty() || fechaNacStr.isEmpty() || montoStr.isEmpty() || fechaPagoStr.isEmpty()) {
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
            Toast.makeText(this, "Formato de fecha de pago incorrecto (DD/MM/AAAA)", Toast.LENGTH_SHORT).show()
            return
        }

        val fechaVencimiento = calcularVencimiento(fechaPagoLong)

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val exitoUsuario = dbHelper.insertarUsuario(
                    nombre = nombreStr,
                    apellido = apellidoStr,
                    tipoDoc = tipoDoc,
                    dni = dniStr,
                    fechaNac = fechaNacStr,
                    direccion = direccionStr,
                    fotoUri = null,
                    tieneFicha = tieneFicha,
                    tieneApto = tieneApto,
                    isSocio = false
                )

                if (!exitoUsuario) {
                    throw SQLiteConstraintException("Error al guardar usuario, DNI duplicado.")
                }

                val exitoPago = dbHelper.insertarPago(
                    usuarioDni = dniStr,
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
                    Toast.makeText(this@RegistrarNuevoNoSocioActivity, "No Socio guardado", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this@RegistrarNuevoNoSocioActivity, CarnetActivity::class.java)
                    intent.putExtra("NOMBRE", nombreStr)
                    intent.putExtra("APELLIDO", apellidoStr)
                    intent.putExtra("DNI", dniStr)
                    intent.putExtra("FECHA_NAC", fechaNacStr)
                    intent.putExtra("DIRECCION", direccionStr)
                    startActivity(intent)

                    finish()
                }

            } catch (e: SQLiteConstraintException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@RegistrarNuevoNoSocioActivity, "Error: El DNI ya existe", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@RegistrarNuevoNoSocioActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
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
        calendario.add(Calendar.DAY_OF_YEAR, 1)
        return calendario.timeInMillis
    }
}