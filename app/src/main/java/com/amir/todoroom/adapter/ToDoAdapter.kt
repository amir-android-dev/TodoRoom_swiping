package com.amir.todoroom.adapter

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.amir.todoroom.R
import com.amir.todoroom.databinding.RecyclerItemsBinding
import com.amir.todoroom.room.TodoApp
import com.amir.todoroom.room.TodoDAO
import com.amir.todoroom.room.TodoEntity
import com.amir.todoroom.swiping.SwipeToEditCallback
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.NonDisposableHandle.parent
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ToDoAdapter(
    context: Context,
    private val todoList: MutableList<TodoEntity>

) : RecyclerView.Adapter<ToDoAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            RecyclerItemsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val context = holder.itemView.context
        val todo = todoList[position]
        holder.tvTitle.text = todo.title
        holder.tvDescription.text = todo.description

        if (position % 2 == 0) {
            holder.cvMain.setBackgroundColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.light_gray
                )
            )
        }

    }

    override fun getItemCount(): Int {
        return todoList.size
    }

    fun test(position: Int) {
        todoList.removeAt(position)
        notifyItemRemoved(position)
    }

    @SuppressLint("NotifyDataSetChanged")
    @OptIn(DelicateCoroutinesApi::class)
    fun dialogBoxEdit(context: Context, position: Int, todoDAO: TodoDAO) {
        val updateDialog = Dialog(context, androidx.appcompat.R.style.Theme_AppCompat_Dialog)
        updateDialog.setContentView(R.layout.update_record)
        updateDialog.setCancelable(false)
        val etTitleUpdate = updateDialog.findViewById<AppCompatEditText>(R.id.et_title_update)
        val etDescriptionUpdate =
            updateDialog.findViewById<AppCompatEditText>(R.id.et_description_update)
        val btnUpdateRecord = updateDialog.findViewById<Button>(R.id.btn_update_record)
        val btnCancelUpdate = updateDialog.findViewById<Button>(R.id.btn_cancel_update)




        btnUpdateRecord.setOnClickListener {

                    val title = etTitleUpdate.text.toString()
                    val description = etDescriptionUpdate.text.toString()
                    if (title.isNotEmpty() && description.isNotEmpty()) {
                        GlobalScope.launch {
                            Looper.prepare()
                            todoDAO.update(TodoEntity(position, title, description))
                            Toast.makeText(context, "Record updated", Toast.LENGTH_LONG).show()

                            updateDialog.dismiss()

                        }
                    } else {
                        Toast.makeText(context, "Name or Email cannot be blank", Toast.LENGTH_LONG).show()

                    }
                }

        btnCancelUpdate.setOnClickListener {
            updateDialog.dismiss()

        }

        updateDialog.show()
    }

    inner class ViewHolder(itemsBinding: RecyclerItemsBinding) :
        RecyclerView.ViewHolder(itemsBinding.root) {
        val clMain = itemsBinding.clMain
        val cvMain = itemsBinding.cvMain
        val tvTitle = itemsBinding.tvTitleItem
        val tvDescription = itemsBinding.tvDescriptionItem


    }
}









