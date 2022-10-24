package ee.developments.messagepigeon.activities.activities.models

import android.app.ActivityManager
import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.RequiresApi

data class Task (
    val taskTitle: String = "",
    val taskDescription: String = "",
    var timeStamp: String = "",
    var documentId:String = "",
    var isTaskCompleted: Boolean = false,
    var taskId:String = "",
//    val assignedTOTask: ArrayList<String> = ArrayList(),
//    val taskFinishers: ArrayList<String> = ArrayList(),
    //val isTaskFinished: Boolean = false

        ):Parcelable{
    @RequiresApi(Build.VERSION_CODES.Q)
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readBoolean(),
        parcel.readString()!!,
//        parcel.createStringArrayList()!!,
//        parcel.createStringArrayList()!!,

    ) {
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun writeToParcel(parcel: Parcel, flags: Int) = with(parcel) {
        parcel.writeString(taskTitle)
        parcel.writeString(taskDescription)
        parcel.writeString(timeStamp)
        parcel.writeString(documentId)
        parcel.writeBoolean(isTaskCompleted)
        parcel.writeString(taskId)



    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Selected_members_from_crateBoard> {
        override fun createFromParcel(parcel: Parcel): Selected_members_from_crateBoard {
            return Selected_members_from_crateBoard(parcel)
        }

        override fun newArray(size: Int): Array<Selected_members_from_crateBoard?> {
            return arrayOfNulls(size)
        }
    }
}