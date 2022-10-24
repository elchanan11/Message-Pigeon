package ee.developments.messagepigeon.activities.activities.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.projemanag.R
import com.projemanag.databinding.ActivityChatBinding
import ee.developments.messagepigeon.activities.activities.adapters.BoardItemsAdapter
import ee.developments.messagepigeon.activities.activities.adapters.MaessageAdapter
import ee.developments.messagepigeon.activities.activities.adapters.TaskAdapter
import ee.developments.messagepigeon.activities.activities.firebase.FireStore
import ee.developments.messagepigeon.activities.activities.models.Board
import ee.developments.messagepigeon.activities.activities.models.Message
import ee.developments.messagepigeon.activities.activities.models.Task
import ee.developments.messagepigeon.activities.activities.swipeToDelete.SwipeToDeleteCallback
import ee.developments.messagepigeon.activities.activities.utils.Constans
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.fragment_chat.view.*
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.*

class ChatActivity : BaseActivity() {

    private var binding: ActivityChatBinding? = null
    private lateinit var messageAdapter: MaessageAdapter
    private var mBoardChatDetails: Board? = null
    private var reciveDocumentId: String? = null

    private var mMessageRoom: String? = null
    private lateinit var mDbRef :DatabaseReference
    private lateinit var messageList: ArrayList<Message>

    private var flagAdapterCreated: Boolean = false
    private lateinit var adapter: MaessageAdapter

    private lateinit var timeStamp: String
    private  var timeStampChanged: Boolean = false
    private var chatPositionInBoard: Int? = null

    private lateinit var txtProgress: TextView
    private lateinit var progressBar: ProgressBar
    private var pStatus: Int? = null



    @SuppressLint("LongLogTag")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        if (intent.hasExtra(Constans.BOARD_CHAT_DETAILS)) {
            mBoardChatDetails = intent.getParcelableExtra<Board>(Constans.BOARD_CHAT_DETAILS)

        }

        val senderId = intent.getStringExtra("uid")
        reciveDocumentId = intent.getStringExtra(Constans.DOCUMENT_ID)
        Constans.DOCUMENTt_ID = reciveDocumentId!!
        chatPositionInBoard = intent.getIntExtra("position",0)

        Log.e("${ee.developments.messagepigeon.activities.activities.utils.Constans.DOCUMENT_ID} inside Chat Activity", reciveDocumentId.toString())

        setupActionBar(mBoardChatDetails!!.name)

        mDbRef = FirebaseDatabase.getInstance().getReference()
        messageList = ArrayList()
        setUpMaessegeAdapter()

// logic for adding data to recyclerView
        showProgressDialog()
        mDbRef.child("message").child(reciveDocumentId!!)
            .addValueEventListener(object: ValueEventListener, ChildEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    messageList.clear()

                    for (postSnapShot in snapshot.children){
                        val message = postSnapShot.getValue(Message::class.java)
                        Log.e("message", "${postSnapShot.getValue(Message::class.java)}")
                        if (message?.message.toString().isNotEmpty() && message?.message != null) {
                            messageList.add(message!!)
                            Log.e("message1", message.message.toString())
                        }
                    }
                     timeStamp  = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        DateTimeFormatter.ISO_INSTANT.format(Instant.now())
                    } else {
                        ""
                    }

                    txtProgress = findViewById(R.id.txtProgress);
                    progressBar = findViewById(R.id.progressBar);
                    pStatus = 0

                    progressBar.progress = pStatus!!;
                    txtProgress.text = "$pStatus %";

                    FireStore().getTasksListListener(this@ChatActivity)
                    setUpMaessegeAdapter()
                    //setUpTaskAdapter()

