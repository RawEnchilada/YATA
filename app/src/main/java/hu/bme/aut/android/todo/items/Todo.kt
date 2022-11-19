package hu.bme.aut.android.todo.items

import android.content.Context
import hu.bme.aut.android.todo.database.DatabaseHandler
import hu.bme.aut.android.todo.notifications.Notification

class StatusEnum(private val _value:Int){ //not an actual enum, for easy initialization during queries
    companion object {
        val Completed = StatusEnum(0)
        val Active = StatusEnum(1)
    }
    val value:Int get() = _value

    override fun equals(other: Any?): Boolean {
        if(other is StatusEnum)return _value == other._value
        else return false
    }

    override fun hashCode(): Int {
        return _value
    }
}

class Todo (
    private var context: Context?,
    private var _description:String,
    private var _status:StatusEnum = StatusEnum.Active,
    private var _id:Long = -1
) {

    private var notification: Notification? = null

    companion object{
        val todos = mutableListOf<Todo>()
    }

    var description:String
        get() = _description
        set(value) {
            _description = value
            update()
        }

    var status:StatusEnum
        get() = _status
        set(value) {
            _status = value
            update()
        }
    /**
     * ID can have multiple meanings:
     * -2 - The instance is no longer in use and should be disposed of, do not use the instance.
     * -1 - The instance has not been saved to the database yet.
     * >= 0 - The instance has been saved to the database and has been assigned this id.
     */
    var id:Long
        get() = _id
        set(value) {
            _id = value
            update()
        }

    init {
        todos.add(this)
        if( id == -1L){
            DatabaseHandler.todos.insertTodo(this)
        }
        showNotification()
    }

    fun delete(){
        todos.remove(this)
        if( id != -1L) {
            DatabaseHandler.todos.deleteTodo(this)
        }
        cancelNotification()
    }

    private fun update(){
        DatabaseHandler.todos.updateTodo(this)
        cancelNotification()
        showNotification()
    }



    fun showNotification(){
        if(notification == null && status == StatusEnum.Active && context != null){
            notification = Notification(id.toInt(),this,context!!)
            notification!!.show()
        }
    }

    fun cancelNotification(){
        notification?.cancel()
        notification = null
    }


}