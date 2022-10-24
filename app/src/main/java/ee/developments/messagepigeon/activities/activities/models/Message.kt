package ee.developments.messagepigeon.activities.activities.models

import android.os.Parcel
import android.os.Parcelable

class Message{
    var message: String? = null
    var senderId: String? = null
    var documentId: String? = null
    var time: String? = null
    var senderName: String? = null

    constructor(){}

    constructor(message: String?, senderId: String?, documentId: String?, time: String?, senderName: String?){
        this.message = message
        this.senderId = senderId
        this.documentId = documentId
        this.time = time
        this.senderName = senderName
    }
}