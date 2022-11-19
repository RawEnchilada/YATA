package hu.bme.aut.android.todo.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import hu.bme.aut.android.todo.items.StatusEnum
import hu.bme.aut.android.todo.items.Todo

object DatabaseHandler {

    const val DATABASE_NAME = "todos.db"
    const val DATABASE_VERSION = 1

    private var helper: DatabaseHelper? = null
    val isInitialized: Boolean
        get(){return (helper != null)}
    private val todosDB : TodosDB = TodosDB()

    val todos: TodosDB
        get() {
            if(helper == null)throw Exception("Database has not been initialized.")
            return todosDB
        }

    fun initialize(context: Context) {
        helper = DatabaseHelper(context)
    }




    private class DatabaseHelper(context:Context): SQLiteOpenHelper(context,
        DATABASE_NAME,null,
        DATABASE_VERSION
    ){

        override fun onCreate(db: SQLiteDatabase) {
            todosDB.onCreate(db)
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            todosDB.onUpgrade(db,oldVersion,newVersion)
        }
    }


    class TodosDB {
        val DATABASE_TABLE = "todos"

        private val DATABASE_CREATE = """create table if not exists $DATABASE_TABLE (
            id integer primary key autoincrement,
            status integer not null,
            description varchar(255) not null
            );"""

        private val DATABASE_DROP = "drop table if exists $DATABASE_TABLE;"

        fun onCreate(database: SQLiteDatabase) {
            database.execSQL(DATABASE_CREATE)
        }

        fun onUpgrade(database: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            Log.w(
                TodosDB::class.java.name,
                "Upgrading from version $oldVersion to $newVersion"
            )
            database.execSQL(DATABASE_DROP)
            onCreate(database)
        }

        fun getTodos(context: Context?):List<Todo>{
            val todos = mutableListOf<Todo>()
            val cursor = helper!!.readableDatabase.query(DATABASE_TABLE, arrayOf("description","status","id"),null,null,null,null,null)
            cursor.moveToFirst()
            while(!cursor.isAfterLast){
                val todo = Todo(
                    context,
                    cursor.getString(0),
                    StatusEnum(cursor.getInt(1)),
                    cursor.getLong(2)
                )
                todos.add(todo)
                cursor.moveToNext()
            }
            cursor.close()
            return todos
        }

        /**Function has side effects regarding id!*/
        fun insertTodo(todo: Todo){
            if(todo.id != -1L)throw Exception("Todo is already in the table.")
            if(todo.id == -2L)return
            val values = ContentValues()
            values.put("description",todo.description)
            values.put("status",todo.status.value)
            val id = helper!!.writableDatabase.insert(DATABASE_TABLE,null,values)
            if(id != -1L){
                todo.id = id
            }
            else{
                throw Exception("Failed to insert todo to table.")
            }
        }

        fun updateTodo(todo: Todo){
            if(todo.id == -1L)throw Exception("Cannot update, todo is not stored in the table.")
            if(todo.id == -2L)return //when id == -2, the item is no longer in use, and should be disregarded
            val values = ContentValues()
            values.put("description",todo.description)
            values.put("status",todo.status.value)
            val affectedRows = helper!!.writableDatabase.update(DATABASE_TABLE,values,"id=?", arrayOf(todo.id.toString()))
            if(affectedRows != 1){
                throw Exception("Something went wrong while updating the field. Affected rows: $affectedRows, id: ${todo.id}")
            }
        }

        fun deleteTodo(todo: Todo){
            if(todo.id == -2L)return
            helper!!.writableDatabase.delete(DATABASE_TABLE,"id=?", arrayOf(todo.id.toString()))
            todo.id = -2L
        }
    }

}