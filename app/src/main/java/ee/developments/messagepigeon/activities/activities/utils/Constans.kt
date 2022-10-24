package ee.developments.messagepigeon.activities.activities.utils

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.provider.Settings
import android.widget.Toast
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat.startActivity
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import ee.developments.messagepigeon.activities.activities.activity.MyProfileEditActivity
import ee.developments.messagepigeon.activities.activities.models.Board
import kotlin.math.acos

object Constans {
    const val USERS: String = "USERS"
    const val TASK: String = "TASK"
    const val BOARDS: String = "BOARDS"

    const val NAME = "name"
    const val IMAGE = "image"
    const val MOBILE = "mobile"
    const val EMAIL = "email"
    const val DOCUMENT_ID = "documentId"
    const val TIME_STAMP = "timeStamp"
    const val TASK_ID = "taskId"
    const val IS_TASK_COMPLITED = "taskCompleted"

    const val ASSIGNED_TO: String = "assinedTo"

    var BOARDS_CHATS_LIST: ArrayList<Board> = ArrayList()

    const val ID: String = "id"

//global variblres
    var USER_NAME = ""
    var USER_IMAGE = ""
    var USER_MOBILE = ""
    var DOCUMENTt_ID = ""

    val CREATE_BOARD_REQUEST_CODE: Int = 12
    val UPDATE_BORARDS_SET_REQUEST_CODE: Int = 13
    val CREATE_NEW_TASK_REQUEST_CODE: Int = 13


    const val BOARD_CHAT_DETAILS = "BOARD DETAILS"

     fun checkPermissions(activity: Activity){
        Dexter.withContext(activity)
            .withPermissions(
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report!!.areAllPermissionsGranted()) {

                        val galleryIntent = Intent(
                            Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        )
                        activity.startActivityForResult(galleryIntent, MyProfileEditActivity.GALLERY)
                    }

                    if (report!!.isAnyPermissionPermanentlyDenied) {
                        Toast.makeText(
                            activity,
                            "Some Permissions not Granted+" +
                                    "You need to grant all pemissions",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest?>?,
                    token: PermissionToken?
                ) {
                    showRationalDialogForPermission(activity)
                }
            }).onSameThread()
            .check()
    }

     fun showRationalDialogForPermission(activity: Activity) {
        AlertDialog.Builder(activity).setMessage(
            "" +
                    "It looks like you have turned off permission required" + "" +
                    "for for this feature. it can be enabled under the" +
                    "Application Settings "
        )
            .setPositiveButton("GO TO SETTINGS")
            { _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", activity.packageName, null)
                    intent.data = uri
                    activity.startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }


            }.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

}