package com.dynamis

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class AdminSQLiteOpenHelper(
    context: Context
) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "dynamis_db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_USERS = "users"
        private const val COL_USERNAME = "username"
        private const val COL_PASSWORD = "password"
        private const val COL_IS_ADMIN = "is_admin"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = "CREATE TABLE $TABLE_USERS (" +
                "$COL_USERNAME TEXT PRIMARY KEY, " +
                "$COL_PASSWORD TEXT, " +
                "$COL_IS_ADMIN INTEGER)"
        db.execSQL(createTable)

        insertAdminUser(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }

    private fun insertAdminUser(db: SQLiteDatabase) {
        val values = ContentValues().apply {
            put(COL_USERNAME, "admin")
            put(COL_PASSWORD, "1234")
            put(COL_IS_ADMIN, 1) // 1 para true
        }
        db.insert(TABLE_USERS, null, values)
    }


    fun checkUser(username: String, password: String): Boolean {
        val db = this.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_USERS WHERE $COL_USERNAME = ? AND $COL_PASSWORD = ?",
            arrayOf(username, password)
        )

        val userExists = cursor.count > 0
        cursor.close()
        db.close()
        return userExists
    }
}