package com.mirellageri.artfolio.data

data class Project(
    val id : Int,
    var title : String,
    var category : String,
    var description : String,
    var imageUrl : String
){
    companion object{
        const val TABLE_NAME = "Project"
        const val COLUMN_ID = "id"
        const val COLUMN_TITLE = "title"
        const val COLUMN_CATEGORY = "category"
        const val COLUMN_DESCRIPTION = "description"
        const val COLUMN_IMAGE_URL = "imageUrl"
        const val SQL_CREATE_TABLE =
            "CREATE TABLE $TABLE_NAME ("+
            "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,"+
            "$COLUMN_TITLE TEXT,"+
            "$COLUMN_CATEGORY TEXT,"+
            "$COLUMN_DESCRIPTION TEXT,"+
            "$COLUMN_IMAGE_URL TEXT)"
        const val SQL_DROP_TABLE = "DROP TABLE IF EXISTS $TABLE_NAME"
    }
}
