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

        const val GENDERS_TABLE_NAME = "Genders"
        const val GENDERS_ID_COL = "id"
        const val GENDERS_GENDER_COL = "gender"

        const val SCHEDULE_TABLE_NAME = "Schedule"
        const val SCHEDULE_ID_COL = "id"
        const val SCHEDULE_DATETIME_COL = "datetime"
        const val SCHEDULE_TITLE_COL = "title"
        const val SCHEDULE_DESCRIPTION_COL = "description"
        const val SCHEDULE_ID_PRIORITY_COL = "idPriority"
        const val SCHEDULE_IS_DONE_COL = "isDone"
        const val SCHEDULE_IS_DELETED_COL = "isDeleted"

        const val PRIORITIES_TABLE_NAME = "Priorities"
        const val PRIORITY_ID_COL = "id"
        const val PRIORITY_PRIORITY_COL = "priority"
    }

    override fun onCreate(db: SQLiteDatabase)
    {
        var query = """
            CREATE TABLE $USERS_TABLE_NAME
            (
                $USERS_ID_COL INTEGER PRIMARY KEY AUTOINCREMENT,
                $USERS_NAME_COL TEXT,
                $USERS_GENDER_COL INTEGER,
                $USERS_BIRTHDAY_COL DATE,
                FOREIGN KEY ($USERS_ID_COL)
                REFERENCES $GENDERS_TABLE_NAME ($GENDERS_ID_COL)
            );
        """.trimIndent()
        db.execSQL(query)

        query = ("""
            CREATE TABLE $GENDERS_TABLE_NAME
            (
                $GENDERS_ID_COL INTEGER PRIMARY KEY AUTOINCREMENT,
                $GENDERS_GENDER_COL TEXT
            ); 
        """.trimIndent())
        db.execSQL(query)

        query = """
            CREATE TABLE $SCHEDULE_TABLE_NAME
            (
                $SCHEDULE_ID_COL INTEGER PRIMARY KEY AUTOINCREMENT,
                $SCHEDULE_DATETIME_COL DATETIME,
                $SCHEDULE_TITLE_COL TEXT,
                $SCHEDULE_DESCRIPTION_COL TEXT,
                $SCHEDULE_ID_PRIORITY_COL INTEGER,
                $SCHEDULE_IS_DONE_COL INTEGER,
                $SCHEDULE_IS_DELETED_COL INTEGER,
                FOREIGN KEY ($SCHEDULE_ID_COL)
                REFERENCES $PRIORITIES_TABLE_NAME ($PRIORITY_ID_COL)
            );
        """.trimIndent()
        db.execSQL(query)

        query = ("""
            CREATE TABLE $PRIORITIES_TABLE_NAME
            (
                $PRIORITY_ID_COL INTEGER PRIMARY KEY AUTOINCREMENT,
                $PRIORITY_PRIORITY_COL TEXT
            ); 
        """.trimIndent())
        db.execSQL(query)

        val valuesGenders = ContentValues()
        valuesGenders.put(GENDERS_GENDER_COL, "Woman")
        db.insert(GENDERS_TABLE_NAME, null, valuesGenders)
        valuesGenders.put(GENDERS_GENDER_COL, "Man")
        db.insert(GENDERS_TABLE_NAME, null, valuesGenders)

        val valuesTypesOfTransactions = ContentValues()
        valuesTypesOfTransactions.put(PRIORITY_PRIORITY_COL, "Very important")
        db.insert(PRIORITIES_TABLE_NAME, null, valuesTypesOfTransactions)
        valuesTypesOfTransactions.put(PRIORITY_PRIORITY_COL, "Important")
        db.insert(PRIORITIES_TABLE_NAME, null, valuesTypesOfTransactions)
        valuesTypesOfTransactions.put(PRIORITY_PRIORITY_COL, "Not important")
        db.insert(PRIORITIES_TABLE_NAME, null, valuesTypesOfTransactions)
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
    fun getSchedule(): Cursor
    {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM $SCHEDULE_TABLE_NAME", null)
    }
    fun selectGenderInGendersById(id: Int): String
    {
        val db = this.readableDatabase
        val query = """
            SELECT $GENDERS_GENDER_COL FROM $GENDERS_TABLE_NAME 
            WHERE $GENDERS_ID_COL = $id
        """.trimIndent()

        val cursor = db.rawQuery(query, null)
        var gender: String
        if(!cursor.moveToFirst())
        {
            gender = ""
        }
        gender = cursor.getString(0)
        cursor.close()
        return gender
    }
    fun selectIdInGendersByGender(gender: String): Int
    {
        val db = this.readableDatabase
        val query = """
            SELECT $GENDERS_ID_COL FROM $GENDERS_TABLE_NAME 
            WHERE $GENDERS_GENDER_COL = "$gender"
        """.trimIndent()

        val cursor = db.rawQuery(query, null)
        var id: Int
        if(!cursor.moveToFirst())
        {
            id = -1
        }
        id = cursor.getInt(0)
        cursor.close()
        return id
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


}