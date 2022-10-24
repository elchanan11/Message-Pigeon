package ee.developments.messagepigeon.activities.activities.models

import android.os.Parcel
import android.os.Parcelable

data class Selected_members_from_crateBoard (
    val id: String = "",
    val image: String = ""
        ):Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) = with(parcel) {
        parcel.writeString(id)
        parcel.writeString(image)
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