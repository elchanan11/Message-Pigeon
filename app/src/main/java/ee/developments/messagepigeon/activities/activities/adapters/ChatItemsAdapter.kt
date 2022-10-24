package ee.developments.messagepigeon.activities.activities.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.projemanag.R
import ee.developments.messagepigeon.activities.activities.firebase.FireStore
import ee.developments.messagepigeon.activities.activities.models.Board
import kotlinx.android.synthetic.main.item_chat.view.*
import java.util.*
import kotlin.collections.ArrayList


// TODO (Step 6: Create an adapter class for Board Items in the MainActivity.)
// START
open class BoardItemsAdapter(
    private val context: Context,
    private var list: ArrayList<Board>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>()  {

    private var onClickListener: OnClickListener? = null

    /**
     * Inflates the item views which is designed in xml layout file
     *
     * create a new
     * {@link ViewHolder} and initializes some private fields to be used by RecyclerView.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_chat,
                parent,
                false
            )
        )
    }

    /**
     * Binds each item in the ArrayList to a view
     *
     * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent
     * an item.
     *
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is MyViewHolder) {

            Glide
                .with(context)
                .load(model.image)
                .centerCrop()
                .placeholder(R.drawable.ic_baseline_person_24)
                .into(holder.itemView.iv_board_image)
            val participants =ArrayList<String>()
                for (i in model.boardParticipants){
                participants.add(i)
            }

            holder.itemView.tv_name.text = model.name
            holder.itemView.tv_created_by.text = "Created By : ${model.createdBy}"

            holder.itemView.setOnClickListener {
//
//                val intent = Intent(context,ChatActivity::class.java)
//
//                intent.putExtra(Constans.BOARD_CHAT_DETAILS, model)
//                intent.putExtra("uid",FirebaseAuth.getInstance().currentUser?.uid )
//                intent.putExtra(Constans.DOCUMENT_ID, model.documentId)
//                intent.putExtra("position", position)
//
//                (context as Activity).startActivityForResult(intent, Constans.UPDATE_BORARDS_SET_REQUEST_CODE)
//
                if (onClickListener != null) {
                    onClickListener!!.onClick(position, model)
                }
            }
        }
    }


    fun refreshOrderItems(position: Int){

        //notifyItemChanged(0)
        notifyItemRemoved(1)

        Collections.swap(list, position, 0)
        notifyItemMoved(position,0)

        //notifyDataSetChanged()

    }

    /**
     * Gets the number of items in the list
     */
    override fun getItemCount(): Int {
        return list.size
    }


    fun removeAt(position: Int) {
        val item = list[position]
        FireStore().deleteChatBoardDocument(this,item.documentId,position)
    }

    /**
     * A function for OnClickListener where the Interface is the expected parameter..
     */
    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    fun boardDeletedSuccefully(documentId: String, position: Int) {

        FireStore().getTasksAfterBoardDeletedAndDeleteMessages(this,documentId,position)

    }

    fun getTasksAfterBoardDeletedSuccess(items: ArrayList<String>, position: Int) {
        FireStore().deleteTasksAfterBoardDeleted(this,items,position)

        list.removeAt(position)
        notifyItemRemoved(position)
    }

    /**
     * An interface for onclick items.
     */
    interface OnClickListener {
        fun onClick(position: Int, model: Board)
    }



    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     */
    private class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}