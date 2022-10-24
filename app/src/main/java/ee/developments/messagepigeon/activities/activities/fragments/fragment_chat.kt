package ee.developments.messagepigeon.activities.activities.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.projemanag.R
import ee.developments.messagepigeon.activities.activities.activity.ChatActivity
import ee.developments.messagepigeon.activities.activities.adapters.BoardItemsAdapter
import ee.developments.messagepigeon.activities.activities.firebase.FireStore
import ee.developments.messagepigeon.activities.activities.models.Board
import ee.developments.messagepigeon.activities.activities.swipeToDelete.SwipeToDeleteCallback
import ee.developments.messagepigeon.activities.activities.utils.Constans
import kotlinx.android.synthetic.main.fragment_chat.view.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [fragment_chat.newInstance] factory method to
 * create an instance of this fragment.
 */
class fragment_chat : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var shouldRefreshOnResume: Boolean? = null
    private val mFireStore = FirebaseFirestore.getInstance()
    lateinit var adapter:BoardItemsAdapter



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view: View =  inflater.inflate(R.layout.fragment_chat, container, false)




        updateBoardToUi(Constans.BOARDS_CHATS_LIST)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        FireStore().getBoardsListListener(this)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment fragment_chat.
         */

        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            fragment_chat().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onPause() {
        super.onPause()

        Log.e("onPause","${shouldRefreshOnResume.toString()}")
    }

    override fun onResume() {
        super.onResume()
        Log.e("onResume","${shouldRefreshOnResume.toString()}")

        updateBoardToUi(Constans.BOARDS_CHATS_LIST)
    }

     fun updateBoardToUi(boardsChatsList: ArrayList<Board>) {
        if(boardsChatsList.size > 0){

            Constans.BOARDS_CHATS_LIST = boardsChatsList

            val context = getContext() ?: return
            Log.e("${Constans.BOARDS_CHATS_LIST.size.toString()}","updateBoardToUi")
            view?.rv_chats_list?.visibility = View.VISIBLE
            view?.no_chats_avlible?.visibility = View.GONE

            view?.rv_chats_list?.layoutManager = LinearLayoutManager(context)
            view?.rv_chats_list?.setHasFixedSize(true)

             adapter = BoardItemsAdapter(context,Constans.BOARDS_CHATS_LIST)

            view?.rv_chats_list?.adapter = adapter
            adapter.notifyItemInserted(Constans.BOARDS_CHATS_LIST.size-1)
            adapter.notifyDataSetChanged()


            val swipeHandler = object : SwipeToDeleteCallback(context) {

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                    adapter.removeAt(viewHolder.adapterPosition)

//                    val item = Constans.BOARDS_CHATS_LIST[0]
//                    item.documentId
//                    Log.e("documentId", item.documentId.toString())

                }
            }
            val itemTouchHelper = ItemTouchHelper(swipeHandler)
            itemTouchHelper.attachToRecyclerView(view?.rv_chats_list)



            adapter.setOnClickListener(
                object :BoardItemsAdapter.OnClickListener{
                    override fun onClick(position: Int, model: Board) {
                        Log.i("fragment chat", "on click")
                        val intent = Intent(context, ChatActivity::class.java)
                        Log.d("board id", model.documentId)

                        intent.putExtra(Constans.BOARD_CHAT_DETAILS, model)
                        intent.putExtra("uid", FirebaseAuth.getInstance().currentUser?.uid )
                        intent.putExtra(Constans.DOCUMENT_ID, model.documentId)
                        intent.putExtra("position", position)

                        startActivity(intent)
                    }
                }
            )
        }else{

            Log.e("inside","updateBoardToUi2")
            view?.no_chats_avlible?.visibility = View.VISIBLE
        }
    }

//
//    fun boardChatSuccefullyDeleted(fragmentChat: fragment_chat,documentId: String){
//        FireStore().deleteTasksAfterBoardDeleted(fragmentChat, items, documentId)
//    }


    fun populateListBoardsToUi(boardList: ArrayList<Board>){
        if(boardList.size > 0){
            val context = getContext() ?: return
            Log.e("inside","populateListBoardsToUi")
            view?.rv_chats_list?.destroyDrawingCache()
            view?.rv_chats_list?.visibility = View.VISIBLE
            view?.no_chats_avlible?.visibility = View.GONE

            view?.rv_chats_list?.layoutManager = LinearLayoutManager(context)
            view?.rv_chats_list?.setHasFixedSize(true)

            val adapter = BoardItemsAdapter(context,Constans.BOARDS_CHATS_LIST)
            view?.rv_chats_list?.adapter = adapter


        }else{

            Log.e("inside","populateListBoardsToUi2")
            view?.no_chats_avlible?.visibility = View.VISIBLE
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        Log.i("onActivityReasult", "fragmrnt chat")

    }

}