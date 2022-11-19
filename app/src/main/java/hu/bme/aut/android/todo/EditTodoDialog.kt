package hu.bme.aut.android.todo

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.DialogFragment
import hu.bme.aut.android.todo.databinding.DialogEditTodoItemBinding
import hu.bme.aut.android.todo.interfaces.TodoDialogListener
import hu.bme.aut.android.todo.items.StatusEnum
import hu.bme.aut.android.todo.items.Todo

class EditTodoDialog(private val listener: TodoDialogListener, private val todo : Todo) : DialogFragment() {


    private lateinit var binding: DialogEditTodoItemBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogEditTodoItemBinding.inflate(LayoutInflater.from(context))
        binding.description.setText(todo.description)
        binding.statusBox.isChecked = (todo.status == StatusEnum.Completed)


        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.edit_todo)
            .setView(binding.root)
            .setPositiveButton(R.string.button_ok) { dialogInterface, i ->
                listener.onEdited(todo,binding.description.text.toString(), StatusEnum(if(binding.statusBox.isChecked) 0 else 1))
            }
            .setNegativeButton(R.string.button_cancel, null)
            .create()
    }
}