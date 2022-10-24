package ee.developments.messagepigeon.activities.activities.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import com.projemanag.R
import com.projemanag.databinding.ActivityCreateNewTaskBinding
import ee.developments.messagepigeon.activities.activities.firebase.FireStore
import ee.developments.messagepigeon.activities.activities.models.Task
import ee.developments.messagepigeon.activities.activities.utils.Constans
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.activity_create_new_task.*
import java.time.Instant
import java.time.format.DateTimeFormatter

class CreateNewTask : BaseActivity() {

    var binding :ActivityCreateNewTaskBinding? = null
    lateinit var task: Task

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateNewTaskBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        task = Task()

        setupActionBar()

        binding?.btnAddTaskCreate?.setOnClickListener{
            val titleTask = binding?.etAddTaskTitle?.text.toString()
            val decriptionTask = binding?.etAddTaskDecription?.text.toString()
            val timeStamp  = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                DateTimeFormatter.ISO_INSTANT.format(Instant.now())
            } else {
                ""
            }

            task = Task(titleTask,decriptionTask,timeStamp,Constans.DOCUMENTt_ID.toString())

            showProgressDialog()
            FireStore().createTask(this, task)
        }
    }

    fun taskCreatedSuccessfully() {
        hideProgressDialog()

        val intent = Intent()
        Log.d("task",task.taskTitle)
        intent.putExtra("task",task)
        setResult(RESULT_OK,intent)

        finish()
    }

    private fun setupActionBar() {

        setSupportActionBar(toolbar_create_chat_activity)
        title = "CREATE TASK"

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }

        toolbar_create_chat_activity?.setNavigationOnClickListener { startActivity(Intent(this, MainActivity::class.java)) }
    }
}