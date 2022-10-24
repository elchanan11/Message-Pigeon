package ee.developments.messagepigeon.activities.activities.activity

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.projemanag.R
import com.projemanag.databinding.ActivityMyProfileEditBinding
import ee.developments.messagepigeon.activities.activities.firebase.FireStore
import ee.developments.messagepigeon.activities.activities.models.User
import ee.developments.messagepigeon.activities.activities.utils.Constans
import kotlinx.android.synthetic.main.activity_my_profile_edit.*
import kotlinx.android.synthetic.main.fragment_profile2.*
import kotlinx.android.synthetic.main.fragment_profile2.view.*
import java.io.IOException


class MyProfileEditActivity : BaseActivity() {
    private var binding: ActivityMyProfileEditBinding? = null
    private var mSelectedImageUri: Uri? = null
    private var mProfileImageUrl: String? = null
    private lateinit var mUserDetails: User

    private var oldName: String? = null
    private var isNameUpdated: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyProfileEditBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setupActionBar()

        FireStore().loadUserData(this)

        oldName= et_name.text.toString()

        iv_user_image.setOnClickListener {
            Constans.checkPermissions(this)
        }

        btn_update.setOnClickListener{
            if (mSelectedImageUri != null){
                uploadUserImage()
            }else{
                showProgressDialog()
                updateUserProfileData()
            }
        }
    }

    fun updateUserProfileData(){

        val userHashMap = HashMap<String, Any>()

        var anyChangesMade = false

        if (mProfileImageUrl?.isNotEmpty() == true && mProfileImageUrl != mUserDetails.image){
            userHashMap[Constans.IMAGE] = mProfileImageUrl!!
            anyChangesMade = true
        }
        if (et_name.text.toString() != mUserDetails.name){
            userHashMap[Constans.NAME] = et_name.text.toString()
            isNameUpdated = true
            anyChangesMade = true

        }
        if (et_mobile.text.toString() != mUserDetails.mobile.toString()){
            var mobile = et_mobile.text.toString()
            if (mobile.toString() != "") {
                userHashMap[Constans.MOBILE] = mobile.toLong()
                anyChangesMade = true
            }
        }
        if (anyChangesMade) {
            FireStore().updateUserProfileData(this@MyProfileEditActivity, userHashMap,oldName!!,isNameUpdated)
            hideProgressDialog()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }else{
            hideProgressDialog()
        }
    }

    fun setUserDataInUi(user: ee.developments.messagepigeon.activities.activities.models.User){
        mUserDetails = user

        Glide
            .with(this@MyProfileEditActivity)
            .load(Constans.USER_IMAGE)
            .centerCrop()
            .placeholder(R.drawable.ic_baseline_person_24)
            .into(iv_user_image)

        Constans.USER_IMAGE = user.image
        Constans.USER_NAME = user.name


        et_name.setText(user.name)
        et_email.setText(user.email)
        if (user.mobile != 0L){
            et_mobile.setText(user.mobile.toString())

            Constans.USER_MOBILE = user.mobile.toString()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY && resultCode == Activity.RESULT_OK && data!!.data != null){
            mSelectedImageUri = data.data

            try {
                Constans.USER_IMAGE = mSelectedImageUri.toString()

                Glide
                    .with(this@MyProfileEditActivity)
                    .load(Constans.USER_IMAGE)
                    .centerCrop()
                    .placeholder(R.drawable.ic_baseline_person_24)
                    .into(iv_user_image)
            }catch (e:IOException){
                e.printStackTrace()
            }
        }
    }

    private fun uploadUserImage(){
        showProgressDialog()

        if(mSelectedImageUri != null){
            val sRef: StorageReference = FirebaseStorage.getInstance()
                .reference.child("USER_IMAGE" +
                        System.currentTimeMillis() + "."
                        + getFileExtention(mSelectedImageUri!!))
            sRef.putFile(mSelectedImageUri!!).addOnSuccessListener {
                taskSnapshot ->
                Log.e("Fire base Image URL",
                taskSnapshot.metadata!!.reference!!.downloadUrl.toString())

                taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                    uri->
                    Log.e("Dowloadble Image URL", uri.toString())
                    mProfileImageUrl = uri.toString()

                    hideProgressDialog()
                    updateUserProfileData()

                }
            }.addOnFailureListener{
                exeption ->
                Toast.makeText(
                    this@MyProfileEditActivity,
                    exeption.message,
                    Toast.LENGTH_LONG
                ).show()
                hideProgressDialog()
            }
        }
    }

//return the type of the uri(image,audio, etc.)
     fun getFileExtention(uri: Uri): String?{
        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(contentResolver.getType(uri!!))
    }



    private fun setupActionBar() {

        setSupportActionBar(binding?.toolbarMyProfileActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }

        binding?.toolbarMyProfileActivity?.setNavigationOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }
    }


    companion object{
        var GALLERY = 1
    }
}