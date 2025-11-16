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
    SQLiteOpenHelper(contexto, DatabaseContract.DATABASE_NAME, null, DatabaseContract.DATABASE_VERSION) {

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