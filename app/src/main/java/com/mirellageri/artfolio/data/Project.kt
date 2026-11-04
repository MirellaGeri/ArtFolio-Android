package com.mirellageri.artfolio.data

data class Project(
    val id : Int,
    var title : String,
    var category : Category,
    var description : String,
    var imageUrl : String,
    var startDate : String,
    var endDate : String,
    var tools : String
){
    companion object{
        const val TABLE_NAME = "Project"
        const val COLUMN_ID = "id"
        const val COLUMN_TITLE = "title"
        const val COLUMN_CATEGORY = "category_id"
        const val COLUMN_DESCRIPTION = "description"
        const val COLUMN_IMAGE_URL = "image_url"
        const val COLUMN_START_DATE = "start_date"
        const val COLUMN_END_DATE = "end_date"
        const val COLUMN_TOOLS = "tools"
        const val SQL_CREATE_TABLE =
            "CREATE TABLE $TABLE_NAME ("+
            "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,"+
            "$COLUMN_TITLE TEXT,"+
            "$COLUMN_CATEGORY INTEGER,"+
            "$COLUMN_DESCRIPTION TEXT,"+
            "$COLUMN_IMAGE_URL TEXT,"+
            "$COLUMN_START_DATE TEXT,"+
            "$COLUMN_END_DATE TEXT,"+
            "$COLUMN_TOOLS TEXT,"+
            "FOREIGN KEY($COLUMN_CATEGORY) REFERENCES ${Category.TABLE_NAME}(${Category.COLUMN_ID}) ON DELETE CASCADE)"
        const val SQL_DROP_TABLE = "DROP TABLE IF EXISTS $TABLE_NAME"
    }
}
