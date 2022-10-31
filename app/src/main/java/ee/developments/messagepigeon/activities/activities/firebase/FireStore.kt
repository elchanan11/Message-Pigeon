package ee.developments.messagepigeon.activities.activities.firebase

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues.TAG
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import ee.developments.messagepigeon.activities.activities.activity.*
import ee.developments.messagepigeon.activities.activities.adapters.BoardItemsAdapter
import ee.developments.messagepigeon.activities.activities.adapters.TaskAdapter
import ee.developments.messagepigeon.activities.activities.fragments.fragment_chat
import ee.developments.messagepigeon.activities.activities.models.Board
import ee.developments.messagepigeon.activities.activities.models.Task
import ee.developments.messagepigeon.activities.activities.models.User
import ee.developments.messagepigeon.activities.activities.signInActivity
import ee.developments.messagepigeon.activities.activities.utils.Constans

class FireStore {
    private val mFireStore = FirebaseFirestore.getInstance()

    var mDbRef = FirebaseDatabase.getInstance().getReference()

    var database: FirebaseDatabase = FirebaseDatabase.getInstance()



    @SuppressLint("LongLogTag")
    fun registerUser(activity: SignUpActivity, userInfo: User){
        Log.e("inside Fire base","check the fun")



        mFireStore.collection(Constans.USERS)
           .document(getCurrentUid()).set(userInfo, SetOptions.merge())
           .addOnSuccessListener {
               Log.e("inside addOnSuccessListener","check the fun")
               activity.userRegisterSuccess()
           }
            .addOnFailureListener {
                e ->
                activity.hideProgressDialog()
                Log.e("Error while creating board", e.message.toString())
            }
    }

    @SuppressLint("LongLogTag")
    fun getSearchedMembers(activity: Activity, mailOrPhoneNum: String, assinedTo: ArrayList<String>){
        when(activity){
            is MembersActivity ->{
                mFireStore.collection(Constans.USERS)
                    .whereIn(Constans.ID, assinedTo)
                    .get()
                    .addOnSuccessListener {
                            document ->
                        Log.e(activity.javaClass.simpleName, document.documents.toString())

                        val usersList: ArrayList<User> = ArrayList()

                        for (i in document){
                            val user:User = i.toObject(User::class.java)
                            if (user.email.toString() == mailOrPhoneNum || user.mobile.toString() == mailOrPhoneNum || user.name.toString() == mailOrPhoneNum){
                                usersList.add(user)
                            }
                        }
                        activity.setUpMembersList(usersList)
                    }
                    .addOnFailureListener {
                            exeption->
                        Log.e("error in getAssinedMembersListDetails", exeption.message.toString())
                    }
            }
        }
    }



    fun getMembersDetails(activity: Activity, email: String){
        when(activity) {
            is MembersActivity-> {
                mFireStore.collection(Constans.USERS)
                    .whereEqualTo(Constans.EMAIL,email)
                    .get()
                    .addOnSuccessListener {
                        document ->
                        if (document.size() > 0){
                            val user = document.documents[0].toObject(User::class.java)!!
                            Log.e("user found",user.toString())

                            activity.memberDetails(user)

                        }
                        else{
                            activity.hideProgressDialog()
                            activity.showErrorSnackBar("No such member found")
                        }
                    }.addOnFailureListener {
                        activity.hideProgressDialog()
                        Log.e(activity.javaClass.simpleName,
                        "Error while getting user details")
                    }
            }
            is BoardActivity -> {
                mFireStore.collection(Constans.USERS)
                    .whereEqualTo(Constans.EMAIL,email)
                    .get()
                    .addOnSuccessListener {
                            document ->
                        if (document.size() > 0){
                            val user = document.documents[0].toObject(User::class.java)!!
                            Log.e("user found",user.toString())

                            activity.memberDetails(user)

                        }
                        else{
                            activity.hideProgressDialog()
                            activity.showErrorSnackBar("No such member found")
                        }
                    }.addOnFailureListener {
                        activity.hideProgressDialog()
                        Log.e(activity.javaClass.simpleName,
                            "Error while getting user details")
                    }

            }
        }
    }

