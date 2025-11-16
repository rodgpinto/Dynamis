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
import com.dynamis.database.DatabaseContract.UsuarioEntry
import com.dynamis.database.DynamisDbHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ModificarEliminarRegistroNoSocioActivity : AppCompatActivity() {

    private lateinit var dbHelper: DynamisDbHelper
    private lateinit var nombre: EditText
    private lateinit var apellido: EditText
    private lateinit var spinnerDocumento: Spinner
    private lateinit var dni: EditText
    private lateinit var fechaNac: EditText
    private lateinit var direccion: EditText
    private lateinit var btnVerCarnet: Button
    private lateinit var btnGuardar: Button
    private lateinit var btnLimpiar: Button
    private lateinit var btnEliminar: Button

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_modificar_eliminar_registro_no_socio)

        dbHelper = DynamisDbHelper(this)

        nombre = findViewById(R.id.nombre)
        apellido = findViewById(R.id.apellido)
        spinnerDocumento = findViewById(R.id.spinnerDocumento)
        dni = findViewById(R.id.editTextNumber)
        fechaNac = findViewById(R.id.txtFechaNacimiento)
        direccion = findViewById(R.id.direccion)
        btnVerCarnet = findViewById(R.id.btnVerCarnet)
        btnGuardar = findViewById(R.id.btnGuardar)
        btnLimpiar = findViewById(R.id.btnLimpiar)
        btnEliminar = findViewById(R.id.btnEliminar)

        val tituloNavbar = findViewById<TextView>(R.id.navbar_title)
        tituloNavbar.text = "Modificar/Eliminar No Socio"

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


        val opcionesDocumento = arrayOf("DNI", "Pasaporte", "LC")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, opcionesDocumento)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDocumento.adapter = adapter

        btnLimpiar.setOnClickListener {
            limpiarCampos()
        }
        btnVerCarnet.setOnClickListener {
            verCarnetUsuario()
        }
        btnGuardar.setOnClickListener {
            guardarCambiosUsuario()
        }
        btnEliminar.setOnClickListener {
            mostrarDialogoDeConfirmacion()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.modificar_eliminar_registro_no_socio)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun mostrarDialogoDeAyuda() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Ayuda: Modificar/Eliminar No Socio")
        builder.setMessage(
            "En esta pantalla podrás:\n\n" +
                    "• **Modificar** los datos de un no-socio existente.\n" +
                    "• **Eliminar:** todo registro de un no-socio incluyendo sus pagos."
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
        dni.requestFocus()
    }


    private fun verCarnetUsuario() {
        val dniUsuario = dni.text.toString()

        if (dniUsuario.isEmpty()) {
            Toast.makeText(this, "Ingresa un DNI para ver el carnet", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch(Dispatchers.IO) {
            var cursor: Cursor? = null
            try {
                cursor = dbHelper.consultarUsuario(dniUsuario)

                if (cursor != null && cursor.moveToFirst()) {
                    val nombreUsuario = cursor.getString(cursor.getColumnIndexOrThrow(UsuarioEntry.COLUMN_NOMBRE))
                    val apellidoUsuario = cursor.getString(cursor.getColumnIndexOrThrow(UsuarioEntry.COLUMN_APELLIDO))
                    val fechaNacUsuario = cursor.getString(cursor.getColumnIndexOrThrow(UsuarioEntry.COLUMN_FECHA_NACIMIENTO))
                    val direccionUsuario = cursor.getString(cursor.getColumnIndexOrThrow(UsuarioEntry.COLUMN_DIRECCION))

                    withContext(Dispatchers.Main) {
                        val intent = Intent(this@ModificarEliminarRegistroNoSocioActivity, CarnetActivity::class.java)
                        intent.putExtra("NOMBRE", nombreUsuario)
                        intent.putExtra("APELLIDO", apellidoUsuario)
                        intent.putExtra("DNI", dniUsuario)
                        intent.putExtra("FECHA_NAC", fechaNacUsuario)
                        intent.putExtra("DIRECCION", direccionUsuario)
                        startActivity(intent)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@ModificarEliminarRegistroNoSocioActivity, "Usuario no encontrado", Toast.LENGTH_SHORT).show()
                    }
                }
            } finally {
                cursor?.close()
            }
        }
    }


    private fun guardarCambiosUsuario() {
        val dniUsuario = dni.text.toString()
        val nuevoNombre = nombre.text.toString()
        val nuevoApellido = apellido.text.toString()
        val nuevoTipoDoc = spinnerDocumento.selectedItem.toString()
        val nuevaFechaNac = fechaNac.text.toString()
        val nuevaDireccion = direccion.text.toString()

        if (dniUsuario.isEmpty()) {
            Toast.makeText(this, "El campo DNI es obligatorio para guardar cambios", Toast.LENGTH_SHORT).show()
            dni.requestFocus()
            return
        }

        if (nuevoNombre.isEmpty() && nuevoApellido.isEmpty() && nuevaFechaNac.isEmpty() && nuevaDireccion.isEmpty()) {
            Toast.makeText(this, "Ingrese al menos un dato para modificar", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch(Dispatchers.IO) {
            var cursor: Cursor? = null
            try {
                cursor = dbHelper.consultarUsuario(dniUsuario)

                if (cursor != null && cursor.moveToFirst()) {

                    val nombreAntiguo = cursor.getString(cursor.getColumnIndexOrThrow(UsuarioEntry.COLUMN_NOMBRE))
                    val apellidoAntiguo = cursor.getString(cursor.getColumnIndexOrThrow(UsuarioEntry.COLUMN_APELLIDO))
                    val fechaNacAntigua = cursor.getString(cursor.getColumnIndexOrThrow(UsuarioEntry.COLUMN_FECHA_NACIMIENTO))
                    val direccionAntigua = cursor.getString(cursor.getColumnIndexOrThrow(UsuarioEntry.COLUMN_DIRECCION))

                    val fotoUriAntigua = cursor.getString(cursor.getColumnIndexOrThrow(UsuarioEntry.COLUMN_FOTO_URI))
                    val tieneFichaAntiguo = cursor.getInt(cursor.getColumnIndexOrThrow(UsuarioEntry.COLUMN_TIENE_FICHA)) == 1
                    val tieneAptoAntiguo = cursor.getInt(cursor.getColumnIndexOrThrow(UsuarioEntry.COLUMN_TIENE_APTO)) == 1
                    val isSocioAntiguo = cursor.getInt(cursor.getColumnIndexOrThrow(UsuarioEntry.COLUMN_IS_SOCIO)) == 1

                    cursor.close()

                    val nombreFinal = if (nuevoNombre.isNotEmpty()) nuevoNombre else nombreAntiguo
                    val apellidoFinal = if (nuevoApellido.isNotEmpty()) nuevoApellido else apellidoAntiguo
                    val fechaNacFinal = if (nuevaFechaNac.isNotEmpty()) nuevaFechaNac else fechaNacAntigua
                    val direccionFinal = if (nuevaDireccion.isNotEmpty()) nuevaDireccion else direccionAntigua

                    val exito = dbHelper.actualizarUsuario(
                        dni = dniUsuario,
                        nombre = nombreFinal,
                        apellido = apellidoFinal,
                        tipoDoc = nuevoTipoDoc,
                        fechaNac = fechaNacFinal,
                        direccion = direccionFinal,
                        fotoUri = fotoUriAntigua,
                        tieneFicha = tieneFichaAntiguo,
                        tieneApto = tieneAptoAntiguo,
                        isSocio = isSocioAntiguo
                    )

                    withContext(Dispatchers.Main) {
                        if (exito) {
                            Toast.makeText(this@ModificarEliminarRegistroNoSocioActivity, "Usuario actualizado", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this@ModificarEliminarRegistroNoSocioActivity, "Error al actualizar", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@ModificarEliminarRegistroNoSocioActivity, "No se encontró el DNI para actualizar", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch(e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ModificarEliminarRegistroNoSocioActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            } finally {
                cursor?.close()
            }
        }
    }


    private fun mostrarDialogoDeConfirmacion() {
        val dniUsuario = dni.text.toString()
        if (dniUsuario.isEmpty()) {
            Toast.makeText(this, "Ingrese un DNI para eliminar", Toast.LENGTH_SHORT).show()
            return
        }

        AlertDialog.Builder(this)
            .setTitle("Confirmar Eliminación")
            .setMessage("¿Estás seguro de que deseas eliminar al usuario con DNI $dniUsuario? Esta acción no se puede deshacer y borrará todos sus pagos.")
            .setIcon(R.drawable.ic_warning)
            .setPositiveButton("Eliminar") { dialog, _ ->
                eliminarUsuario(dniUsuario)
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }


    private fun eliminarUsuario(dniUsuario: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val exito = dbHelper.borrarUsuario(dniUsuario)

                withContext(Dispatchers.Main) {
                    if (exito) {
                        Toast.makeText(this@ModificarEliminarRegistroNoSocioActivity, "Usuario eliminado", Toast.LENGTH_SHORT).show()
                        limpiarCampos()
                    } else {
                        Toast.makeText(this@ModificarEliminarRegistroNoSocioActivity, "No se encontró el usuario", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ModificarEliminarRegistroNoSocioActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}