package ee.developments.messagepigeon.activities.activities.activity

import android.app.Activity
import android.app.Dialog
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.projemanag.R
import com.projemanag.databinding.ActivityBoardBinding
import ee.developments.messagepigeon.activities.activities.adapters.BoardItemsAdapter
import ee.developments.messagepigeon.activities.activities.adapters.Selected_members_from_board_adapter
import ee.developments.messagepigeon.activities.activities.firebase.FireStore
import ee.developments.messagepigeon.activities.activities.models.Board
import ee.developments.messagepigeon.activities.activities.models.Selected_members_from_crateBoard
import ee.developments.messagepigeon.activities.activities.models.User
import ee.developments.messagepigeon.activities.activities.utils.Constans
import kotlinx.android.synthetic.main.activity_board.*
import kotlinx.android.synthetic.main.dialog_search_member.*
import java.io.IOException
import java.time.Instant
import java.time.format.DateTimeFormatter

class BoardActivity : BaseActivity() {

    private var binding: ActivityBoardBinding? = null
    private var mSelectedImageUri: Uri? = null
    private var mBoardImageUrl: String? = ""
    private lateinit var mUserName: String
    var assiignedUsersArrayList: ArrayList<String> = ArrayList()
    var boardsParticipantsList: ArrayList<String> = ArrayList()
    var isAdapterInitialized: Boolean = false
    var selectedMembers: ArrayList<Selected_members_from_crateBoard> = ArrayList()

    private val mFireStore = FirebaseFirestore.getInstance()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBoardBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setupActionBar()

        setUpMembersList()
        mUserName = Constans.USER_NAME

        iv_board_image.setOnClickListener{
            Constans.checkPermissions(this)
        }
        btn_create.setOnClickListener{
            if(mSelectedImageUri != null){
                Log.e("inside","if")
                showProgressDialog()
                uploadBoardImage()
            }else {
                showProgressDialog()
                Log.e("inside", "else")

                createBoard()
            }
        }

    }

    fun setUpMembersList(){

        if(isAdapterInitialized == false){
            selectedMembers.add(Selected_members_from_crateBoard("", ""))
        }
            rv_add_members_list_from_board.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            rv_add_members_list_from_board.setHasFixedSize(true)

            val adapter = Selected_members_from_board_adapter(this, selectedMembers)
            rv_add_members_list_from_board.adapter = adapter
            isAdapterInitialized = true

            adapter.setOnClickListener(object :
                Selected_members_from_board_adapter.OnClickListener {
                override fun onClick() {
                    Log.e("setApMembersList", "ItemClikced")
                    dialogSearchMember()
                }
            })

    }

    private fun dialogSearchMember() {
        val dialog = Dialog(this)
        /*Set the screen content from a layout resource.
    The resource will be inflated, adding all top-level views to the screen.*/
        dialog.setContentView(R.layout.dialog_search_member)
        dialog.tv_add.setOnClickListener(View.OnClickListener {

            val email = dialog.et_email_search_member.text.toString()

            if (email.isNotEmpty()) {
                dialog.dismiss()
                showProgressDialog()
                FireStore().getMembersDetails(this, email)
            } else {
                Toast.makeText(
                    this@BoardActivity,
                    "Please enter members email address.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
        dialog.tv_cancel.setOnClickListener(View.OnClickListener {
            dialog.dismiss()
        })
        dialog.show()
    }

    fun memberDetails(user: User){
        val id = user.id
        val image = user.image
        var j = 0

        for (i in selectedMembers) {
            val selcedtedMemberId = i.id
            j++
            if (id != selcedtedMemberId) {
                Log.e("inside for", i.toString())
                if (id != getCurrentUserID()) {
                    if (j == selectedMembers.size) {
                        selectedMembers.add(Selected_members_from_crateBoard(id, image))
                        boardsParticipantsList.add(user.name)
                        setUpMembersList()
                    }
                }else{
                    showErrorSnackBar("You cant your account to the chat!!")
                }
                //FireStore().assignMemberToBoard(this,mBoardChatDetails,user)
            }else{
                showErrorSnackBar("The account is already added!!")
            }
            hideProgressDialog()
        }
    }

    fun boardCreatedSuccefully(){

        FireStore().getBoardsList()

        hideProgressDialog()

        setResult(Activity.RESULT_OK)
        finish()

    }
    private fun createBoard(){
        Log.e("inside", "createBoard1")
        //showProgressDialog()

        assiignedUsersArrayList.add(getCurrentUserID())

        for (i in selectedMembers){
            //Log.e(" for assaned to", "${i.id}")
            if (i.id.isNotEmpty()){
                assiignedUsersArrayList.add(i.id)
                Log.e(" for assaned to", "${i.id}")
            }
        }

        val timeStamp  = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            DateTimeFormatter.ISO_INSTANT.format(Instant.now())
        } else {
            ""
        }
        Log.e("time stamp", timeStamp)
        var board = Board(
            et_board_name.text.toString(),
            mBoardImageUrl.toString(),
            mUserName.toString(),
            assiignedUsersArrayList,
            boardsParticipantsList,
            "",
            timeStamp
            )
        FireStore().createBoard(this, board)
    }

    private fun uploadBoardImage(){

            if(mSelectedImageUri != null){
                val sRef: StorageReference = FirebaseStorage.getInstance()
                    .reference.child("BOARD_IMAGE" +
                            System.currentTimeMillis() + "."
                            + getFileExtention(mSelectedImageUri!!))
                sRef.putFile(mSelectedImageUri!!).addOnSuccessListener {
                        taskSnapshot ->
                    Log.e("Board Image URL",
                        taskSnapshot.metadata!!.reference!!.downloadUrl.toString())

                    taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                            uri->
                        Log.e("Dowloadble Image URL", uri.toString())
                        mBoardImageUrl = uri.toString()

                        //hideProgressDialog()
                            Log.e("mSelectedImageUri", "not empty")

                        createBoard()
                    }

                }.addOnFailureListener{
                        exeption ->
                    Toast.makeText(
                        this@BoardActivity,
                        exeption.message,
                        Toast.LENGTH_LONG
                    ).show()
                    hideProgressDialog()
                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MyProfileEditActivity.GALLERY && resultCode == Activity.RESULT_OK && data!!.data != null){
            mSelectedImageUri = data.data

            try {
                Constans.USER_IMAGE = mSelectedImageUri.toString()

                Glide
                    .with(this)
                    .load(mSelectedImageUri)
                    .centerCrop()
                    .placeholder(R.drawable.ic_baseline_person_24)
                    .into(iv_board_image)
            }catch (e: IOException){
                e.printStackTrace()
            }
        }
    }

    fun getFileExtention(uri: Uri): String?{
        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(contentResolver.getType(uri!!))
    }

    /**
     * A function for actionBar Setup.
     */
    private fun setupActionBar() {

        setSupportActionBar(binding?.toolbarCreateBoardActivity)
        title = "CREATE CHAT"

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }

        binding?.toolbarCreateBoardActivity?.setNavigationOnClickListener { startActivity(Intent(this, MainActivity::class.java)) }
    }
}