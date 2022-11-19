package hu.bme.aut.android.todo

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import hu.bme.aut.android.todo.database.DatabaseHandler
import hu.bme.aut.android.todo.databinding.ActivityMainBinding
import hu.bme.aut.android.todo.interfaces.TodoDialogListener
import hu.bme.aut.android.todo.items.StatusEnum
import hu.bme.aut.android.todo.items.Todo
import hu.bme.aut.android.todo.notifications.OnClearReceiver
import hu.bme.aut.android.todo.recycler.TodoAdapter

class MainActivity : AppCompatActivity(), TodoDialogListener {


    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: TodoAdapter

    private val todoMarkedDoneReceiver = TodoMarkedDoneReceiver(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        DatabaseHandler.initialize(this)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(findViewById(R.id.toolbar))

        binding.floatingActionButton.setOnClickListener{
            NewTodoDialog(this).show(
                supportFragmentManager,
                "NewTodoItemDialog"
            )
        }

        registerReceiver(todoMarkedDoneReceiver, IntentFilter(OnClearReceiver.TODO_DONE))

        initRecyclerView()

    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(todoMarkedDoneReceiver)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item:MenuItem) = when(item.itemId) {
        R.id.actionThemeSwitch ->{
            if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_NO) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    fun refreshView(){
        adapter.refreshTodos()
    }



    override fun onCreated(description: String) {
        val todo = Todo(this,description)
        adapter.attachTodo(todo)
    }

    override fun onEdited(todo:Todo,description: String, status: StatusEnum) {
        todo.description = description
        todo.status = status
        adapter.refreshTodo(todo)

    }

    override fun onShowEdit(todo: Todo) {
        EditTodoDialog(this,todo).show(
            supportFragmentManager,
            "EditTodoItemDialog"
        )
    }

    private fun initRecyclerView() {
        adapter = TodoAdapter(this)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
        adapter.detachTodos()
        val todos = DatabaseHandler.todos.getTodos(this)
        adapter.attachTodos(todos)
    }


    class TodoMarkedDoneReceiver(private val mainActivity: MainActivity) : BroadcastReceiver(){

        override fun onReceive(p0: Context?, p1: Intent?) {
            mainActivity.refreshView()
        }

    }
}