                    hideProgressDialog()
                }

                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    Log.i("inside", "l")
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

                }

                override fun onChildRemoved(snapshot: DataSnapshot) {

                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

                }

                override fun onCancelled(error: DatabaseError) {

                }


            })



        mMessageRoom = senderId


        binding?.ivSentMessage?.setOnClickListener{

            if (et_chat_box.text.toString().isNotEmpty()) {
                val sdf = SimpleDateFormat("dd/M")
                val currentDate = sdf.format(Date())
                val date = currentDate.toString()
                val name = Constans.USER_NAME

                Log.e("name", Constans.USER_NAME)

                val message = et_chat_box.text.toString()
                val messegeObject = Message(message, senderId, reciveDocumentId, currentDate,name)


                Log.e("Date", currentDate)

                mDbRef.child("message").child(reciveDocumentId!!)
                    .push()
                    .setValue(messegeObject)

                et_chat_box.text.clear()


                FireStore().getBoardsList()

//                    val chatBoardAdapter = rv_chats_list.adapter
//                    chatBoardAdapter?.notifyDataSetChanged()

                 val temp = Constans.BOARDS_CHATS_LIST[chatPositionInBoard!!]
                Constans.BOARDS_CHATS_LIST.remove(temp)
                Constans.BOARDS_CHATS_LIST.add(0,temp)

                val chatsAdapter = BoardItemsAdapter(this,Constans.BOARDS_CHATS_LIST)


                chatsAdapter.refreshOrderItems(chatPositionInBoard!!)

                timeStampChanged = true
            }
        }
        rbChat.setOnClickListener{
            rv_chat_list.visibility = View.VISIBLE
            binding?.llWriteMessge?.visibility = View.VISIBLE
            rv_task_list.visibility = View.INVISIBLE
            binding?.llAddTaskToTaskList?.visibility = View.INVISIBLE

        }
        rbTask.setOnClickListener{
            rv_chat_list.visibility = View.INVISIBLE
            binding?.llWriteMessge?.visibility = View.GONE
            binding?.rvTaskList?.visibility = View.VISIBLE
            binding?.llAddTaskToTaskList?.visibility = View.VISIBLE
        }
        binding?.cvAddTask?.setOnClickListener{
            val intent = Intent(this, CreateNewTask::class.java)
            startActivityForResult(intent, Constans.CREATE_NEW_TASK_REQUEST_CODE)
        }



    }


    fun setUpTaskAdapter(tasks: ArrayList<Task>){

        //for the precentege of the progres dialog
        var tasksCompleted = 0.0
        for (item in tasks){
            val task = item
            if (task.isTaskCompleted == true){
                tasksCompleted = tasksCompleted!! + 1
            }
        }

        if (tasks.size > 0) {

            val div: Double = tasksCompleted / tasks.size
            pStatus = ((tasksCompleted / tasks.size).toDouble() * 100).toInt()

            Log.e("div", div.toString())
            Log.e("tasksCompleted", tasksCompleted.toString())
            Log.e("pStatus", pStatus.toString())
            Log.e("tasks.size", tasks.size.toString())
        }else{
            pStatus = 0
        }
        progressBar.progress = pStatus!!;
        txtProgress.text = "$pStatus %";

        var taskAdapter:TaskAdapter

        rv_task_list.layoutManager = LinearLayoutManager(this)
        rv_task_list.setHasFixedSize(true)

        taskAdapter = TaskAdapter(this,tasks)
        Log.e("messageList",tasks.toString())

        rv_task_list.adapter = taskAdapter
        flagAdapterCreated = true
        //adapter.notifyDataSetChanged()

        val swipeHandler = object : SwipeToDeleteCallback(this) {

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                taskAdapter.removeAt(viewHolder.adapterPosition)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(rv_task_list)


        taskAdapter.setOnClickListener(
            object :TaskAdapter.OnClickListener{
                override fun onClick(position: Int, model: Task) {
                    val userHashMap = HashMap<String, Any>()
                    if (!model.isTaskCompleted) {
                        Log.i("fabTaskComlited", "Task complited")
                        userHashMap[Constans.IS_TASK_COMPLITED] = true
                        FireStore().uptateTaskCoplited(this@ChatActivity, userHashMap, model)
                    }
                }

            }
        )        //rv_task_list.smoothScrollToPosition(rv_task_list.getAdapter()!!.getItemCount());
    }

    fun setUpMaessegeAdapter(){

        if (flagAdapterCreated == false) {
            messageAdapter = MaessageAdapter(this, messageList)

            rv_chat_list.layoutManager = LinearLayoutManager(this)
            rv_chat_list.setHasFixedSize(true)

             adapter = MaessageAdapter(this, messageList)
            Log.e("messageList",messageList.toString())

            rv_chat_list.adapter = adapter
            flagAdapterCreated = true

        }else{
            adapter.notifyDataSetChanged()
            rv_chat_list.smoothScrollToPosition(rv_chat_list.getAdapter()!!.getItemCount());
        }
    }

    fun bordsOrderUpdated(){
        FireStore().getBoardsList()

        adapter.notifyDataSetChanged()
        setResult(RESULT_OK)
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && requestCode == Constans.CREATE_NEW_TASK_REQUEST_CODE && data != null) {
            if (data?.hasExtra("task") == true) {
                FireStore().getTasksListListener(this)

                timeStamp  = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    DateTimeFormatter.ISO_INSTANT.format(Instant.now())
                } else {
                    ""
                }

                timeStampChanged = true
                Log.d("newTask", "newTask?.taskTitle.toString()")
            }

        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_members, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_members ->{
                val intent = Intent(this, MembersActivity::class.java)
                intent.putExtra(Constans.BOARD_CHAT_DETAILS,mBoardChatDetails )
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupActionBar(title: String) {

        setSupportActionBar(toolbar_task_list_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
            actionBar.title = title
        }

        toolbar_task_list_activity.setNavigationOnClickListener {
            onBackPressed()
        }
    }
    override fun onBackPressed() {
    super.onBackPressed();
        val boardHashMap = HashMap<String, Any>()
        boardHashMap[Constans.TIME_STAMP] = timeStamp


        if (timeStampChanged) {

            showProgressDialog()
            FireStore().updateTimeStamp(this, boardHashMap, mBoardChatDetails!!)
            hideProgressDialog()

            //startActivity(Intent(this,MainActivity::class.java))
        }else{
            //onBackPressed()
            setResult(RESULT_OK)
            finish()
        }
    }

    fun taskComplitedUpdatedSuccefully() {
        FireStore().getTasksListListener(this)
    }

}