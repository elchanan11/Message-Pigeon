package ee.developments.messagepigeon.activities.activities.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.projemanag.R
import ee.developments.messagepigeon.activities.activities.activity.ChatActivity
import ee.developments.messagepigeon.activities.activities.models.User
import ee.developments.messagepigeon.activities.activities.utils.Constans
import kotlinx.android.synthetic.main.item_chat.view.*
import kotlinx.android.synthetic.main.item_member1.view.*

class MembersListItemAdapter(
    val context: Context,
    val list: ArrayList<User>
):RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_member1, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is MembersListItemAdapter.MyViewHolder) {

            Glide
                .with(context)
                .load(model.image)
                .centerCrop()
                .placeholder(R.drawable.ic_baseline_person_24)
                .into(holder.itemView.iv_member_image)

            holder.itemView.tv_member_name.text = model.name
            holder.itemView.tv_member_email.text = model.email

            holder.itemView.setOnClickListener {


            }
        }
    }


    override fun getItemCount(): Int {
        return list.size
    }

    private class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}