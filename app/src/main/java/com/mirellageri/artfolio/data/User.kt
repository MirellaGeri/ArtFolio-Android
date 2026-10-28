package com.mirellageri.artfolio.data

data class User(
    val id: Int,
    var userName : String,
    var password : String
){
    companion object{
        const val TABLE_NAME = "User"
        const val COLUMN_ID = "id"
        const val COLUMN_USER_NAME = "userName"
        const val COLUMN_PASSWORD = "password"
        const val SQL_CREATE_TABLE =
            "CREATE TABLE $TABLE_NAME (" +
            "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,"+
            "$COLUMN_USER_NAME TEXT UNIQUE,"+
            "$COLUMN_PASSWORD TEXT )"
        const val SQL_DROP_TABLE = "DROP TABLE IF EXISTS $TABLE_NAME"
    }
}
