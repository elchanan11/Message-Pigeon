package ee.developments.messagepigeon.activities.activities.adapters

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.projemanag.R
import ee.developments.messagepigeon.activities.activities.firebase.FireStore
import ee.developments.messagepigeon.activities.activities.models.Task
import kotlinx.android.synthetic.main.item_task.view.*

class TaskAdapter (
        val context: Context,
        val list: ArrayList<Task>
    ): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        private var onClickListener: OnClickListener? = null

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return MyViewHolder(
                LayoutInflater.from(context).inflate(
                    R.layout.item_task, parent, false)
            )
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val model = list[position]


            if (holder is TaskAdapter.MyViewHolder) {
                if (model.isTaskCompleted){
                    holder.itemView.cv_item_task.setCardBackgroundColor(Color.parseColor("#AAAAAA"))
                }

                holder.itemView.taskTitle.text = model.taskTitle
                holder.itemView.taskDetails.text = model.taskDescription

                holder.itemView.fabTaskComlited.setOnClickListener {
                    if (onClickListener != null){
                        onClickListener!!.onClick(position, model)
                    }
                }
            }
        }
        override fun getItemCount(): Int {
            return list.size
        }

        fun setOnClickListener(onClickListener: OnClickListener){
            this.onClickListener = onClickListener
        }

    fun removeAt(adapterPosition: Int) {
        Log.e("list size", list.size.toString())
        Log.e("adapterPosition", adapterPosition.toString())
        val item = list[adapterPosition]
        Log.e("itemId", item.taskId.toString())
//        list.removeAt(adapterPosition)
//        notifyItemRemoved(adapterPosition)
        //FireStore().deleteTaskItem(this,item.taskId,0)
    }

    interface OnClickListener{
            fun onClick(position: Int, model: Task)
        }

        private class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
    }
