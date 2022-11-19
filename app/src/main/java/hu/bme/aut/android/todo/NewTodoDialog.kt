package hu.bme.aut.android.todo

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.DialogFragment
import hu.bme.aut.android.todo.databinding.DialogNewTodoItemBinding
import hu.bme.aut.android.todo.interfaces.TodoDialogListener

class NewTodoDialog(private val listener: TodoDialogListener) : DialogFragment() {




    private lateinit var binding: DialogNewTodoItemBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogNewTodoItemBinding.inflate(LayoutInflater.from(context))
        binding.description

        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.new_todo)
            .setView(binding.root)
            .setPositiveButton(R.string.button_ok) { dialogInterface, i ->
                listener.onCreated(binding.description.text.toString())
            }
            .setNegativeButton(R.string.button_cancel, null)
            .create()
    }
}