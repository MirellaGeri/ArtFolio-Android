package com.mirellageri.artfolio.data

data class Category(
    val id: Int,
    var name : String
){
    companion object{
        const val TABLE_NAME = "Category"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"

        const val SQL_CREATE_TABLE =
            "CREATE TABLE $TABLE_NAME (" +
                    "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "$COLUMN_NAME TEXT)"

        const val SQL_DROP_TABLE = "DROP TABLE IF EXISTS $TABLE_NAME"
    }
}
