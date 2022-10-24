package ee.developments.messagepigeon.activities.activities.models

import android.os.Parcel
import android.os.Parcelable

data class Board (
    val name: String = "",
    val image: String = "",
    val createdBy: String = "",
    val assinedTo: ArrayList<String> = ArrayList(),
    val boardParticipants: ArrayList<String> = ArrayList(),
    var documentId:String = "",
    var timeStamp: String = ""
        ):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createStringArrayList()!!,
        parcel.createStringArrayList()!!,
        parcel.readString()!!,
        parcel.readString()!!
    ) {
    }

    override fun describeContents(): Int {
       return 0
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) = with(parcel) {
        parcel.writeString(name)
        parcel.writeString(image)
        parcel.writeString(createdBy)
        parcel.writeStringList(assinedTo)
        parcel.writeStringList(boardParticipants)
        parcel.writeString(documentId)
        parcel.writeString(timeStamp)
    }

    companion object CREATOR : Parcelable.Creator<Board> {
        override fun createFromParcel(parcel: Parcel): Board {
            return Board(parcel)
        }

        override fun newArray(size: Int): Array<Board?> {
            return arrayOfNulls(size)
        }
    }
}