package com.mirellageri.artfolio.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.mirellageri.artfolio.utils.DatabaseManager

class UserDAO(val context: Context) {
    lateinit var db: SQLiteDatabase
    fun openDB(){
        db = DatabaseManager(context).writableDatabase
    }
    fun closeDB(){
        db.close()
    }
    fun checkUserCredential(userName : String,password :String): Boolean{
        openDB()
        var isValid = false
        val selection = "${User.COLUMN_USER_NAME} = ? AND ${User.COLUMN_PASSWORD} = ?"
        val selectionArgs = arrayOf(userName, password)
        try {
            val cursor = db.query(
                User.TABLE_NAME,   // The table to query
                null,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
            )
            if (cursor.moveToNext()) {
                isValid = true
            }
            cursor.close()
        }catch (e: Exception){
            Log.e("DATABASE", e.localizedMessage,e)
        }finally {
            closeDB()
        }
        return isValid
    }
}