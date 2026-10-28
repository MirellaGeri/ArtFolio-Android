package com.mirellageri.artfolio.utils

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.mirellageri.artfolio.data.Project
import com.mirellageri.artfolio.data.User

class DatabaseManager(context : Context): SQLiteOpenHelper(context,DATABASE_NAME,null,DATA_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(Project.SQL_CREATE_TABLE)
        db.execSQL(User.SQL_CREATE_TABLE)
        db.execSQL("INSERT INTO ${User.TABLE_NAME} (${User.COLUMN_USER_NAME}, ${User.COLUMN_PASSWORD}) VALUES ('admin', '1234')")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onDestroy(db)
        onCreate(db)
    }
    private fun onDestroy(db: SQLiteDatabase){
        db.execSQL(Project.SQL_DROP_TABLE)
        db.execSQL(User.SQL_DROP_TABLE)
    }

    companion object{
        const val DATABASE_NAME = "portfolio.db"
        const val DATA_VERSION = 1
    }
}