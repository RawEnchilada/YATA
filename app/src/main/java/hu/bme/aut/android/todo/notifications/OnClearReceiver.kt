package hu.bme.aut.android.todo.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import hu.bme.aut.android.todo.database.DatabaseHandler
import hu.bme.aut.android.todo.items.StatusEnum
import hu.bme.aut.android.todo.items.Todo

class OnClearReceiver : BroadcastReceiver() {

    companion object{
        const val TODO_DONE = "TODO_MARKED_DONE"
    }

    fun finishTodo(todo: Todo){
        todo.status = StatusEnum.Completed
        todo.cancelNotification()
    }

    override fun onReceive(ctx: Context, intent: Intent) {
        val todoId = intent.getLongExtra("todoId",-1)
        if (todoId == -1L)return
        else{
            var todos: List<Todo> = Todo.todos
            if(!DatabaseHandler.isInitialized){
                DatabaseHandler.initialize(ctx)
                todos = DatabaseHandler.todos.getTodos(ctx)
            }

            val todo = todos.find{t -> t.id == todoId}
            if (todo != null) {
                finishTodo(todo)
            }
        }
        ctx.sendBroadcast(Intent(TODO_DONE))
    }

}