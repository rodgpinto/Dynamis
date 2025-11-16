package com.dynamis.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import com.dynamis.database.DatabaseContract.PagoEntry
import com.dynamis.database.DatabaseContract.UsuarioEntry
import java.util.Calendar

class DynamisDbHelper(contexto: Context) :
    SQLiteOpenHelper(contexto, DatabaseContract.DATABASE_NAME, null, 5 ) {

    private val SQL_CREAR_TABLA_USUARIOS =
        "CREATE TABLE ${UsuarioEntry.TABLE_NAME} (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT," +
                "${UsuarioEntry.COLUMN_NOMBRE} TEXT NOT NULL," +
                "${UsuarioEntry.COLUMN_APELLIDO} TEXT NOT NULL," +
                "${UsuarioEntry.COLUMN_TIPO_DOCUMENTO} TEXT NOT NULL," +
                "${UsuarioEntry.COLUMN_DNI} TEXT NOT NULL UNIQUE," +
                "${UsuarioEntry.COLUMN_FECHA_NACIMIENTO} TEXT NOT NULL," +
                "${UsuarioEntry.COLUMN_DIRECCION} TEXT," +
                "${UsuarioEntry.COLUMN_FOTO_URI} TEXT," +
                "${UsuarioEntry.COLUMN_TIENE_FICHA} INTEGER NOT NULL DEFAULT 0," +
                "${UsuarioEntry.COLUMN_TIENE_APTO} INTEGER NOT NULL DEFAULT 0," +
                "${UsuarioEntry.COLUMN_IS_SOCIO} INTEGER NOT NULL DEFAULT 0)"

    private val SQL_CREAR_TABLA_PAGOS =
        "CREATE TABLE ${PagoEntry.TABLE_NAME} (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT," +
                "${PagoEntry.COLUMN_USUARIO_DNI} TEXT NOT NULL," +
                "${PagoEntry.COLUMN_MONTO} REAL NOT NULL," +
                "${PagoEntry.COLUMN_FECHA_PAGO} INTEGER NOT NULL," +
                "${PagoEntry.COLUMN_FECHA_VENCIMIENTO} INTEGER NOT NULL," +
                "${PagoEntry.COLUMN_TIPO_PAGO} TEXT NOT NULL," +
                "${PagoEntry.COLUMN_ACTIVIDAD} TEXT," +
                "FOREIGN KEY(${PagoEntry.COLUMN_USUARIO_DNI}) REFERENCES ${UsuarioEntry.TABLE_NAME}(${UsuarioEntry.COLUMN_DNI}) ON DELETE CASCADE)"

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREAR_TABLA_USUARIOS)
        db.execSQL(SQL_CREAR_TABLA_PAGOS)

        precargarUsuarios(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS ${PagoEntry.TABLE_NAME}")
        db.execSQL("DROP TABLE IF EXISTS ${UsuarioEntry.TABLE_NAME}")
        onCreate(db)
    }

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    private fun precargarUsuarios(db: SQLiteDatabase) {

        val usuariosData = listOf(
            listOf("Homero", "Simpson", "12456789", "1956-05-12", "Avenida Siempre Viva 742", true),
            listOf("Marge", "Simpson", "13458921", "1956-03-19", "Avenida Siempre Viva 742", true),
            listOf("Bart", "Simpson", "28456932", "1980-04-01", "Avenida Siempre Viva 742", true),
            listOf("Lisa", "Simpson", "30451233", "1982-09-28", "Avenida Siempre Viva 742", true),
            listOf("Maggie", "Simpson", "50458978", "1988-01-14", "Avenida Siempre Viva 742", true),
            listOf("Ned", "Flanders", "15478922", "1959-02-11", "Avenida Siempre Viva 744", true),
            listOf("Rod", "Flanders", "32894512", "1983-08-09", "Avenida Siempre Viva 744", true),
            listOf("Todd", "Flanders", "33894513", "1985-07-21", "Avenida Siempre Viva 744", true),
            listOf("Montgomery", "Burns", "10457891", "1890-09-15", "Mansión Burns", true),
            listOf("Waylon", "Smithers", "20457893", "1959-04-15", "Mansión Burns", true),

            listOf("Krusty", "ElPayaso", "21457894", "1957-10-29", "Estudio de TV Canal 6", false),
            listOf("Moe", "Szyslak", "18457922", "1957-11-05", "Calle Principal 21", false),
            listOf("Barney", "Gumble", "17451289", "1958-06-23", "Calle Principal 15", false),
            listOf("Apu", "Nahasapeemapetilon", "19345781", "1962-04-19", "Kwik-E-Mart, Springfield", false),
            listOf("Manjula", "Nahasapeemapetilon", "22345782", "1964-09-02", "Kwik-E-Mart, Springfield", false),
            listOf("Milhouse", "Van Houten", "31457822", "1982-07-02", "Calle Pino 12", false),
            listOf("Kirk", "Van Houten", "15457821", "1958-03-12", "Calle Pino 12", false),
            listOf("Luann", "Van Houten", "16457820", "1960-05-30", "Calle Pino 12", false),
            listOf("Seymour", "Skinner", "14457892", "1954-10-18", "Escuela Primaria de Springfield", false),
            listOf("Edna", "Krabappel", "15457890", "1955-01-24", "Calle Cerezo 9", false)
        )

        for (userData in usuariosData) {
            val values = ContentValues().apply {
                put(UsuarioEntry.COLUMN_NOMBRE, userData[0] as String)
                put(UsuarioEntry.COLUMN_APELLIDO, userData[1] as String)
                put(UsuarioEntry.COLUMN_DNI, userData[2] as String)
                put(UsuarioEntry.COLUMN_FECHA_NACIMIENTO, userData[3] as String)
                put(UsuarioEntry.COLUMN_DIRECCION, userData[4] as String)
                put(UsuarioEntry.COLUMN_TIPO_DOCUMENTO, "DNI")
                put(UsuarioEntry.COLUMN_IS_SOCIO, if (userData[5] as Boolean) 1 else 0)
                put(UsuarioEntry.COLUMN_TIENE_FICHA, 1)
                put(UsuarioEntry.COLUMN_TIENE_APTO, 1)
            }
            db.insert(UsuarioEntry.TABLE_NAME, null, values)
        }


        val hoy = Calendar.getInstance()
        val timestampHoy = hoy.timeInMillis

        val dnisVencidos = setOf("10457891", "15478922") // Montgomery Burns y Ned Flanders

        val timestampVencido = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -15) // Venció hace 15 días
            set(Calendar.HOUR_OF_DAY, 23); set(Calendar.MINUTE, 59); set(Calendar.SECOND, 59); set(Calendar.MILLISECOND, 999)
        }.timeInMillis

        val timestampActivo = Calendar.getInstance().apply {
            add(Calendar.MONTH, 1); add(Calendar.DAY_OF_YEAR, 15) // Vence en 1 mes y 15 días
            set(Calendar.HOUR_OF_DAY, 23); set(Calendar.MINUTE, 59); set(Calendar.SECOND, 59); set(Calendar.MILLISECOND, 999)
        }.timeInMillis

        val allSociosDnis = usuariosData.filter { it[5] as Boolean }.map { it[2] as String }

        for (dni in allSociosDnis) {
            val esVencido = dni in dnisVencidos
            val fechaVencimiento = if (esVencido) timestampVencido else timestampActivo
            val monto = if (esVencido) 10000.0 else 5000.0

            db.insert(PagoEntry.TABLE_NAME, null, ContentValues().apply {
                put(PagoEntry.COLUMN_USUARIO_DNI, dni)
                put(PagoEntry.COLUMN_MONTO, monto)
                put(PagoEntry.COLUMN_FECHA_PAGO, timestampHoy)
                put(PagoEntry.COLUMN_FECHA_VENCIMIENTO, fechaVencimiento)
                put(PagoEntry.COLUMN_TIPO_PAGO, "Mensual")
            })
        }
    }


    fun insertarUsuario(
        nombre: String, apellido: String, tipoDoc: String, dni: String,
        fechaNac: String, direccion: String?, fotoUri: String?,
        tieneFicha: Boolean, tieneApto: Boolean, isSocio: Boolean
    ): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(UsuarioEntry.COLUMN_NOMBRE, nombre)
            put(UsuarioEntry.COLUMN_APELLIDO, apellido)
            put(UsuarioEntry.COLUMN_TIPO_DOCUMENTO, tipoDoc)
            put(UsuarioEntry.COLUMN_DNI, dni)
            put(UsuarioEntry.COLUMN_FECHA_NACIMIENTO, fechaNac)
            put(UsuarioEntry.COLUMN_DIRECCION, direccion)
            put(UsuarioEntry.COLUMN_FOTO_URI, fotoUri)
            put(UsuarioEntry.COLUMN_TIENE_FICHA, if (tieneFicha) 1 else 0)
            put(UsuarioEntry.COLUMN_TIENE_APTO, if (tieneApto) 1 else 0)
            put(UsuarioEntry.COLUMN_IS_SOCIO, if (isSocio) 1 else 0)
        }
        val resultado = db.insert(UsuarioEntry.TABLE_NAME, null, values)
        return resultado != -1L
    }

    fun insertarPago(
        usuarioDni: String, monto: Double, fechaPago: Long,
        fechaVencimiento: Long, tipoPago: String, actividad: String?
    ): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(PagoEntry.COLUMN_USUARIO_DNI, usuarioDni)
            put(PagoEntry.COLUMN_MONTO, monto)
            put(PagoEntry.COLUMN_FECHA_PAGO, fechaPago)
            put(PagoEntry.COLUMN_FECHA_VENCIMIENTO, fechaVencimiento)
            put(PagoEntry.COLUMN_TIPO_PAGO, tipoPago)
            put(PagoEntry.COLUMN_ACTIVIDAD, actividad)
        }
        val resultado = db.insert(PagoEntry.TABLE_NAME, null, values)
        return resultado != -1L
    }


    fun getListarSociosVENCIDOS(timestampInicioHoy: Long): Cursor {
        val db = this.readableDatabase

        val query = """
            SELECT 
                T1.*, 
                IFNULL(MAX(T2.${PagoEntry.COLUMN_FECHA_VENCIMIENTO}), 0) as ultima_fecha_vencimiento
            FROM ${UsuarioEntry.TABLE_NAME} AS T1
            LEFT JOIN ${PagoEntry.TABLE_NAME} AS T2 
                ON T1.${UsuarioEntry.COLUMN_DNI} = T2.${PagoEntry.COLUMN_USUARIO_DNI}
            WHERE T1.${UsuarioEntry.COLUMN_IS_SOCIO} = 1
            GROUP BY T1.${UsuarioEntry.COLUMN_DNI}
            HAVING ultima_fecha_vencimiento < $timestampInicioHoy
            ORDER BY T1.${UsuarioEntry.COLUMN_APELLIDO} ASC
        """

        return db.rawQuery(query, null)
    }


    fun consultarUsuario(dni: String): Cursor {
        val db = this.readableDatabase
        val selection = "${UsuarioEntry.COLUMN_DNI} = ?"
        val selectionArgs = arrayOf(dni)
        return db.query(
            UsuarioEntry.TABLE_NAME, null, selection, selectionArgs, null, null, null
        )
    }


    fun actualizarUsuario(
        dni: String?,
        nombre: String?,
        apellido: String?,
        tipoDoc: String?,
        fechaNac: String?,
        direccion: String?,
        fotoUri: String?,
        tieneFicha: Boolean?,
        tieneApto: Boolean?,
        isSocio: Boolean?
    ): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(UsuarioEntry.COLUMN_NOMBRE, nombre)
            put(UsuarioEntry.COLUMN_APELLIDO, apellido)
            put(UsuarioEntry.COLUMN_TIPO_DOCUMENTO, tipoDoc)
            put(UsuarioEntry.COLUMN_DNI, dni)
            put(UsuarioEntry.COLUMN_FECHA_NACIMIENTO, fechaNac)
            put(UsuarioEntry.COLUMN_DIRECCION, direccion)
            put(UsuarioEntry.COLUMN_FOTO_URI, fotoUri)
            put(UsuarioEntry.COLUMN_TIENE_FICHA, if (tieneFicha == true) 1 else 0)
            put(UsuarioEntry.COLUMN_TIENE_APTO, if (tieneApto == true) 1 else 0)
            put(UsuarioEntry.COLUMN_IS_SOCIO, if (isSocio == true) 1 else 0)
        }
        val selection = "${UsuarioEntry.COLUMN_DNI} = ?"
        val selectionArgs = arrayOf(dni)
        val count = db.update(
            UsuarioEntry.TABLE_NAME, values, selection, selectionArgs
        )
        return count > 0
    }


    fun borrarUsuario(dni: String): Boolean {
        val db = this.writableDatabase
        val selection = "${UsuarioEntry.COLUMN_DNI} = ?"
        val selectionArgs = arrayOf(dni)
        val count = db.delete(
            UsuarioEntry.TABLE_NAME, selection, selectionArgs
        )
        return count > 0
    }

    fun getUsuarios(isSocio: Boolean): Cursor {
        val db = this.readableDatabase
        val selection = "${UsuarioEntry.COLUMN_IS_SOCIO} = ?"
        val selectionArgs = arrayOf(if (isSocio) "1" else "0")
        val orderBy = "${UsuarioEntry.COLUMN_APELLIDO} ASC"
        return db.query(
            UsuarioEntry.TABLE_NAME, null, selection, selectionArgs, null, null, orderBy
        )
    }


    fun getListarTODOSSocios(): Cursor {
        val db = this.readableDatabase

        val sqlQuery = """
            SELECT 
                T1.*, 
                IFNULL(MAX(T2.${PagoEntry.COLUMN_FECHA_VENCIMIENTO}), 0) as ultima_fecha_vencimiento
            FROM ${UsuarioEntry.TABLE_NAME} AS T1
            LEFT JOIN ${PagoEntry.TABLE_NAME} AS T2 
                ON T1.${UsuarioEntry.COLUMN_DNI} = T2.${PagoEntry.COLUMN_USUARIO_DNI}
            WHERE T1.${UsuarioEntry.COLUMN_IS_SOCIO} = 1
            GROUP BY T1.${UsuarioEntry.COLUMN_DNI}
            ORDER BY T1.${UsuarioEntry.COLUMN_APELLIDO} ASC
        """

        return db.rawQuery(sqlQuery, null)
    }
}