    /**
     * A function to assign a updated members list to board.
     */
    fun assignMemberToBoard(activity: MembersActivity, board: Board, user: User) {

        val assignedToHashMap = HashMap<String, Any>()
        assignedToHashMap[Constans.ASSIGNED_TO] = board.assinedTo

        mFireStore.collection(Constans.BOARDS)
            .document(board.documentId)
            .update(assignedToHashMap)
            .addOnSuccessListener {


                Log.e(activity.javaClass.simpleName, "TaskList updated successfully.")
                activity.memberAssinedSuccess(user)
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while creating a board.", e)
            }
    }

    @SuppressLint("LongLogTag")
    fun getAssinedMembersListDetails(activity: Activity,
                                     assinedTo: ArrayList<String>){
        when(activity){
            is MembersActivity ->{
                mFireStore.collection(Constans.USERS)
                    .whereIn(Constans.ID, assinedTo)
                    .get()
                    .addOnSuccessListener {
                        document ->
                        Log.e(activity.javaClass.simpleName, document.documents.toString())

                        val usersList: ArrayList<User> = ArrayList()

                        for (i in document){
                            val user:User = i.toObject(User::class.java)
                            Log.e("getAssinedMembersListDetails", user.toString())
                            usersList.add(user)
                        }

                        activity.setUpMembersList(usersList)
                    }
                    .addOnFailureListener {
                        exeption->
                        Log.e("error in getAssinedMembersListDetails", exeption.message.toString())
                    }
            }
        }
    }

    fun createBoard(activity: BoardActivity, board: Board){
        Log.e("inside Fire base","check the fun")

        mFireStore.collection(Constans.BOARDS)
            .document()
            .set(board, SetOptions.merge())
            .addOnSuccessListener {
                Log.e("OnSuccessListener","check the fun")
                activity.boardCreatedSuccefully()
            }
            .addOnFailureListener {
                    e ->
                Log.e(activity.javaClass.simpleName, "error writing document")
            }

    }

    fun getBoardsList(){
        Log.e("Current uId", getCurrentUid())
        Log.e("ASSIGNED_TO", Constans.ASSIGNED_TO)


        mFireStore.collection(Constans.BOARDS)
            //.whereArrayContains(Constans.ASSIGNED_TO, getCurrentUid())
            .orderBy("timeStamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener {
                document ->
                Log.e("getBoardsList",document.documents.toString()+"inside addOnSuccessListener")
                Log.e("getBoardsList","inside addOnSuccessListener")

                Constans.BOARDS_CHATS_LIST = ArrayList()

                for (i in document.documents){
                    val board = i.toObject(Board::class.java)!!
                    for (j in board.assinedTo) {
                        if (getCurrentUid() == j) {
                            board.documentId = i.id
                            Log.e("documentId", "${i.toObject(Board::class.java)!!}")
                            Log.e("documentId", "${i.id}")
                            Constans.BOARDS_CHATS_LIST.add(board)
                        }
                    }
                }
                Log.e("getBoardsList","${Constans.BOARDS_CHATS_LIST.size.toString()}")
                //activity.hideProgressDialog()

            }.addOnFailureListener {
                e ->
                //activity.hideProgressDialog()
                Log.e("getBoardsList","Error while creating the board"+"inside addOnFailureListener",e)
            }

    }

