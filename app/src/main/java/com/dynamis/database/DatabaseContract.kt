package com.dynamis.database

import android.provider.BaseColumns


object DatabaseContract {

    const val DATABASE_NAME = "dynamis.db"
    const val DATABASE_VERSION = 2

    object UsuarioEntry : BaseColumns {
        const val TABLE_NAME = "usuarios"


        const val COLUMN_NOMBRE = "nombre"
        const val COLUMN_APELLIDO = "apellido"
        const val COLUMN_TIPO_DOCUMENTO = "tipo_documento"
        const val COLUMN_DNI = "dni"
        const val COLUMN_FECHA_NACIMIENTO = "fecha_nacimiento"
        const val COLUMN_DIRECCION = "direccion"
        const val COLUMN_FOTO_URI = "foto_uri"
        const val COLUMN_TIENE_FICHA = "tiene_ficha"
        const val COLUMN_TIENE_APTO = "tiene_apto"
        const val COLUMN_IS_SOCIO = "is_socio"
    }

    object PagoEntry : BaseColumns {
        const val TABLE_NAME = "pagos"

        const val COLUMN_USUARIO_DNI = "usuario_dni"

        const val COLUMN_MONTO = "monto"
        const val COLUMN_FECHA_PAGO = "fecha_pago"
        const val COLUMN_FECHA_VENCIMIENTO = "fecha_vencimiento"
        const val COLUMN_TIPO_PAGO = "tipo_pago"
        const val COLUMN_ACTIVIDAD = "actividad"
    }
}