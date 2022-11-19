package hu.bme.aut.android.todo.interfaces

import hu.bme.aut.android.todo.items.StatusEnum
import hu.bme.aut.android.todo.items.Todo

interface TodoDialogListener {
    fun onCreated(description: String)
    fun onEdited(todo:Todo,description:String, status: StatusEnum)
    fun onShowEdit(todo:Todo)
}