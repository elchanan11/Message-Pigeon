package ee.developments.messagepigeon.activities.activities.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.projemanag.R
import ee.developments.messagepigeon.activities.activities.firebase.FireStore
import ee.developments.messagepigeon.activities.activities.models.Board
import kotlinx.android.synthetic.main.recive_message.view.*
import kotlinx.android.synthetic.main.sent_message.view.*

class MaessageAdapter(val context: Context, val messegeList: ArrayList<ee.developments.messagepigeon.activities.activities.models.Message>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    val ITEM_RECIVE = 1
    val ITEM_SENT = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if(viewType == 1){
            Log.e("inflating", "R.layout.recive_message")
            return MaessageAdapter.ReciveViewHolder(
                LayoutInflater.from(context).inflate(
                    R.layout.recive_message, parent, false
                )
            )
        }else{
            Log.e("inflating", "R.layout.sent")
            return MaessageAdapter.SentViewHolder(
                LayoutInflater.from(context).inflate(
                    R.layout.sent_message, parent, false
                )
            )
        }
    }

    @SuppressLint("LongLogTag")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val currentMaeege = messegeList[position]



        Log.e("onBindViewHolder", "")
        Log.e("holder.itemView.javaClass", "${holder.itemView.javaClass}")

        if (holder is SentViewHolder) {
            Log.e("time from adapter", currentMaeege.time.toString())
            holder.itemView.tv_sent_message.text = currentMaeege.message
            holder.itemView.tv_sent_message_time.text = currentMaeege.time

        } else if (holder is ReciveViewHolder) {
            //Log.e("reciveMessege", currentMaeege.senderId.toString())

            holder.itemView.tv_recive_message.text = currentMaeege.message
            holder.itemView.tv_recive_message_time.text = currentMaeege.time
            holder.itemView.tv_sender_name.text = currentMaeege.senderName
        }
    }

    override fun getItemCount(): Int {
        return messegeList.size
    }

    override fun getItemViewType(position: Int): Int {
        val currentMassage = messegeList[position]

        if( currentMassage.senderId == FireStore().getCurrentUid()){
            return ITEM_SENT
        }else{
            return ITEM_RECIVE
        }
    }



    class SentViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val sentMessege = itemView.findViewById<TextView>(R.id.tv_sent_message)
        val date = itemView.findViewById<TextView>(R.id.tv_sent_message_time)
    }


    class ReciveViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val reciveMessege = itemView.findViewById<TextView>(R.id.tv_recive_message)
        val date = itemView.findViewById<TextView>(R.id.tv_sent_message_time)
        val senderName = itemView.findViewById<TextView>(R.id.tv_sender_name)
    }

}