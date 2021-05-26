package es.uniovi.eii.stitchingbot.model

import android.os.Parcel
import android.os.Parcelable

data class SewingMachine(
    val id: Int=0,
    var name: String? ="",
    var imgUrl: String? ="",
    var hasPedal: Boolean=false) : Parcelable{

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readByte() != 0.toByte()
    ) {
    }

    constructor(name: String, imgUrl: String="", hasPedal: Boolean ) : this(0, name, imgUrl, hasPedal)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeString(imgUrl)
        parcel.writeByte(if (hasPedal) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SewingMachine> {
        override fun createFromParcel(parcel: Parcel): SewingMachine {
            return SewingMachine(parcel)
        }

        override fun newArray(size: Int): Array<SewingMachine?> {
            return arrayOfNulls(size)
        }
    }
}