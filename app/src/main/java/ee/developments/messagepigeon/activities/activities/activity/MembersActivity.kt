package ee.developments.messagepigeon.activities.activities.activity

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.projemanag.R
import com.projemanag.databinding.ActivityBoardBinding
import com.projemanag.databinding.ActivityMembersBinding
import ee.developments.messagepigeon.activities.activities.adapters.MembersListItemAdapter
import ee.developments.messagepigeon.activities.activities.firebase.FireStore
import ee.developments.messagepigeon.activities.activities.models.Board
import ee.developments.messagepigeon.activities.activities.models.User
import ee.developments.messagepigeon.activities.activities.utils.Constans
import kotlinx.android.synthetic.main.activity_members.*
import kotlinx.android.synthetic.main.dialog_search_member.*

class MembersActivity : BaseActivity() {

    private lateinit var mBoardChatDetails: Board
    private lateinit var mAssinedMemberList: ArrayList<User>


    private var binding: ActivityMembersBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMembersBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setupActionBar()

        if (intent.hasExtra(Constans.BOARD_CHAT_DETAILS)){
            mBoardChatDetails = intent.getParcelableExtra<Board>(Constans.BOARD_CHAT_DETAILS)!!
        }

        showProgressDialog()
        for (i in mBoardChatDetails.assinedTo){
            Log.e("members assined",i.toString())
        }
        FireStore().getAssinedMembersListDetails(this@MembersActivity,mBoardChatDetails.assinedTo)

        binding?.ivSerch?.setOnClickListener{
            val textSerch:String = binding?.etFindMember?.text.toString()
            showProgressDialog()
            FireStore().getSearchedMembers(this,textSerch,mBoardChatDetails.assinedTo)
        }
        binding?.ivCancelSerch?.setOnClickListener{
            binding?.etFindMember?.text?.clear()
            showProgressDialog()
            FireStore().getAssinedMembersListDetails(this@MembersActivity,mBoardChatDetails.assinedTo)
        }
    }

    fun memberDetails(user: User){
        val userId = user.id
        val assinedTo = mBoardChatDetails.assinedTo
        var isAccountExists = false

        for (i in assinedTo) {
            val usrsAssienedId = i
            if (i == userId) {
                isAccountExists = true
            }
        }
        hideProgressDialog()
        if (isAccountExists){
            showErrorSnackBar("You cant assign an assigned account")
        }else{
            mBoardChatDetails.assinedTo.add(user.id)
            FireStore().assignMemberToBoard(this, mBoardChatDetails, user)
        }
    }

    fun setUpMembersList(list: ArrayList<User>){

        mAssinedMemberList = list
        hideProgressDialog()

        rv_members_list.layoutManager = LinearLayoutManager(this)
        rv_members_list.setHasFixedSize(true)

        val adapter = MembersListItemAdapter(this, list)
        rv_members_list.adapter = adapter

    }

    /**
     * A function for actionBar Setup.
     */
    private fun setupActionBar() {

        setSupportActionBar(binding?.toolbarAddMembersActivity)
        title = "MEMBERS"

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }

        binding?.toolbarAddMembersActivity?.setNavigationOnClickListener { onBackPressed() }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_member, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar menu items
        when (item.itemId) {
            R.id.action_add_member -> {

                // START
                dialogSearchMember()
                // END
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
    // END

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
                    this@MembersActivity,
                    "Please enter members email address.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
        dialog.tv_cancel.setOnClickListener(View.OnClickListener {
            dialog.dismiss()
        })
        //Start the dialog and display it on screen.
        dialog.show()
    }
    fun memberAssinedSuccess(user:User){
        mAssinedMemberList.add(user)
        setUpMembersList(mAssinedMemberList)
    }
}