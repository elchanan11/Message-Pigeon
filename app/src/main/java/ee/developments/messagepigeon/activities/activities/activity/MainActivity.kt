package ee.developments.messagepigeon.activities.activities.activity

import android.app.Activity
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.projemanag.R
import com.projemanag.databinding.ActivityMainBinding
import ee.developments.messagepigeon.activities.activities.adapters.BoardItemsAdapter
import ee.developments.messagepigeon.activities.activities.adapters.Selected_members_from_board_adapter
import ee.developments.messagepigeon.activities.activities.firebase.FireStore
import ee.developments.messagepigeon.activities.activities.fragments.fragment_chat
import ee.developments.messagepigeon.activities.activities.fragments.fragment_profile
import ee.developments.messagepigeon.activities.activities.fragments.fragment_settings
import ee.developments.messagepigeon.activities.activities.models.Board
import ee.developments.messagepigeon.activities.activities.models.User
import ee.developments.messagepigeon.activities.activities.utils.Constans
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_chat.*
import kotlinx.android.synthetic.main.fragment_chat.view.*
import kotlinx.android.synthetic.main.fragment_profile2.*
import java.security.AccessController.getContext

class MainActivity : BaseActivity(),View.OnClickListener {

    lateinit var bottomNav : BottomNavigationView
    private var binding: ActivityMainBinding? = null
    private lateinit var mUserName: String
    private val mFireStore = FirebaseFirestore.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)





        FireStore().loadUserData(this, true)

            loadFragment(fragment_chat())



            bottomNav = findViewById(R.id.bottomNav) as BottomNavigationView
        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.profile_nav -> { loadFragment(fragment_profile())

                }

                R.id.chat_nav -> { loadFragment(fragment_chat()) }

//                R.id.camera_vav -> { loadFragment(fragment_settings()) }

                else -> {}
            }
            return@setOnItemSelectedListener true
        }
        fabCreateBoard.setOnClickListener{
            //startActivityForResult(Intent(this, BoardActivity::class.java),Constans.CREATE_BOARD_REQUEST_CODE)
            startActivityForResult(Intent(this, BoardActivity::class.java),Constans.CREATE_BOARD_REQUEST_CODE)
            //finish()
        }
    }

    fun updateUserDetails(user: User, readBoardLisr: Boolean){
        mUserName = user.name
        Constans.USER_NAME = user.name
        Constans.USER_IMAGE = user.image

        Log.e("updateUserDetails","")

        if (readBoardLisr){
            //showProgressDialog()
            FireStore().getBoardsList()
            loadFragment(fragment_chat())
            hideProgressDialog()
        }

//        val transction = this.supportFragmentManager.beginTransaction()
//        val chatFragmant = fragment_chat()
//        transction.detach(chatFragmant)
//        Log.e("detach","")
//        //transction.attach(chatFragmant)
//
//        transction.commit()

        //loadFragment(fragment_chat())

    }

      fun loadFragment(fragment: Fragment){
          Log.e("inside","loadFragment")
        val fragmentMeneger = supportFragmentManager
        val transaction = fragmentMeneger.beginTransaction()

        transaction.replace(R.id.container,fragment)
        transaction.commit()
    }


    override fun onClick(v: View?) {
        when(v?.id){
            R.id.ll_profile_image_name -> {

                startActivity(Intent(this, MyProfileEditActivity::class.java))
            }
            R.id.ll_profile -> {
                startActivity(Intent(this, MyProfileEditActivity::class.java))
            }
            R.id.ll_sign_out -> {

                FirebaseAuth.getInstance().signOut()

                val intent = Intent(this@MainActivity, IntroActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
                finish()

            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.i("Main ","onActivityResult")

        if (requestCode == Constans.CREATE_BOARD_REQUEST_CODE
            && requestCode == Activity.RESULT_OK){
            Log.e("Main ","onActivityResult")
            FireStore().getBoardsList()
            loadFragment(fragment_chat())
        }

//        mFireStore.collection(Constans.BOARDS)
//            .whereArrayContains(Constans.ASSIGNED_TO,FireStore().getCurrentUid())
//            .orderBy("timeStamp", Query.Direction.DESCENDING)
//            .addSnapshotListener { value, e ->
//                Log.d("MainActivity","board listener")
//                if (e != null) {
//                    Log.w(ContentValues.TAG, "Listen failed.", e)
//                    return@addSnapshotListener
//                }
//
//                val boards = ArrayList<Board>()
//
//                Constans.BOARDS_CHATS_LIST = ArrayList()
//                for (doc in value!!) {
//                    val board = doc.toObject(Board()::class.java)
//                    Constans.BOARDS_CHATS_LIST.add(board)
//
//                }
//                Log.d(ContentValues.TAG, "Current cites in CA: $boards")
//
//                loadFragment(fragment_chat())
//                //FireStore().getBoardsList()
//
//
//              if(Constans.BOARDS_CHATS_LIST.size > 0) {
//                  Log.e("${Constans.BOARDS_CHATS_LIST.size.toString()}", "updateBoardToUi")
//                  rv_chats_list?.visibility = View.VISIBLE
//                  no_chats_avlible?.visibility = View.GONE
//
//                  rv_chats_list?.layoutManager = LinearLayoutManager(this)
//                  rv_chats_list?.setHasFixedSize(true)
////might be an error
//                  val adapter = BoardItemsAdapter(this, Constans.BOARDS_CHATS_LIST)
//                  rv_chats_list?.adapter = adapter
//
//                  adapter.notifyItemInserted(0)
//
//                  BoardItemsAdapter(this,Constans.BOARDS_CHATS_LIST).notifyItemInserted(0)
//
//
//                  adapter.setOnClickListener(
//                      object : BoardItemsAdapter.OnClickListener {
//                          override fun onClick(position: Int, model: Board) {
//                              Log.i("fragment chat", "on click")
//                              val intent = Intent(this@MainActivity, ChatActivity::class.java)
//
//                              intent.putExtra(Constans.BOARD_CHAT_DETAILS, model)
//                              intent.putExtra("uid", FirebaseAuth.getInstance().currentUser?.uid)
//                              intent.putExtra(Constans.DOCUMENT_ID, model.documentId)
//                              intent.putExtra("position", position)
//
//                              startActivityForResult(
//                                  intent,
//                                  Constans.UPDATE_BORARDS_SET_REQUEST_CODE
//                              )
//                          }
//                      }
//                  )
//              }else{
//
//                Log.e("inside","updateBoardToUi2")
//                no_chats_avlible?.visibility = View.VISIBLE
//              }
 //          }


    }


}