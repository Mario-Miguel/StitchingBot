package es.uniovi.eii.stitchingbot.model

import android.os.Parcel
import android.os.Parcelable

data class SewingMachine(
    val id: Int=0,
    var name: String? ="",
    var imgUrl: String? ="",
    var motorSteps: Int=467) : Parcelable{

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeString(imgUrl)
        parcel.writeInt(motorSteps)
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