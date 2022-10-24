package ee.developments.messagepigeon.activities.activities.models

import android.os.Parcel
import android.os.Parcelable
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter.writeString

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val image: String = "",
    val mobile: Long = 0,
    val fcmToken: String = ""
    ):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readLong()!!,
        parcel.readString()!!
    ) {
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel?, flags: Int) = with(dest) {
        this!!.writeString(id)
        this!!.writeString(name)
        this!!.writeString(email)
        this!!.writeString(image)
        this!!.writeString(fcmToken)
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}
