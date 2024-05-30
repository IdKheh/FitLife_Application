package com.example.fitlife

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBLocalHelper(context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        val query = ("CREATE TABLE " + TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DATA_COL + " TEXT," +
                AGE_COL + " INTEGER," +
                HEIGHT_COL + " INTEGER," +
                WEIGHT_COL + " INTEGER," +
                BMI_COL + " FLOAT)")
        db.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun dropData() {
        writableDatabase.use { db ->
            db.delete(TABLE_NAME, null, null)
        }
    }

    fun addRecord(age: Int, height: Int, weight: Int, bmi: Float) {
        val values = ContentValues().apply {
            put(AGE_COL, age)
            put(HEIGHT_COL, height)
            put(WEIGHT_COL, weight)
            put(BMI_COL, bmi)
            put(DATA_COL, System.currentTimeMillis()) // Storing current time in milliseconds
        }

        val db = this.writableDatabase
        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    fun getAllRecords(): Cursor? {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_NAME", null)
    }

    companion object {
        private const val DATABASE_NAME = "FitApplication"
        private const val DATABASE_VERSION = 1
        const val TABLE_NAME = "Fit"
        const val ID_COL = "id"
        const val DATA_COL = "data"
        const val AGE_COL = "age"
        const val HEIGHT_COL = "height"
        const val WEIGHT_COL = "weight"
        const val BMI_COL = "bmi"
    }
}
