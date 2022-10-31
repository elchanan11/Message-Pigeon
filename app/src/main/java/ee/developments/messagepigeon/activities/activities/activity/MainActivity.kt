package ee.developments.messagepigeon.activities.activities.activity

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceIdReceiver
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.projemanag.R
import com.projemanag.databinding.ActivityMainBinding
import ee.developments.messagepigeon.activities.activities.firebase.FireStore
import ee.developments.messagepigeon.activities.activities.fragments.fragment_chat
import ee.developments.messagepigeon.activities.activities.fragments.fragment_profile
import ee.developments.messagepigeon.activities.activities.models.User
import ee.developments.messagepigeon.activities.activities.utils.Constans
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity(),View.OnClickListener {

    lateinit var bottomNav : BottomNavigationView
    private var binding: ActivityMainBinding? = null
    private lateinit var mUserName: String
    private val mFireStore = FirebaseFirestore.getInstance()
    private lateinit var mSharedprefences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        mSharedprefences = this.getSharedPreferences(Constans.MESSAGEPIEGON_PREFERNCES,Context.MODE_PRIVATE )

        val tokenUpdated = mSharedprefences.getBoolean(Constans.FCM_TOKEN_UPDATED, false)
        Log.e("tokenUpdated: ", tokenUpdated.toString())

        if (tokenUpdated){
            //showProgressDialog()
            FireStore().loadUserData(this, true)

        }else{
            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new FCM registration token
                val token = task.result
                Log.e("token",task.result.toString())

                updateFcmToken(token)

            }).addOnCanceledListener {
                Log.e("set token","fail")
            }
        }

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

        }else{
            hideProgressDialog()
        }

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

                mSharedprefences.edit().clear().apply()

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

    }

    fun tokenUpdateSuccess() {
        hideProgressDialog()
        val editor: SharedPreferences.Editor = mSharedprefences.edit()
        editor.putBoolean(Constans.FCM_TOKEN_UPDATED,true)
        editor.apply()
        showProgressDialog()
        FireStore().loadUserData(this,true)
    }

    private fun updateFcmToken(token: String){
        val userHashMap = HashMap<String, Any>()
        userHashMap[Constans.FCM_TOKEN] = token
        showProgressDialog()
        FireStore().updateUserProfileData(this,userHashMap,"",false)
    }
}