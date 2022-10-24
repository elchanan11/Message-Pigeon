package ee.developments.messagepigeon.activities.activities.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.projemanag.R
import ee.developments.messagepigeon.activities.activities.models.Selected_members_from_crateBoard
import ee.developments.messagepigeon.activities.activities.models.User
import kotlinx.android.synthetic.main.item_add_member_from_board.view.*
import kotlinx.android.synthetic.main.item_member1.view.*

class Selected_members_from_board_adapter(
    val context: Context,
    val list: ArrayList<Selected_members_from_crateBoard>
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_add_member_from_board, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]


        if (holder is Selected_members_from_board_adapter.MyViewHolder) {
            if (position == list.size - 1){
                holder.itemView.iv_add_member.visibility = View.VISIBLE
                holder.itemView.iv_selected_member_image.visibility = View.GONE

                Log.e("members1:" , model.toString())
                Glide
                    .with(context)
                    .load(model.image)
                    .centerCrop()
                    .placeholder(R.drawable.ic_baseline_person_24)
                    .into(holder.itemView.iv_selected_member_image)

            }else{
                holder.itemView.iv_add_member.visibility = View.GONE
                holder.itemView.iv_selected_member_image.visibility = View.VISIBLE
                Log.e("members2:" , model.toString())
                Glide
                .with(context)
                .load(model.image)
                .centerCrop()
                .placeholder(R.drawable.ic_baseline_person_24)
                .into(holder.itemView.iv_selected_member_image)
            }


            holder.itemView.setOnClickListener {
                if (onClickListener != null){
                    onClickListener!!.onClick()
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

    interface OnClickListener{
        fun onClick()
    }

    private class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}