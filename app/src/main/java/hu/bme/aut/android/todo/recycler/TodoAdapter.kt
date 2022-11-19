package hu.bme.aut.android.todo.recycler

import android.annotation.SuppressLint
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hu.bme.aut.android.todo.databinding.TodoListBinding
import hu.bme.aut.android.todo.interfaces.TodoDialogListener
import hu.bme.aut.android.todo.items.StatusEnum
import hu.bme.aut.android.todo.items.Todo

class TodoAdapter(private val listener: TodoDialogListener) : RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

    private val items = mutableListOf<Todo>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = TodoViewHolder(
        TodoListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val todo = items[position]

        holder.binding.description.text = todo.description
        holder.binding.status.isChecked = (todo.status == StatusEnum.Completed)
        if(todo.status == StatusEnum.Completed){
            holder.binding.description.paintFlags = holder.binding.description.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        }

        holder.binding.status.setOnCheckedChangeListener { button, checked ->
            if(checked) {
                todo.status = StatusEnum.Completed
                holder.binding.description.paintFlags =
                    holder.binding.description.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            }
            else{
                todo.status = StatusEnum.Active
                holder.binding.description.paintFlags =
                    holder.binding.description.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }
        }
        holder.binding.deleteButton.setOnClickListener{
            detachTodo(todo)
            todo.delete()
        }
        holder.binding.editButton.setOnClickListener {
            listener.onShowEdit(todo)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun attachTodo(todo:Todo){
        items.add(todo)
        notifyItemInserted(items.size-1)
    }

    fun attachTodos(todos:List<Todo>){
        val start = items.size-1
        items.addAll(todos)
        notifyItemRangeInserted(start, items.size-1)
    }

    fun detachTodo(todo:Todo){
        val i = items.indexOf(todo)
        items.remove(todo)
        notifyItemRemoved(i)
    }

    fun detachTodos(){
        val i = items.size-1
        items.clear()
        notifyItemRangeRemoved(0,i)
    }

    fun refreshTodo(todo:Todo){
        val i = items.indexOf(todo)
        notifyItemChanged(i)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun refreshTodos(){
        notifyDataSetChanged()
    }


    inner class TodoViewHolder(val binding: TodoListBinding) : RecyclerView.ViewHolder(binding.root)

}