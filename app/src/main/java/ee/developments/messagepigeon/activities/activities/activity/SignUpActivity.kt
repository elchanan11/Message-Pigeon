package ee.developments.messagepigeon.activities.activities.activity

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.projemanag.R
import com.projemanag.databinding.ActivitySignUpBinding
import ee.developments.messagepigeon.activities.activities.firebase.FireStore
import ee.developments.messagepigeon.activities.activities.models.User

class SignUpActivity : BaseActivity(),View.OnClickListener {
    private var binding: ActivitySignUpBinding? = null
    private var btnSignApp:Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        btnSignApp = findViewById(R.id.btn_sign_up)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        setupActionBar()

        btnSignApp?.setOnClickListener(this)
    }

    /**
     * A function for actionBar Setup.
     */
    private fun setupActionBar() {

        setSupportActionBar(binding?.toolbarSignUpActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }

        binding?.toolbarSignUpActivity?.setNavigationOnClickListener { onBackPressed() }
    }

    private fun registerUser(){
        val name: String = binding?.etName?.text.toString().trim { it <= ' ' }
        val email: String = binding?.etEmail?.text.toString().trim { it <= ' ' }
        val password: String = binding?.etPassword?.text.toString().trim { it <= ' ' }

        if (validateForm(name, email, password)) {

            showProgressDialog()

            FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword(email,password)
                . addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val firebaseUser: FirebaseUser = task.result!!.user!!
                        val registerEmail = firebaseUser.email!!
                        val user  = User(firebaseUser.uid, name, registerEmail)

                        FireStore().registerUser(this, user)

                    }else{
                        Toast.makeText(
                            this,
                            task.exception!!.message,
                            Toast.LENGTH_LONG
                        )
                            .show()
                        hideProgressDialog()
                    }
                }
        }
    }
    // END

    // TODO (Step 10: A function to validate the entries of a new user.)
    // START
    /**
     * A function to validate the entries of a new user.
     */
    private fun validateForm(name: String, email: String, password: String): Boolean {
        return when {
            TextUtils.isEmpty(name) -> {
                showErrorSnackBar("Please enter name.")
                false
            }
            TextUtils.isEmpty(email) -> {
                showErrorSnackBar("Please enter email.")
                false
            }
            TextUtils.isEmpty(password) -> {
                showErrorSnackBar("Please enter password.")
                false
            }
            else -> {
                true
            }
        }
    }

    fun userRegisterSuccess(){
        Toast.makeText(
            this,
            " you have succecfully registered the email adress ",
            Toast.LENGTH_LONG

        )
            .show()
        hideProgressDialog()
        FirebaseAuth.getInstance().signOut()
        finish()
    }

    override fun onClick(v: View?) {
        Log.e("inside onClick","b")

        registerUser()
    }
}