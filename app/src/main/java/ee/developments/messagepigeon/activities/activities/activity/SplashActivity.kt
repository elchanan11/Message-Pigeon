package ee.developments.messagepigeon.activities.activities.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.projemanag.R
import ee.developments.messagepigeon.activities.activities.firebase.FireStore

class SplashActivity : BaseActivity() {

    /**
     * This function is auto created by Android when the Activity Class is created.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        //This call the parent constructor
        super.onCreate(savedInstanceState)
        // This is used to align the xml view to this class
        setContentView(R.layout.activity_splash)

        // This is used to hide the status bar and make the splash screen as a full screen activity.
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        // Adding the handler to after the a task after some delay.
        Handler().postDelayed({

            var currentUserID = FireStore().getCurrentUid()


            if (currentUserID.isNotEmpty()){
                FireStore().getBoardsList()
                startActivity(Intent(this, MainActivity::class.java))
            }else{
                startActivity(Intent(this, IntroActivity::class.java))
            }

            // Start the Intro Activity
            finish() // Call this when your activity is done and should be closed.
        }, 500) // Here we pass the delay time in milliSeconds after which the splash activity will disappear.
    }
}