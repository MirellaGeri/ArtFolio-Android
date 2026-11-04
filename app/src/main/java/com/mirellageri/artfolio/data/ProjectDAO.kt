package com.mirellageri.artfolio.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.mirellageri.artfolio.utils.DatabaseManager

class ProjectDAO(val context : Context) {
    val categoryDAO = CategoryDAO(context)
    lateinit var db : SQLiteDatabase
    fun openDB(){
        db = DatabaseManager(context).writableDatabase
    }
    fun closeDB(){
        db.close()
    }
    fun save(project : Project){
        if (project.id == -1){
            insert(project)
        }else{
            update(project)
        }
    }
    private fun getContentValue(project: Project): ContentValues{
        val values = ContentValues()
        values.put(Project.COLUMN_TITLE,project.title)
        values.put(Project.COLUMN_CATEGORY,project.category.id)
        values.put(Project.COLUMN_DESCRIPTION,project.description)
        values.put(Project.COLUMN_IMAGE_URL,project.imageUrl)
        return values
    }
    fun insert(project : Project){
        openDB()
        val values = getContentValue(project)
        try {
            val newRowId = db.insertOrThrow(Project.TABLE_NAME,null,values)
            Log.i("DATABASE", "New row inserted in table ${Project.TABLE_NAME} with id: $newRowId")
        }catch (e : Exception){
            Log.e("DATABASE",e.localizedMessage,e)
        }finally {
            closeDB()
        }
    }
    fun update(project : Project){
        openDB()
        val values = getContentValue(project)
        try {
            val updatedRows = db.update(Project.TABLE_NAME,values,"${Project.COLUMN_ID} = ${project.id}",null)
            Log.i("DATABASE", "$updatedRows rows updated in table ${Project.TABLE_NAME}")
        }catch (e : Exception){
            Log.e("DATABASE",e.localizedMessage,e)
        }finally {
            closeDB()
        }
    }
    private fun cursorToEntity(cursor : Cursor): Project{
        val id = cursor.getInt(cursor.getColumnIndexOrThrow(Project.COLUMN_ID))
        val title = cursor.getString(cursor.getColumnIndexOrThrow(Project.COLUMN_TITLE))
        val categoryId = cursor.getInt(cursor.getColumnIndexOrThrow(Project.COLUMN_CATEGORY))
        val description = cursor.getString(cursor.getColumnIndexOrThrow(Project.COLUMN_DESCRIPTION))
        val imageUrl = cursor.getString(cursor.getColumnIndexOrThrow(Project.COLUMN_IMAGE_URL))

        val startDateIdx = cursor.getColumnIndex(Project.COLUMN_START_DATE)
        val startDate = if (startDateIdx != -1 && !cursor.isNull(startDateIdx)) {
            cursor.getString(startDateIdx)
        } else ""

        val endDateIdx = cursor.getColumnIndex(Project.COLUMN_END_DATE)
        val endDate = if (endDateIdx != -1 && !cursor.isNull(endDateIdx)) {
            cursor.getString(endDateIdx)
        } else ""

        val materialsIdx = cursor.getColumnIndex(Project.COLUMN_TOOLS)
        val tools = if (materialsIdx != -1 && !cursor.isNull(materialsIdx)) {
            cursor.getString(materialsIdx)
        } else ""

        //val startDate = cursor.getString(cursor.getColumnIndexOrThrow(Project.COLUMN_START_DATE))
        //val endDate = cursor.getString(cursor.getColumnIndexOrThrow(Project.COLUMN_END_DATE))
        //val tools = cursor.getString(cursor.getColumnIndexOrThrow(Project.COLUMN_TOOLS))
        val category = categoryDAO.find(categoryId)!!
        return Project(id,title,category,description,imageUrl,startDate,endDate,tools)
    }
    fun findAll(): List<Project> {
        return findAllBy(null)
    }

    fun findAllByCategory(category: Category): List<Project> {
        return findAllBy("${Project.COLUMN_CATEGORY} = ${category.id}")
    }
    fun findAllBy(where:String?) : List<Project>{
        openDB()
        val projectList = mutableListOf<Project>()
        try {
            val cursor = db.query(
                Project.TABLE_NAME,   // The table to query
                null,             // The array of columns to return (pass null to get all)
                where,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                "${Project.COLUMN_ID} DESC"               // The sort order
            )
            while (cursor.moveToNext()){
                val project = cursorToEntity(cursor)
                projectList.add(project)
            }
        }catch (e : Exception){
            Log.e("DATABASE", e.localizedMessage,e)
        }finally {
            closeDB()
        }
        return projectList
    }
    fun findById(id :Int) : Project?{
        openDB()
        var project: Project? = null
        try {
            val cursor = db.query(
                Project.TABLE_NAME,   // The table to query
                null,             // The array of columns to return (pass null to get all)
                "${Project.COLUMN_ID} = $id",              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
            )
            if (cursor.moveToNext()) {
                project = cursorToEntity(cursor)
            }
        }catch (e: Exception){
            Log.e("DATABASE", e.localizedMessage,e)
        }finally {
            closeDB()
        }
        return project
    }
}