    fun getBoardsListListener(fragmentChat: fragment_chat){
        mFireStore.collection(Constans.BOARDS)
            .whereArrayContains(Constans.ASSIGNED_TO,getCurrentUid())
            .orderBy("timeStamp", Query.Direction.DESCENDING)
            .addSnapshotListener { value, e ->
                Log.i("addSnapshotListener","getBoardsListListener")
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }

                val boards = ArrayList<Board>()

                for (doc in value!!) {
                    val board = doc.toObject(Board()::class.java)
                    board.documentId = doc.id
                    boards.add(board)

                }
                fragmentChat.updateBoardToUi(boards)
            }
    }

    fun createTask(activity: CreateNewTask, task: Task){
        Log.e("inside Fire base","check the fun")

        mFireStore.collection(Constans.TASK)
            .document()
            .set(task, SetOptions.merge())
            .addOnSuccessListener {
                Log.e("OnSuccessListener","check the fun")
                activity.taskCreatedSuccessfully()
            }
            .addOnFailureListener {
                    e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "error writing document")
            }

    }

    fun getTasksListListener(activity: ChatActivity){
        mFireStore.collection(Constans.TASK)
            .whereEqualTo(Constans.DOCUMENT_ID, Constans.DOCUMENTt_ID)
            .orderBy("timeStamp", Query.Direction.DESCENDING)
            .addSnapshotListener { value, e ->
                Log.i("addSnapshotListener","getTasksListListener")
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }

                val tasks = ArrayList<Task>()

                for (doc in value!!) {
                    val task = doc.toObject(Task()::class.java)
                    task.taskId = doc.id
                    Log.e("isTaskComplited", task.isTaskCompleted.toString())
                    tasks.add(task)
                }
                activity.setUpTaskAdapter(tasks)
            }
    }

   fun uptateTaskCoplited(activity: Activity,TaskHashMap: HashMap<String, Any>,task: Task){
       when(activity) {
           is ChatActivity -> {
               Log.i("updateTimeStamp", TaskHashMap.toString())
               mFireStore.collection(Constans.TASK)
                   .document(task.taskId)
                   .update(TaskHashMap)
                   .addOnSuccessListener {
                       Log.e(activity.javaClass.simpleName, "profile data updated")

                       activity.taskComplitedUpdatedSuccefully()

                   }.addOnFailureListener { e ->
                       Log.e(activity.javaClass.simpleName, "profile data Error")

                   }
           }
       }
   }

    fun getTasksList(activity: ChatActivity){

        mFireStore.collection(Constans.TASK)
            .whereEqualTo(Constans.DOCUMENT_ID, Constans.DOCUMENTt_ID)
            .orderBy("timeStamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener {
                    document ->
                Log.e("getTasksList",document.documents.toString()+"inside addOnSuccessListener")
                Log.e("getTasksList","inside addOnSuccessListener")

                val tasks:ArrayList<Task> = ArrayList()

                for (i in document.documents){
                    val task = i.toObject(Task::class.java)!!
                            tasks.add(task)
                        }
                activity.setUpTaskAdapter(tasks)
            }.addOnFailureListener {
                    e ->
                activity.hideProgressDialog()
                Log.e("getTasksList","Error while creating the board"+"inside addOnFailureListener",e)
            }

    }

    fun deleteChatBoardDocument(fragment: BoardItemsAdapter, documentId: String, position: Int) {
        var items = ArrayList<String>()

        mFireStore.collection(Constans.BOARDS).document(documentId)
            .delete()
            .addOnSuccessListener {

                Log.e("inside", "deleteChatBoardDocument")
                Log.d(TAG, "DocumentSnapshot of Board successfully deleted!")
                fragment.boardDeletedSuccefully(documentId,position)
            }
            .addOnFailureListener { e -> Log.w(TAG, "Error deleting of Board document ", e) }


        for (i in items) {
            mFireStore.collection(Constans.TASK)
                .document(i)
                .delete()
                .addOnSuccessListener {

                    Log.d(TAG, "DocumentSnapshot of Task successfully deleted!")

                }.addOnFailureListener { e -> Log.w(TAG, "Error delte task ", e) }
        }
    }
    fun getTasksAfterBoardDeletedAndDeleteMessages(fragmentChat: BoardItemsAdapter, documentId: String, position: Int) {
        var items = ArrayList<String>()

        mFireStore.collection(Constans.TASK)
            .whereEqualTo(Constans.DOCUMENT_ID, documentId)
            .get()
            .addOnSuccessListener { document ->
                for (i in document) {
                    items.add(i.id)

                    Log.e("itemId", i.id.toString())
                }
                fragmentChat.getTasksAfterBoardDeletedSuccess(items,position)
            }.addOnFailureListener { e -> Log.w(TAG, "Error getting task to delete document", e) }


        mDbRef.child("message").child(documentId)
            .removeValue()


    }

    fun deleteTasksAfterBoardDeleted(
        fragmentChat: BoardItemsAdapter,
        items: ArrayList<String>,
        position: Int
    ) {

        for (i in items) {
            mFireStore.collection(Constans.TASK).document(i)
                .delete()
                .addOnSuccessListener {

                    Log.e(" $i", "deleted")

                }
                .addOnFailureListener { e -> Log.w(TAG, "Error deleting of Board document ", e) }
        }
    }

    fun deleteTaskItem(taskAdapter: TaskAdapter, taskId: String, adapterPosition: Int) {
        mFireStore.collection(Constans.TASK).document(taskId)
            .delete()
            .addOnSuccessListener {

                Log.e("inside", "deleteChatBoardDocument")
                Log.d(TAG, "DocumentSnapshot of Board successfully deleted!")
            }
            .addOnFailureListener { e -> Log.w(TAG, "Error deleting of Board document ", e) }

    }

    fun loadUserData(activity: Activity,readBoardList: Boolean = false){
         Log.e("inside ","loadUserData")
        mFireStore.collection(Constans.USERS)
            .document(getCurrentUid())
            .get()
            .addOnSuccessListener { document ->
                val loggedInUser = document.toObject(User::class.java)

                if (loggedInUser != null) {
                    when(activity){
                        is signInActivity -> {
                            activity.signInSuccess(loggedInUser)
                        }
                        is MainActivity -> {
                            activity.showProgressDialog()

                            activity.updateUserDetails(loggedInUser,readBoardList)

                        }
                        is MyProfileEditActivity -> {
                            activity.setUserDataInUi(loggedInUser)
                        }
                    }
                }
            }
            .addOnFailureListener {
                    e ->
                when(activity){
                    is signInActivity -> {
                        activity.hideProgressDialog()
                    }
                    is MainActivity -> {
                        activity.hideProgressDialog()
                    }
                }
                Log.e("FireS - signInUser", "error writing document")
            }
    }

    fun loadFCMtokensOfAssighnedMemberstosepcificBoard(activity: ChatActivity, mAssignedUserid: java.util.ArrayList<String>){
        mFireStore.collection(Constans.USERS)
            .get().addOnSuccessListener {
                document ->
                val mUersFCM: ArrayList<String> = ArrayList()

                Log.d("inside","loadFCMtokensOfAssighnedMemberstosepcificBoard")
                for (i in document){
                    Log.d("i",i.id)
                    for (j in mAssignedUserid){
                        Log.d("j",j.toString())
                        if (i.id == j) {

                            if (i.id != getCurrentUid()) {
                                val user = i.toObject(User::class.java)
                                mUersFCM.add(user.fcmToken)
                                Log.e("fcm", user.fcmToken)
                            }
                        }
                    }
                }
                activity.getFcmOfAssinedUsersSucess(mUersFCM)
            }
    }

    fun updateTimeStamp(activity: Activity,boardHashMap: HashMap<String, Any>,board: Board){
        Log.d("updateTimeStamp",board.documentId.toString())
        Log.d("updateTimeStamp",boardHashMap.toString())
        when(activity) {
            is ChatActivity -> {
                Log.i("updateTimeStamp", boardHashMap.toString())
                mFireStore.collection(Constans.BOARDS)
                    .document(board.documentId)
                    .update(boardHashMap)
                    .addOnSuccessListener {
                        Log.e(activity.javaClass.simpleName, "profile data updated")

                        activity.bordsOrderUpdated()

                    }.addOnFailureListener { e ->
                        Log.e(activity.javaClass.simpleName, "profile data Error")

                    }
            }
        }

    }

    fun updateUserProfileData(activity: Activity,
                              userHashMap: HashMap<String, Any>,oldName:String,isNameUpdated: Boolean){
        mFireStore.collection(Constans.USERS)
            .document(getCurrentUid())
            .update(userHashMap)
            .addOnSuccessListener {
                Log.e(activity.javaClass.simpleName, "profile data updated")
                Toast.makeText(
                    activity,
                    "Profile uppdated succefully",
                    Toast.LENGTH_LONG
                ).show()
                if (isNameUpdated == true){
                    val boardHashMap = HashMap<String, Any>()

                }
                when(activity){
                    is MainActivity ->{
                        activity.tokenUpdateSuccess()
                    }
                }
            }.addOnFailureListener {
                e ->
                when(activity){
                    is MainActivity ->{
                        activity.hideProgressDialog()
                    }
                    is MyProfileEditActivity ->{
                        activity.hideProgressDialog()
                    }
                }

                Log.e(activity.javaClass.simpleName, "profile data Error")

            }



    }



    fun getCurrentUid(): String{
        val currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserID = ""
        if (currentUser != null){
            currentUserID = currentUser.uid
        }
        return currentUserID
    }




}