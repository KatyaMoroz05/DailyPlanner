package com.example.dailyplanner

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.time.LocalDate
import java.time.LocalTime

class DatabaseHelper(context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION)
{
    companion object
    {
        private const val DATABASE_NAME = "DailyDatabase"
        private const val DATABASE_VERSION = 1

        const val USERS_TABLE_NAME = "Users"
        const val USERS_ID_COL = "id"
        const val USERS_NAME_COL = "name"
        const val USERS_GENDER_COL = "gender"
        const val USERS_BIRTHDAY_COL = "birthday"

        const val SCHEDULE_TABLE_NAME = "Schedule"
        const val SCHEDULE_ID_COL = "id"
        const val SCHEDULE_DATETIME_COL = "datetime"
        const val SCHEDULE_TITLE_COL = "title"
        const val SCHEDULE_DESCRIPTION_COL = "description"
        const val SCHEDULE_ID_PRIORITY_COL = "idPriority"
        const val SCHEDULE_IS_DONE_COL = "isDone"
        const val SCHEDULE_IS_DELETED_COL = "isDeleted"
    }

    override fun onCreate(db: SQLiteDatabase)
    {
        var query = ("CREATE TABLE " + USERS_TABLE_NAME + " ("
                + USERS_ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                USERS_NAME_COL + " TEXT," +
                USERS_GENDER_COL + " INTEGER," +
                USERS_BIRTHDAY_COL + " DATE" + ")")

        db.execSQL(query)

        query = ("CREATE TABLE " + SCHEDULE_TABLE_NAME + " ("
                + SCHEDULE_ID_COL + " INTEGER PRIMARY KEY, " +
                SCHEDULE_DATETIME_COL + " DATETIME, " +
                SCHEDULE_TITLE_COL + " TEXT, " +
                SCHEDULE_DESCRIPTION_COL + " TEXT, " +
                SCHEDULE_ID_PRIORITY_COL + " INTEGER, " +
                SCHEDULE_IS_DONE_COL + " INTEGER, " +
                SCHEDULE_IS_DELETED_COL + " INTEGER" + ")")

        db.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int)
    {
        db.execSQL("DROP TABLE IF EXISTS $USERS_TABLE_NAME")
        db.execSQL("DROP TABLE IF EXISTS $SCHEDULE_TABLE_NAME")
        onCreate(db)
    }

    fun addUser(name : String, gender: Int, birthday: LocalDate  )
    {
        val values = ContentValues()

        values.put(USERS_NAME_COL, name)
        values.put(USERS_GENDER_COL, gender)
        values.put(USERS_BIRTHDAY_COL, birthday.toString())

        val db = this.writableDatabase

        db.insert(USERS_TABLE_NAME, null, values)
        db.close()
    }

    fun updateUser(id: Int, name : String, gender: Int, birthday: LocalDate  )
    {
        val values = ContentValues()

        values.put(USERS_NAME_COL, name)
        values.put(USERS_GENDER_COL, gender)
        values.put(USERS_BIRTHDAY_COL, birthday.toString())

        val db = this.writableDatabase

        db.update(USERS_TABLE_NAME, values, "id=?", arrayOf(id.toString()))
        db.close()
    }

    fun getUsers(): Cursor
    {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM $USERS_TABLE_NAME", null)
    }

    fun addScheduleRecord(date : LocalDate, time: LocalTime, title : String,
                          description: String, idPriority: Int)
    {
        val values = ContentValues()

        values.put(SCHEDULE_DATETIME_COL, "${date.toString()}T${time.toString()}")
        values.put(SCHEDULE_TITLE_COL, title)
        values.put(SCHEDULE_DESCRIPTION_COL, description)
        values.put(SCHEDULE_ID_PRIORITY_COL, idPriority)
        values.put(SCHEDULE_IS_DONE_COL, 0)
        values.put(SCHEDULE_IS_DELETED_COL, 0)

        val db = this.writableDatabase

        db.insert(SCHEDULE_TABLE_NAME, null, values)
        db.close()
    }

    fun setIsDoneScheduleRecord(id: Int, isDone: Boolean)
    {
        val values = ContentValues()

        if(isDone)
        {
            values.put(SCHEDULE_IS_DONE_COL, 1)
        }
        else
        {
            values.put(SCHEDULE_IS_DONE_COL, 0)
        }

        val db = this.writableDatabase

        db.update(SCHEDULE_TABLE_NAME, values, "id=?", arrayOf(id.toString()))
        db.close()
    }

    fun setDeletedScheduleRecord(id: Int)
    {
        val db = this.writableDatabase

        val values = ContentValues()
        values.put(SCHEDULE_IS_DELETED_COL, 1)

        db.update(SCHEDULE_TABLE_NAME, values, "id=?", arrayOf(id.toString()))
        db.close()
    }

    fun getSchedule(): Cursor
    {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM $SCHEDULE_TABLE_NAME", null)
    }
}