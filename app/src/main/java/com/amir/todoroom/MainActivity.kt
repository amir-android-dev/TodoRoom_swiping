package com.amir.todoroom

import android.app.AlertDialog
import android.app.Dialog

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amir.todoroom.adapter.ToDoAdapter
import com.amir.todoroom.databinding.ActivityMainBinding
import com.amir.todoroom.room.TodoApp
import com.amir.todoroom.room.TodoDAO
import com.amir.todoroom.room.TodoEntity
import com.amir.todoroom.swiping.SwipeToDeleteCallback
import com.amir.todoroom.swiping.SwipeToEditCallback
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null
    // private var todoList: List<TodoEntity>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
//TODO todoDb APP
        val todoDao = (application as TodoApp).db.todoDao()
//TODO btnSave + AddRecord function
        binding?.btnSave?.setOnClickListener {
            addRecord(todoDao)
        }

        lifecycleScope.launch {
            todoDao.fetchAllTodos().collect {
                val list = List(it)
                setupListOfIntoRecyclerView(list as MutableList<TodoEntity>, todoDao)

            }
        }

    }

    private fun List(size: List<TodoEntity>): List<TodoEntity> {
        return size
    }


    private fun setupListOfIntoRecyclerView(todoList: MutableList<TodoEntity>, todoDAO: TodoDAO) {
        if (todoList.isNotEmpty()) {

            val todoAdapter = ToDoAdapter(this, todoList)
            binding?.rvRec?.layoutManager = LinearLayoutManager(this)
            binding?.rvRec?.adapter = todoAdapter


      swipeToUpdate(todoList,todoDAO)
            swipeToDelete(todoList, todoDAO)

        } else {
            Toast.makeText(this, "No records", Toast.LENGTH_LONG).show()
        }

    }

    /**Adding a new Record*/

    private fun addRecord(todoDAO: TodoDAO) {
        val title = binding?.etTitle?.text.toString()
        val description = binding?.etDescription?.text.toString()

        if (title.isNotEmpty() && description.isNotEmpty()) {
            lifecycleScope.launch {
                todoDAO.insert(TodoEntity(title = title, description = description))
                Toast.makeText(
                    this@MainActivity, "Record is saved", Toast.LENGTH_LONG
                ).show()
                binding?.etTitle?.text?.clear()
                binding?.etDescription?.text?.clear()
            }
        } else {
            Toast.makeText(
                this,
                "either the title or description is empty...Please fill it.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()

    }

    private fun swipeToUpdate(todoList:MutableList<TodoEntity>,todoDAO: TodoDAO){
        val swipeToEditCallback = object : SwipeToEditCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                //TODO creating a custom dialog
                val updateDialog =
                    Dialog(this@MainActivity, androidx.appcompat.R.style.Theme_AppCompat_Dialog)
                updateDialog.setContentView(R.layout.update_record)
                updateDialog.setCancelable(false)
                val etTitleUpdate =
                    updateDialog.findViewById<AppCompatEditText>(R.id.et_title_update)
                val etDescriptionUpdate =
                    updateDialog.findViewById<AppCompatEditText>(R.id.et_description_update)
                val btnUpdateRecord = updateDialog.findViewById<Button>(R.id.btn_update_record)
                val btnCancelUpdate = updateDialog.findViewById<Button>(R.id.btn_cancel_update)
                //TODO sending data from mainActivity to update record layout
                //start
                val intent = Intent(this@MainActivity, MainActivity::class.java)
                intent.putExtra("title", todoList[viewHolder.adapterPosition].title)
                intent.putExtra("description", todoList[viewHolder.adapterPosition].description)
                val titleSendFromMainActivity = intent.getStringExtra("title")
                val descriptionSendFromMainActivity = intent.getStringExtra("description")
                etTitleUpdate.setText(titleSendFromMainActivity)
                etDescriptionUpdate.setText(descriptionSendFromMainActivity)
                //End
                //TODO Update
                btnUpdateRecord.setOnClickListener {
                    //start
                    val title = etTitleUpdate.text.toString()
                    val description = etDescriptionUpdate.text.toString()
                    if (title.isNotEmpty() && description.isNotEmpty()) {
                        lifecycleScope.launch {
                            todoDAO.update(
                                TodoEntity(
                                    todoList[viewHolder.adapterPosition].id, title, description))
                            Toast.makeText(this@MainActivity, "Record updated", Toast.LENGTH_LONG)
                                .show()
                            updateDialog.dismiss()
                            recreate()
                        }
                    } else {
                        Toast.makeText(this@MainActivity,
                            "Name or Email cannot be blank", Toast.LENGTH_LONG).show()
                    }
                    //end
                }

                btnCancelUpdate.setOnClickListener {
                    updateDialog.dismiss()
                    recreate()
                }
                updateDialog.show()

            }
        }
        //TODO binding the swiping class to activity
        val itemTouchHelper = ItemTouchHelper(swipeToEditCallback)
        itemTouchHelper.attachToRecyclerView(binding?.rvRec)
    }

    //TODO DELETE
    private fun swipeToDelete(todoList: MutableList<TodoEntity>, todoDAO: TodoDAO){
        val swipeToDeleteCallback = object : SwipeToDeleteCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                val builder = AlertDialog.Builder(this@MainActivity)
                builder.setTitle("Delete Record")
                builder.setIcon(R.drawable.ic_dangerous_24)
                builder.setPositiveButton("YES") { dialogInterface, _ ->
                    lifecycleScope.launch {
                        todoDAO.delete(TodoEntity(todoList[viewHolder.adapterPosition].id))
                        recreate()
                        Toast.makeText(
                            this@MainActivity,
                            "Delete Success",
                            Toast.LENGTH_LONG
                        ).show()

                    }
                    dialogInterface.dismiss()
                }
                builder.setNegativeButton("No") { dialogInterface, _ ->
                    dialogInterface.dismiss()
                    recreate()
                }
                val alertDialog: AlertDialog = builder.create()
                alertDialog.setCancelable(false)
                alertDialog.show()
            }

        }
        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(binding?.rvRec)
    }

//

}