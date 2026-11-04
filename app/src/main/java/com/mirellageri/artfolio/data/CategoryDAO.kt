package com.mirellageri.artfolio.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.mirellageri.artfolio.utils.DatabaseManager

class CategoryDAO (val context: Context){
    private lateinit var db: SQLiteDatabase
    private fun open() {
        db = DatabaseManager(context).writableDatabase
    }
    private fun close() {
        db.close()
    }
    private fun getContentValues(category: Category): ContentValues {
        val values = ContentValues()
        values.put(Category.COLUMN_NAME, category.name)
        return values
    }
    private fun readFromCursor(cursor: Cursor): Category {
        val id = cursor.getInt(cursor.getColumnIndexOrThrow(Category.COLUMN_ID))
        val name = cursor.getString(cursor.getColumnIndexOrThrow(Category.COLUMN_NAME))
        return Category(id, name)
    }
    fun insert(category: Category) {
        // Create a new map of values, where column names are the keys
        val values = getContentValues(category)
        try {
            open()
            // Insert the new row, returning the primary key value of the new row
            val newRowId = db.insert(Category.TABLE_NAME, null, values)
            Log.i("DATABASE", "New row inserted in table ${Category.TABLE_NAME} with id: $newRowId")
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            close()
        }
    }
    fun update(category: Category) {
        // Create a new map of values, where column names are the keys
        val values = getContentValues(category)
        try {
            open()
            // Insert the new row, returning the primary key value of the new row
            val updatedRows = db.update(Category.TABLE_NAME, values, "${Category.COLUMN_ID} = ${category.id}", null)
            Log.i("DATABASE", "$updatedRows rows updated in table ${Category.TABLE_NAME}")
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            close()
        }
    }
    fun delete(id: Int) {
        try {
            open()
            // Insert the new row, returning the primary key value of the new row
            val deletedRows = db.delete(Category.TABLE_NAME, "${Category.COLUMN_ID} = $id", null)
            Log.i("DATABASE", "$deletedRows rows deleted in table ${Category.TABLE_NAME}")
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            close()
        }
    }
    fun findIdxName(name: String): Category? {
        var categoryFound: Category? = null  // Valor por defecto si no existe
        val projection = arrayOf(Category.COLUMN_ID, Category.COLUMN_NAME)
        // Filter results WHERE "title" = 'My Title'
        val selection = "${Category.COLUMN_NAME} = ?"
        val selectionArgs = arrayOf(name)
        // How you want the results sorted in the resulting Cursor
        val sortOrder = null
        try {
            open()
            val cursor = db.query(
                Category.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
            )
            cursor.use {c ->
                if (c.moveToFirst()) {
                    val id = c.getInt(c.getColumnIndexOrThrow(Category.COLUMN_ID))
                    val nameFromDb = c.getString(c.getColumnIndexOrThrow(Category.COLUMN_NAME))
                    // Construimos la clase Category completa
                    categoryFound = Category(id, nameFromDb)
                }
            }
        }catch (e : Exception){
            e.printStackTrace()
        }finally {
            close()
        }
        return categoryFound
    }
    fun find(id: Int) : Category? {
        var category: Category? = null
        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        val projection = arrayOf(Category.COLUMN_ID, Category.COLUMN_NAME)
        // Filter results WHERE "title" = 'My Title'
        val selection = "${Category.COLUMN_ID} = $id"
        val selectionArgs = null
        // How you want the results sorted in the resulting Cursor
        val sortOrder = null
        try {
            open()
            val cursor = db.query(
                Category.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
            )
            // Read the cursor data
            if (cursor.moveToNext()) {
                category = readFromCursor(cursor)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            close()
        }
        return category
    }
    fun findAll() : List<Category> {
        val items: MutableList<Category> = mutableListOf()
        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        val projection = arrayOf(Category.COLUMN_ID, Category.COLUMN_NAME)
        // Filter results WHERE "title" = 'My Title'
        val selection = null
        val selectionArgs = null
        // How you want the results sorted in the resulting Cursor
        val sortOrder = null
        try {
            open()
            val cursor = db.query(
                Category.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
            )
            // Read the cursor data
            while (cursor.moveToNext()) {
                val category = readFromCursor(cursor)
                items.add(category)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            close()
        }
        return items
    }
}